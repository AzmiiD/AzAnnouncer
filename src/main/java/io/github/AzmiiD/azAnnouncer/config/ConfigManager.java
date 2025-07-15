package io.github.AzmiiD.azAnnouncer.config;

import io.github.AzmiiD.azAnnouncer.AzAnnouncer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final AzAnnouncer plugin;
    private FileConfiguration mainConfig;
    private FileConfiguration announcementsConfig;
    private File announcementsFile;

    public ConfigManager(AzAnnouncer plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // Load main config
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        mainConfig = plugin.getConfig();

        // Load announcements config
        createAnnouncementsConfig();
        reloadAnnouncementsConfig();
    }

    private void createAnnouncementsConfig() {
        announcementsFile = new File(plugin.getDataFolder(), "announcements.yml");
        if (!announcementsFile.exists()) {
            announcementsFile.getParentFile().mkdirs();
            plugin.saveResource("announcements.yml", false);
        }
    }

    private void reloadAnnouncementsConfig() {
        announcementsConfig = YamlConfiguration.loadConfiguration(announcementsFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource("announcements.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            announcementsConfig.setDefaults(defConfig);
        }
    }

    public Map<Integer, Announcement> loadAnnouncements() {
        Map<Integer, Announcement> announcements = new HashMap<>();

        ConfigurationSection section = announcementsConfig.getConfigurationSection("announcements");
        if (section == null) {
            plugin.getLogger().warning("No announcements found in announcements.yml!");
            return announcements;
        }

        for (String key : section.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                ConfigurationSection announcementSection = section.getConfigurationSection(key);

                if (announcementSection == null) continue;

                List<String> messages = announcementSection.getStringList("message");
                int interval = announcementSection.getInt("interval", 60);
                String sound = announcementSection.getString("sound", "");

                if (messages.isEmpty()) {
                    plugin.getLogger().warning("Announcement " + id + " has no messages!");
                    continue;
                }

                Announcement announcement = new Announcement(id, messages, interval, sound);
                announcements.put(id, announcement);

            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid announcement ID: " + key);
            }
        }

        plugin.getLogger().info("Loaded " + announcements.size() + " announcements.");
        return announcements;
    }

    public FileConfiguration getMainConfig() {
        return mainConfig;
    }

    public FileConfiguration getAnnouncementsConfig() {
        return announcementsConfig;
    }

    public void saveAnnouncementsConfig() {
        try {
            announcementsConfig.save(announcementsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save announcements.yml!");
            e.printStackTrace();
        }
    }
}