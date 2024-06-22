package de.frinshhd.logicsimplecommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class SimpleCommand extends Command {

    protected boolean correct = true;
    private String text;

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
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (getPermission() != null && !commandSender.hasPermission(getPermission())) {
            return false;
        }

        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNullElse(text, "No text defined.")));
        return true;
    }

    public boolean isCorrect() {
        return correct;
    }
}
