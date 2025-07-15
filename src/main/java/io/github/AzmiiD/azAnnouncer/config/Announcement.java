package io.github.AzmiiD.azAnnouncer.config;

import java.util.List;

public class Announcement {

    private final int id;
    private final List<String> messages;
    private final int interval;
    private final String sound;

    public Announcement(int id, List<String> messages, int interval, String sound) {
        this.id = id;
        this.messages = messages;
        this.interval = interval;
        this.sound = sound;
    }

    public int getId() {
        return id;
    }

    public List<String> getMessages() {
        return messages;
    }

    public int getInterval() {
        return interval;
    }

    public String getSound() {
        return sound;
    }

    public boolean hasSound() {
        return sound != null && !sound.isEmpty();
    }
}
