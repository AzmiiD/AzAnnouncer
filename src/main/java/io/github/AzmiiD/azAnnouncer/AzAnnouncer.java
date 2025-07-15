package io.github.AzmiiD.azAnnouncer;

import io.github.AzmiiD.azAnnouncer.config.ConfigManager;
import io.github.AzmiiD.azAnnouncer.task.AnnouncementManager;
import io.github.AzmiiD.azAnnouncer.util.MessageUtil;
import io.github.AzmiiD.azAnnouncer.command.AzAnnouncerCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class AzAnnouncer extends JavaPlugin {

    private static AzAnnouncer instance;
    private ConfigManager configManager;
    private AnnouncementManager announcementManager;
    private BukkitAudiences adventure;
    private boolean placeholderAPIAvailable;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize Adventure
        this.adventure = BukkitAudiences.create(this);

        // Check if PlaceholderAPI is available
        placeholderAPIAvailable = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholderAPIAvailable) {
            getLogger().info("PlaceholderAPI found! Placeholder support enabled.");
        } else {
            getLogger().info("PlaceholderAPI not found. Placeholders will not be parsed.");
        }

        // Initialize managers
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        announcementManager = new AnnouncementManager(this);
        announcementManager.startAnnouncements();

        // Register command
        new AzAnnouncerCommand(this);

        getLogger().info("AzAnnouncer has been enabled!");
    }

    @Override
    public void onDisable() {
        if (announcementManager != null) {
            announcementManager.stopAnnouncements();
        }

        if (adventure != null) {
            adventure.close();
        }

        getLogger().info("AzAnnouncer has been disabled!");
    }

    public void reload() {
        // Stop current announcements
        if (announcementManager != null) {
            announcementManager.stopAnnouncements();
        }

        // Reload configs
        configManager.loadConfigs();

        // Restart announcements
        announcementManager.startAnnouncements();
    }

    public static AzAnnouncer getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AnnouncementManager getAnnouncementManager() {
        return announcementManager;
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }

    public boolean isPlaceholderAPIAvailable() {
        return placeholderAPIAvailable;
    }

    public String getPrefix() {
        String prefix = configManager.getMainConfig().getString("prefix", "&e&lAzAnnouncer &7>> ");
        return MessageUtil.colorize(prefix);
    }
}
