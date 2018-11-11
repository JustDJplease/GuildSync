package me.honeyblu.guildsync;

import com.google.gson.JsonObject;
import org.bukkit.ChatColor;

class WynncraftPlayer {
    private String rank;
    private boolean veteran;
    private GuildSync guildSync;

    WynncraftPlayer(GuildSync guildSync, JsonObject json) {
        this.guildSync = guildSync;
        rank = json.get("rank").getAsString().toUpperCase();
        veteran = json.get("veteran").getAsBoolean();
    }

    String getPrefix() {
        if (guildSync.getConfig().contains("gamerank." + rank + ".prefix")) {
            if (rank.equalsIgnoreCase("PLAYER") && veteran) {
                return getFormatted("VETERAN");
            }
            return getFormatted(rank);
        }
        return "";
    }

    private String getFormatted(String rank) {
        return ChatColor.translateAlternateColorCodes('&', guildSync.getConfig().getString("gamerank." + rank + ".prefix"));
    }
}
