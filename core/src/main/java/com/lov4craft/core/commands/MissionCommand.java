package com.lov4craft.core.commands;

import com.lov4craft.core.LOV4CraftCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MissionCommand implements CommandExecutor, TabCompleter {
    private final LOV4CraftCore plugin;
    private final List<String> subCommands = Arrays.asList("list", "info", "accept", "abandon", "progress");

    public MissionCommand(LOV4CraftCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                // TODO: Implement mission list logic
                player.sendMessage(ChatColor.GOLD + "=== Available Missions ===");
                player.sendMessage(ChatColor.YELLOW + "Mission system coming soon!");
                break;
            case "info":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /mission info <id>");
                    return true;
                }
                // TODO: Implement mission info logic
                player.sendMessage(ChatColor.YELLOW + "Mission info system coming soon!");
                break;
            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /mission accept <id>");
                    return true;
                }
                // TODO: Implement mission accept logic
                player.sendMessage(ChatColor.YELLOW + "Mission accept system coming soon!");
                break;
            case "abandon":
                // TODO: Implement mission abandon logic
                player.sendMessage(ChatColor.YELLOW + "Mission abandon system coming soon!");
                break;
            case "progress":
                // TODO: Implement mission progress logic
                player.sendMessage(ChatColor.YELLOW + "Mission progress system coming soon!");
                break;
            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Mission Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/mission list " + ChatColor.WHITE + "- List available missions");
        player.sendMessage(ChatColor.YELLOW + "/mission info <id> " + ChatColor.WHITE + "- Show mission details");
        player.sendMessage(ChatColor.YELLOW + "/mission accept <id> " + ChatColor.WHITE + "- Accept a mission");
        player.sendMessage(ChatColor.YELLOW + "/mission abandon " + ChatColor.WHITE + "- Abandon current mission");
        player.sendMessage(ChatColor.YELLOW + "/mission progress " + ChatColor.WHITE + "- Show mission progress");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return filterCompletions(subCommands, args[0]);
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "info":
                case "accept":
                    // TODO: Return list of available mission IDs
                    break;
            }
        }

        return completions;
    }

    private List<String> filterCompletions(List<String> completions, String partial) {
        List<String> filtered = new ArrayList<>();
        String partialLower = partial.toLowerCase();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(partialLower)) {
                filtered.add(completion);
            }
        }
        return filtered;
    }
}
