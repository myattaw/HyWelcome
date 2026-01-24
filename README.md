# HyWelcome Plugin

HyWelcome is a simple and customizable welcome plugin for Hytale servers. It provides a warm and personalized experience for players when they join or leave your server. With features like customizable messages, first join notifications, and welcome titles, HyWelcome enhances the player experience on your server.

## Features
- Disable vanilla join/leave messages.
- Customizable join and leave messages.
- Special messages for first-time players.
- Welcome titles with customizable fade-in, stay, and fade-out durations.
- Optional sound effects for titles.
- Easy-to-configure settings via `config.json`.

## Installation

1. Download the latest release from the [Releases](https://github.com/myattaw/) page.
2. Place the `HyWelcome` plugin file into your server's `plugins` folder.
3. Start your Hytale server to generate the default configuration file.
4. Stop the server and navigate to the `config.json` file located in the `HyWelcome` plugin folder.
5. Customize the settings and messages to your liking.
6. Restart your server to apply the changes.

## Configuration

The plugin's behavior can be customized through the `config.json` file. Below is an example of the default configuration:

```json
{
  "settings": {
    "enabled": true,
    "firstJoinMessage": true,
    "titleOnJoin": true
  },
  "messages": {
    "join": "<green>[+]</green> <gold>The player <bold>{player}</bold> has joined the server!</gold>",
    "leave": "<red>[-]</red> <gold>The player <bold>{player}</bold> has left the server!</gold>",
    "firstJoin": "<green>[+]</green> <gold>The player <bold>{player}</bold> joined for the first time!</gold>",
    "titleMessage": "Welcome",
    "titleSubMessage": "Enjoy your stay, {player}!"
  },
  "title": {
    "fadeInSeconds": 1.2,
    "staySeconds": 3.0,
    "fadeOutSeconds": 1.2,
    "playSound": false
  }
}
```

### Configuration Options

- **Settings**:
  - `enabled`: Enable or disable the plugin.
  - `firstJoinMessage`: Show a special message for first-time players.
  - `titleOnJoin`: Display a welcome title when a player joins.

- **Messages**:
  - `join`: Message displayed when a player joins the server.
  - `leave`: Message displayed when a player leaves the server.
  - `firstJoin`: Message displayed when a player joins the server for the first time.
  - `titleMessage`: The main title displayed to players upon joining.
  - `titleSubMessage`: The subtitle displayed to players upon joining.

- **Title**:
  - `fadeInSeconds`: Duration of the fade-in effect for the title.
  - `staySeconds`: Duration the title stays on the screen.
  - `fadeOutSeconds`: Duration of the fade-out effect for the title.
  - `playSound`: Whether to play a sound when the title is displayed.

## Usage

Once the plugin is installed and configured:

1. Start your Hytale server.
2. Players will see the configured join, leave, and first join messages in the chat.
3. If enabled, players will also see a welcome title upon joining the server.

## Credits

This plugin uses the [TinyMessage](https://github.com/Zoltus/TinyMessage) library for advanced chat formatting. Special thanks to [Zoltus](https://github.com/Zoltus) for their work on this library.

## Contributing

Contributions are welcome! Feel free to fork this repository, make changes, and submit a pull request. Please ensure your code follows the project's coding standards.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please open an issue on the [GitHub repository](https://github.com/myattaw/).
