package com.lov4craft.core.commands;

import com.lov4craft.core.LOV4CraftCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class LOV4CraftCommand implements CommandExecutor, TabCompleter {
    private final LOV4CraftCore plugin;

    public LOV4CraftCommand(LOV4CraftCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                showHelp(sender);
                break;
            case "reload":
                if (!sender.hasPermission("lov4craft.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "LOV4CRAFT configuration reloaded!");
                break;
            case "version":
                sender.sendMessage(ChatColor.GOLD + "LOV4CRAFT Version: " + plugin.getDescription().getVersion());
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Type /lov4craft help for help.");
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== LOV4CRAFT Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/lov4craft help " + ChatColor.WHITE + "- Show this help message");
        if (sender.hasPermission("lov4craft.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/lov4craft reload " + ChatColor.WHITE + "- Reload the plugin configuration");
        }
        sender.sendMessage(ChatColor.YELLOW + "/lov4craft version " + ChatColor.WHITE + "- Show plugin version");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("version");
            if (sender.hasPermission("lov4craft.admin")) {
                completions.add("reload");
            }
            return filterCompletions(completions, args[0]);
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
