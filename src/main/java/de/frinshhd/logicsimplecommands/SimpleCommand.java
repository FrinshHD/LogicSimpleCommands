package de.frinshhd.logicsimplecommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class SimpleCommand extends Command {

    protected boolean correct = true;
    private String text;
    private CommandTypes type;

    protected SimpleCommand(Map<?, ?> map) {
        super((String) map.get("command"));

        if (map.containsKey("description")) {
            setDescription((String) map.get("description"));
        }

        if (map.containsKey("usage")) {
            setUsage((String) map.get("usage"));
        }

        if (map.containsKey("permission")) {
            setPermission((String) map.get("permission"));
        }

        if (map.containsKey("aliases")) {
            setAliases((ArrayList<String>) map.get("aliases"));
        }

        if (map.containsKey("type")) {
            type = CommandTypes.valueOf((String) map.get("type"));
        } else {
            type = CommandTypes.SIMPLE;
        }

        if (map.containsKey("text")) {
            this.text = (String) map.get("text");
        } else {
            text = null;
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            return false;
        }

        switch (type) {
            case SIMPLE:
                sender.sendMessage(MessageFormat.build(Objects.requireNonNullElse(text, "No text defined.")));
                return true;
            case LINK_INVENTORY:
                if (!(sender instanceof Player player)) {
                    return false;
                }

                Main.linkInventory.openInventory(player);
                return true;
            default:
                return false;
        }
    }

    public boolean isCorrect() {
        return correct;
    }
}

enum CommandTypes {
    SIMPLE,
    LINK_INVENTORY
}
