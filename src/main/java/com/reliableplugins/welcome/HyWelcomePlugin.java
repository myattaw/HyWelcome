package com.reliableplugins.welcome;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.packets.interface_.ServerMessage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.reliableplugins.welcome.config.HyWelcomeConfig;
import com.reliableplugins.welcome.util.TinyMsg;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HyWelcomePlugin extends JavaPlugin {

    private HyWelcomeConfig config;
    private Path configPath;

    // Prevent duplicate join triggers
    private static final long JOIN_COOLDOWN_MS = 1000;
    private final Map<UUID, Long> lastJoinMessageTime = new ConcurrentHashMap<>();

    // Cached "first join ever" detection result between connect -> ready
    private final Map<UUID, Boolean> firstJoinCache = new ConcurrentHashMap<>();

    public HyWelcomePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {

        // load config.json
        this.configPath = getDataDirectory().resolve("config.json");
        this.config = HyWelcomeConfig.loadOrCreate(configPath);

        // Suppress default join/leave world system messages
        PacketAdapters.registerOutbound((PacketFilter) (_, packet) -> {
            if (!(packet instanceof ServerMessage msgPacket)) return false;
            if (msgPacket.message == null) return false;

            String id = msgPacket.message.messageId;
            if (id == null) return false;

            return id.equals("server.general.playerJoinedWorld")
                    || id.equals("server.general.playerLeftWorld");
        });

        // first join detection
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);

        // join / leave actions
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerLeave);
    }

    /**
     * Detects "first join ever" by checking the player's stored ECS data.
     */
    private void onPlayerConnect(PlayerConnectEvent event) {
        UUID uuid = event.getPlayerRef().getUuid();

        Universe.get().getPlayerStorage().load(uuid).whenComplete((holder, throwable) -> {

            // If load fails or missing -> assume first join
            if (throwable != null || holder == null) {
                firstJoinCache.put(uuid, true);
                return;
            }

            boolean firstJoin = false;

            try {
                if (holder.getArchetype() == null || holder.getArchetype().isEmpty()) {
                    firstJoin = true;
                } else {
                    UUIDComponent uuidComponent = holder.getComponent(UUIDComponent.getComponentType());
                    if (uuidComponent == null) {
                        firstJoin = true;
                    }
                }
            } catch (Throwable t) {
                // safest fallback
                firstJoin = true;
            }

            firstJoinCache.put(uuid, firstJoin);
        });
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        if (!config.settings.enabled) return;

        Player player = event.getPlayer();

        Ref<EntityStore> ref = event.getPlayerRef();
        PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        UUID uuid = playerRef.getUuid();

        // cooldown
        long now = System.currentTimeMillis();
        Long last = lastJoinMessageTime.get(uuid);
        if (last != null && now - last < JOIN_COOLDOWN_MS) return;
        lastJoinMessageTime.put(uuid, now);

        // "first join ever" result
        boolean isFirstJoin = firstJoinCache.getOrDefault(uuid, false);
        firstJoinCache.remove(uuid);

        String rawJoinMsg = (isFirstJoin && config.settings.firstJoinMessage)
                ? config.messages.firstJoin
                : config.messages.join;

        String joinMsg = rawJoinMsg.replace("{player}", player.getDisplayName());

        playerRef.sendMessage(TinyMsg.parse(joinMsg));

        // show title
        if (config.settings.titleOnJoin) {
            String titleRaw = config.messages.titleMessage.replace("{player}", player.getDisplayName());
            String subRaw = config.messages.titleSubMessage.replace("{player}", player.getDisplayName());

            Message titleMsg = TinyMsg.parse(titleRaw);
            Message subMsg = TinyMsg.parse(subRaw);

            EventTitleUtil.showEventTitleToPlayer(
                    playerRef,
                    subMsg,
                    titleMsg,
                    true,
                    null,
                    (float) config.title.staySeconds,
                    (float) config.title.fadeInSeconds,
                    (float) config.title.fadeOutSeconds
            );
        }
    }

    private void onPlayerLeave(PlayerDisconnectEvent event) {
        if (!config.settings.enabled) return;

        PlayerRef playerRef = event.getPlayerRef();
        if (playerRef == null) return;

        String raw = config.messages.leave;
        String msg = raw.replace("{player}", playerRef.getUsername());

        playerRef.sendMessage(TinyMsg.parse(msg));

        // cleanup
        UUID uuid = playerRef.getUuid();
        firstJoinCache.remove(uuid);
        lastJoinMessageTime.remove(uuid);
    }
}
