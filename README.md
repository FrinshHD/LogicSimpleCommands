# LogicSimpleCommands Plugin

LogicSimpleCommands is a versatile and easy-to-use plugin for managing custom commands and inventories in your Minecraft server. This plugin allows server administrators to define commands and inventories directly from a configuration file, making it simple to customize and extend server functionality without needing to write additional code.

## Features

- **Dynamic Command Registration**: Define commands in `config.yml` and have them automatically registered when the server starts.
- **Custom Inventories**: Create and manage custom inventories with items, actions, and more, all configurable via `config.yml`.
- **Permissions Support**: Assign permissions to commands to control access.
- **Aliases**: Define aliases for commands to provide multiple ways to execute the same command.
- **Reloadable Configuration**: Easily reload the configuration without restarting the server using the `/lsc reload` command.

## Configuration

### Commands

Define your commands in the `config.yml` file:

```yaml
commands:
  - command: "discord"
    description: "Join our Discord server"
    usage: "/discord"
    aliases: ["dc"]
    permission: "lsc.discord"
    text: "Join our Discord server at discord.gg/example"
  - command: "store"
    description: "Get the Store link."
    usage: "/store"
    permission: "lsc.shop"
    text: "Visit our shop at example.com/store"
  - command: "links"
    description: "View all our links"
    usage: "/links"
    permission: "lsc.links"
    inventory: "links"
```
