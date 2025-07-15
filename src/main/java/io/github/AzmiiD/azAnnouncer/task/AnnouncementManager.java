package io.github.AzmiiD.azAnnouncer.task;

import io.github.AzmiiD.azAnnouncer.AzAnnouncer;
import io.github.AzmiiD.azAnnouncer.config.Announcement;
import io.github.AzmiiD.azAnnouncer.util.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AnnouncementManager {

    private final AzAnnouncer plugin;
    private final Map<Integer, Announcement> announcements;
    private final Map<Integer, BukkitTask> announcementTasks;
    private final MiniMessage miniMessage;

    public AnnouncementManager(AzAnnouncer plugin) {
        this.plugin = plugin;
        this.announcements = new HashMap<>();
        this.announcementTasks = new HashMap<>();
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void startAnnouncements() {
        // Load announcements from config
        announcements.clear();
        announcements.putAll(plugin.getConfigManager().loadAnnouncements());

        // Start tasks for each announcement
        for (Announcement announcement : announcements.values()) {
            startAnnouncementTask(announcement);
        }
    }

    public void stopAnnouncements() {
        // Cancel all tasks
        for (BukkitTask task : announcementTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        announcementTasks.clear();
    }

    private void startAnnouncementTask(Announcement announcement) {
        // Convert seconds to ticks (20 ticks = 1 second)
        long intervalTicks = announcement.getInterval() * 20L;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            sendAnnouncementToAll(announcement);
        }, intervalTicks, intervalTicks);

        announcementTasks.put(announcement.getId(), task);
    }

    public void sendAnnouncementToAll(Announcement announcement) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendAnnouncementToPlayer(announcement, player);
        }
    }

    public void sendAnnouncementToPlayer(Announcement announcement, Player player) {
        Audience audience = plugin.getAdventure().player(player);

        for (String message : announcement.getMessages()) {
            // Process placeholders if PlaceholderAPI is available
            String processedMessage = message;
            if (plugin.isPlaceholderAPIAvailable()) {
                processedMessage = PlaceholderAPI.setPlaceholders(player, message);
            }

            // Convert hex colors and legacy colors
            processedMessage = MessageUtil.processHexColors(processedMessage);
            processedMessage = MessageUtil.convertLegacyToMiniMessage(processedMessage);

            // Parse with MiniMessage and send
            Component component = miniMessage.deserialize(processedMessage);
            audience.sendMessage(component);
        }

        // Play sound if configured
        if (announcement.hasSound()) {
            try {
                Sound sound = Sound.valueOf(announcement.getSound().toUpperCase());
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid sound in announcement " + announcement.getId() + ": " + announcement.getSound());
            }
        }
    }

    public Announcement getAnnouncement(int id) {
        return announcements.get(id);
    }

    public Set<Integer> getAnnouncementIds() {
        return announcements.keySet();
    }

    public Collection<Announcement> getAllAnnouncements() {
        return announcements.values();
    }
}