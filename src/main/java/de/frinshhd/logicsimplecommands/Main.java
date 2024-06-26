package de.frinshhd.logicsimplecommands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    private static final Map<String, Inventory> inventories = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        loadConfig();

        registerCommands();
    }

    public void loadConfig() {
        FileConfiguration config = this.getConfig();

        //inventories
        List<Map<?, ?>> inventories = config.getMapList("inventories");

        inventories.forEach(inventory -> {
            if (inventory.get("id") == null) {
                return;
            }

            Inventory inv = new Inventory(inventory);

            Main.inventories.put((String) inventory.get("id"), inv);
        });

        //commands
        List<Map<?, ?>> commands = config.getMapList("commands");

        commands.forEach(command -> {
            SimpleCommand simpleCommand = new SimpleCommand(command);

            if (!simpleCommand.isCorrect()) {
                return;
            }

            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                commandMap.getKnownCommands().remove(simpleCommand.getName());

                commandMap.register(Main.getInstance().getName(), simpleCommand);
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[DynamicCommands] Error registering command " + simpleCommand + " " + e);
                return;
            }
        });

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }
    }

    public void registerCommands() {
        // main command
        Bukkit.getPluginCommand("lsc").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
                if (args.length == 0) {
                    return false;
                }

                switch (args[0]) {
                    case "reload":
                        if (!sender.hasPermission("lsc.reload")) {
                            return false;
                        }

                        Main.getInstance().reloadConfig();
                        loadConfig();
                        sender.sendMessage("§aConfig reloaded.");
                        return true;
                    default:
                        return false;
                }
            }
        });
        Bukkit.getPluginCommand("lsc").setTabCompleter(new TabCompleter() {
            @Override
            public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
                List<String> completions = new ArrayList<>();

                if (args.length <= 1) {

                    List<String> possibleArguments = new ArrayList<>(List.of(
                            "reload"
                    ));

                    possibleArguments.forEach(possibleArgument -> {
                        if (possibleArgument.startsWith(args[0].toLowerCase())) {
                            completions.add(possibleArgument);
                        }
                    });
                }

                return completions;
            }
        });

    }

    public static Inventory getInventory(String invID) {
        return inventories.get(invID);
    }

}
