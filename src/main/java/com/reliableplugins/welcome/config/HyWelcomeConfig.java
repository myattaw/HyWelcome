package com.reliableplugins.welcome.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HyWelcomeConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public Settings settings = new Settings();
    public Messages messages = new Messages();
    public Title title = new Title();

    public static final class Settings {
        public boolean enabled = true;
        public boolean firstJoinMessage = true;
        public boolean titleOnJoin = true;
    }

    public static final class Messages {
        public String join = "<green>[+]</green> <gold>Welcome <bold>{player}</bold>!</gold>";
        public String leave = "<red>[-]</red> <gold><bold>{player}</bold> left the server.</gold>";
        public String firstJoin = "<green>[+]</green> <gold><bold>{player}</bold> joined for the first time!</gold>";

        public String titleMessage = "Welcome";
        public String titleSubMessage = "Enjoy your stay!";
    }

    public static final class Title {
        public double fadeInSeconds = 1.2;
        public double staySeconds = 3.0;
        public double fadeOutSeconds = 1.2;
        public boolean playSound = false;
    }

    /** Static factory (load or create default file) */
    public static HyWelcomeConfig loadOrCreate(Path configPath) {
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                HyWelcomeConfig cfg = new HyWelcomeConfig();
                cfg.save(configPath);
                return cfg;
            }

            try (Reader reader = Files.newBufferedReader(configPath)) {
                HyWelcomeConfig cfg = GSON.fromJson(reader, HyWelcomeConfig.class);
                return (cfg == null) ? new HyWelcomeConfig() : cfg;
            }

        } catch (Exception e) {
            e.printStackTrace();
            HyWelcomeConfig fallback = new HyWelcomeConfig();
            try {
                fallback.save(configPath);
            } catch (Exception ignored) {}
            return fallback;
        }
    }

    public void save(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(this, writer);
        }
    }

}
