package de.frinshhd.logicsimplecommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static LinkInventory linkInventory;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        loadConfig();
        linkInventory = new LinkInventory();
    }

    public void loadConfig() {
        FileConfiguration config = this.getConfig();
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

                commandMap.register(Main.getInstance().getName(), simpleCommand);
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("[DynamicCommands] Error registering command " + simpleCommand + " " + e);
                return;
            }
        });


    }

}
