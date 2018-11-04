package me.honeyblu.guildsync;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

class ChatListener implements Listener {

    private final GuildSync guildSync;

    ChatListener(GuildSync guildSync) {
        this.guildSync = guildSync;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String name = player.getName();
        String message = event.getMessage();

        if (guildSync.data == null || guildSync.chatPrefix.isEmpty()) {
            player.sendMessage("§4Still fetching your Wynncraft rank... One moment!");
            Bukkit.broadcastMessage("§7" + name + "§8: §f" + message);
            return;
        }

        if (guildSync.getConfig().contains("overrides." + name + ".chat")) {
            Bukkit.broadcastMessage(getChat("overrides." + name + ".chat").replace("{player}", name).replace("{message}", message));
            return;
        }

        if (!guildSync.chatPrefix.containsKey(player.getName())) {
            Bukkit.broadcastMessage(getChat("rank.UNRANKED.chat").replace("{player}", name).replace("{message}", message));
            return;
        }

        String rank = guildSync.chatPrefix.get(player.getName());
        Bukkit.broadcastMessage(getChat("rank." + rank + ".chat").replace("{player}", name).replace("{message}", message));
    }

    private String getChat(String s) {
        return ChatColor.translateAlternateColorCodes('&', guildSync.getConfig().getString(s));
    }
}
