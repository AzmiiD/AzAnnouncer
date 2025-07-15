package io.github.AzmiiD.azAnnouncer.command;

import io.github.AzmiiD.azAnnouncer.AzAnnouncer;
import io.github.AzmiiD.azAnnouncer.config.Announcement;
import io.github.AzmiiD.azAnnouncer.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AzAnnouncerCommand implements CommandExecutor, TabCompleter {

    private final AzAnnouncer plugin;
    private final String reloadCommand;
    private final List<String> reloadAliases;
    private final String sendCommand;

    public AzAnnouncerCommand(AzAnnouncer plugin) {
        this.plugin = plugin;

        // Load command names from config
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        this.reloadCommand = config.getString("commands.reload", "azannouncer reload");
        this.reloadAliases = config.getStringList("commands.reload-aliases");
        this.sendCommand = config.getString("commands.send", "azannouncer send");

        // Register main command
        PluginCommand command = plugin.getCommand("azannouncer");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }

        // Register aliases
        for (String alias : reloadAliases) {
            PluginCommand aliasCommand = plugin.getCommand(alias);
            if (aliasCommand != null) {
                aliasCommand.setExecutor(this);
                aliasCommand.setTabCompleter(this);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Handle direct alias commands (like /rl)
        if (reloadAliases.contains(label.toLowerCase())) {
            return handleReload(sender);
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Handle reload
        if (subCommand.equals("reload") || reloadAliases.contains(subCommand)) {
            return handleReload(sender);
        }

        // Handle send
        if (subCommand.equals("send")) {
            return handleSend(sender, args);
        }

        sendHelp(sender);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("azannouncer.reload")) {
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&cYou don't have permission to use this command!"));
            return true;
        }

        plugin.reload();
        sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&aConfiguration reloaded successfully!"));
        return true;
    }

    private boolean handleSend(CommandSender sender, String[] args) {
        if (!sender.hasPermission("azannouncer.send")) {
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&cYou don't have permission to use this command!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&cUsage: /azannouncer send <id> <player|all>"));
            return true;
        }

        String idStr = args[1];
        String target = args[2];

        // Parse announcement ID
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&cInvalid announcement ID!"));
            return true;
        }

        // Get announcement
        Announcement announcement = plugin.getAnnouncementManager().getAnnouncement(id);
        if (announcement == null) {
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&cAnnouncement with ID " + id + " not found!"));
            return true;
        }

        // Send announcement
        if (target.equalsIgnoreCase("all")) {
            plugin.getAnnouncementManager().sendAnnouncementToAll(announcement);
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&aAnnouncement sent to all players!"));
        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&cPlayer not found!"));
                return true;
            }

            plugin.getAnnouncementManager().sendAnnouncementToPlayer(announcement, player);
            sender.sendMessage(MessageUtil.colorize(plugin.getPrefix() + "&aAnnouncement sent to " + player.getName() + "!"));
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageUtil.colorize("&e&l=== AzAnnouncer Help ==="));
        sender.sendMessage(MessageUtil.colorize("&f/azannouncer reload &7- Reload configuration"));
        sender.sendMessage(MessageUtil.colorize("&f/azannouncer send <id> <player|all> &7- Send announcement"));
        sender.sendMessage(MessageUtil.colorize("&f/rl &7- Reload configuration (alias)"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("send");
            completions.addAll(reloadAliases);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            // Add all announcement IDs
            completions.addAll(plugin.getAnnouncementManager().getAnnouncementIds()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList()));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("send")) {
            completions.add("all");
            // Add online player names
            completions.addAll(Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
        }

        // Filter based on what's already typed
        String currentArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg))
                .collect(Collectors.toList());
    }
}
