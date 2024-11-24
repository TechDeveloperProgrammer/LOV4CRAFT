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

public class CoupleCommand implements CommandExecutor, TabCompleter {
    private final LOV4CraftCore plugin;
    private final List<String> subCommands = Arrays.asList("invite", "accept", "decline", "break", "home", "sethome");

    public CoupleCommand(LOV4CraftCore plugin) {
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
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /couple invite <player>");
                    return true;
                }
                // TODO: Implement invite logic
                player.sendMessage(ChatColor.YELLOW + "Couple invite system coming soon!");
                break;
            case "accept":
                // TODO: Implement accept logic
                player.sendMessage(ChatColor.YELLOW + "Couple accept system coming soon!");
                break;
            case "decline":
                // TODO: Implement decline logic
                player.sendMessage(ChatColor.YELLOW + "Couple decline system coming soon!");
                break;
            case "break":
                // TODO: Implement break logic
                player.sendMessage(ChatColor.YELLOW + "Couple break system coming soon!");
                break;
            case "home":
                // TODO: Implement home teleport logic
                player.sendMessage(ChatColor.YELLOW + "Couple home system coming soon!");
                break;
            case "sethome":
                // TODO: Implement sethome logic
                player.sendMessage(ChatColor.YELLOW + "Couple sethome system coming soon!");
                break;
            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Couple Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/couple invite <player> " + ChatColor.WHITE + "- Invite player to form a couple");
        player.sendMessage(ChatColor.YELLOW + "/couple accept " + ChatColor.WHITE + "- Accept a couple invitation");
        player.sendMessage(ChatColor.YELLOW + "/couple decline " + ChatColor.WHITE + "- Decline a couple invitation");
        player.sendMessage(ChatColor.YELLOW + "/couple break " + ChatColor.WHITE + "- Break up with your couple");
        player.sendMessage(ChatColor.YELLOW + "/couple home " + ChatColor.WHITE + "- Teleport to couple home");
        player.sendMessage(ChatColor.YELLOW + "/couple sethome " + ChatColor.WHITE + "- Set couple home location");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return filterCompletions(subCommands, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            return null; // Return null to show online players
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
