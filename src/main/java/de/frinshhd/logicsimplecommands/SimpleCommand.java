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
    private final String text;
    private final String inventory;

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

        if (map.containsKey("text")) {
            this.text = (String) map.get("text");
        } else {
            text = null;
        }

        if (map.containsKey("inventory")) {
            this.inventory = (String) map.get("inventory");
        } else {
            inventory = null;
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            return false;
        }

        if (inventory != null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
                return true;
            }

            Player player = (Player) sender;

            if (Main.getInventory(inventory) == null) {
                player.sendMessage(ChatColor.RED + "Inventory not found.");
            }

            Main.getInventory(inventory).openInventory(player);
        }

        if (text == null && inventory == null) {
            return false;
        } else if (text == null) {
            return true;
        }

        sender.sendMessage(MessageFormat.build(text));
        return true;
    }

    public boolean isCorrect() {
        return correct;
    }
}
