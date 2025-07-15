package io.github.AzmiiD.azAnnouncer.util;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fk-orA-FK-OR])");

    /**
     * Colorize a message with legacy color codes
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Process hex colors in the format &#RRGGBB to MiniMessage format
     */
    public static String processHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = "<color:#" + hexColor + ">";
            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Convert legacy color codes to MiniMessage format
     */
    public static String convertLegacyToMiniMessage(String message) {
        // First, handle the special cases for formatting codes
        message = message.replace("&l", "<bold>");
        message = message.replace("&L", "<bold>");
        message = message.replace("&m", "<strikethrough>");
        message = message.replace("&M", "<strikethrough>");
        message = message.replace("&n", "<underlined>");
        message = message.replace("&N", "<underlined>");
        message = message.replace("&o", "<italic>");
        message = message.replace("&O", "<italic>");
        message = message.replace("&k", "<obfuscated>");
        message = message.replace("&K", "<obfuscated>");
        message = message.replace("&r", "<reset>");
        message = message.replace("&R", "<reset>");

        // Handle color codes
        message = message.replace("&0", "<black>");
        message = message.replace("&1", "<dark_blue>");
        message = message.replace("&2", "<dark_green>");
        message = message.replace("&3", "<dark_aqua>");
        message = message.replace("&4", "<dark_red>");
        message = message.replace("&5", "<dark_purple>");
        message = message.replace("&6", "<gold>");
        message = message.replace("&7", "<gray>");
        message = message.replace("&8", "<dark_gray>");
        message = message.replace("&9", "<blue>");
        message = message.replace("&a", "<green>");
        message = message.replace("&A", "<green>");
        message = message.replace("&b", "<aqua>");
        message = message.replace("&B", "<aqua>");
        message = message.replace("&c", "<red>");
        message = message.replace("&C", "<red>");
        message = message.replace("&d", "<light_purple>");
        message = message.replace("&D", "<light_purple>");
        message = message.replace("&e", "<yellow>");
        message = message.replace("&E", "<yellow>");
        message = message.replace("&f", "<white>");
        message = message.replace("&F", "<white>");

        return message;
    }
}