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

public class WalletCommand implements CommandExecutor, TabCompleter {
    private final LOV4CraftCore plugin;
    private final List<String> subCommands = Arrays.asList("link", "balance", "withdraw", "history");

    public WalletCommand(LOV4CraftCore plugin) {
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
            case "link":
                // TODO: Implement wallet linking logic
                player.sendMessage(ChatColor.GOLD + "=== Wallet Linking ===");
                player.sendMessage(ChatColor.YELLOW + "To link your wallet:");
                player.sendMessage(ChatColor.WHITE + "1. Open your Web3 wallet (MetaMask, Trust Wallet)");
                player.sendMessage(ChatColor.WHITE + "2. Sign the verification message");
                player.sendMessage(ChatColor.WHITE + "3. Wait for confirmation");
                player.sendMessage(ChatColor.YELLOW + "Wallet linking system coming soon!");
                break;
            case "balance":
                // TODO: Implement balance check logic
                player.sendMessage(ChatColor.GOLD + "=== Wallet Balance ===");
                player.sendMessage(ChatColor.YELLOW + "Balance check system coming soon!");
                break;
            case "withdraw":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /wallet withdraw <amount>");
                    return true;
                }
                // TODO: Implement withdrawal logic
                player.sendMessage(ChatColor.YELLOW + "Withdrawal system coming soon!");
                break;
            case "history":
                // TODO: Implement transaction history logic
                player.sendMessage(ChatColor.GOLD + "=== Transaction History ===");
                player.sendMessage(ChatColor.YELLOW + "Transaction history system coming soon!");
                break;
            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Wallet Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/wallet link " + ChatColor.WHITE + "- Link your crypto wallet");
        player.sendMessage(ChatColor.YELLOW + "/wallet balance " + ChatColor.WHITE + "- Check your balance");
        player.sendMessage(ChatColor.YELLOW + "/wallet withdraw <amount> " + ChatColor.WHITE + "- Withdraw your rewards");
        player.sendMessage(ChatColor.YELLOW + "/wallet history " + ChatColor.WHITE + "- View transaction history");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return filterCompletions(subCommands, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("withdraw")) {
            completions.add("<amount>");
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
