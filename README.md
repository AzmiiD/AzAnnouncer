# 📢 AzAnnouncer - Automatic Announcement Plugin

AzAnnouncer is a lightweight, configurable, and easy-to-use automatic announcer plugin for Spigot/Paper Minecraft servers.

Designed to deliver custom messages in the chat on a timed interval, AzAnnouncer keeps your server active with regular messages like rules, vote reminders, and server tips — all while supporting sound effects and fully customizable settings.

----

## ✨ Features

- 💬 Sends timed chat announcements
- 🎵 Plays customizable sound effects with each message
- 📁 Separate files for config and announcements (`config.yml`, `announcements.yml`)
- ⏱ Customizable interval between messages
- 🎨 Chat color codes support (`&6`, `&a`, etc.)
- 🧩 Supports simple placeholders (e.g. `{player}`)
- ⚙️ No external dependencies

---

## ⚙️ Configuration

### `config.yml`

```yaml
prefix: '&6[Announcement] '
interval: 300
sound: 'minecraft:entity.experience_orb.pickup'
