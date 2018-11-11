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

        if (guildSync.data == null || guildSync.chatRanks.isEmpty()) {
            player.sendMessage("§4Still fetching your Guild rank... One moment!");
            Bukkit.broadcastMessage("§7" + name + "§8: §f" + message);
            return;
        }

        String prefix;
        if (!guildSync.playerData.containsKey(name)) {
            player.sendMessage("§4Still fetching your Wynncraft rank... One moment!");
            prefix = "";
        } else {
            WynncraftPlayer wynncraftPlayer = guildSync.playerData.get(name);
            if (wynncraftPlayer == null) prefix = "";
            else prefix = wynncraftPlayer.getPrefix();
        }

        if (guildSync.getConfig().contains("overrides." + name + ".chat")) {
            Bukkit.broadcastMessage(getChat("overrides." + name + ".chat").replace("{player}", name).replace("{message}", message).replace("{rank}", prefix));
            return;
        }

        if (!guildSync.chatRanks.containsKey(player.getName())) {
            Bukkit.broadcastMessage(getChat("rank.UNRANKED.chat").replace("{player}", name).replace("{message}", message).replace("{rank}", prefix));
            return;
        }

        String rank = guildSync.chatRanks.get(player.getName());
        Bukkit.broadcastMessage(getChat("rank." + rank + ".chat").replace("{player}", name).replace("{message}", message).replace("{rank}", prefix));
    }

    private String getChat(String s) {
        return ChatColor.translateAlternateColorCodes('&', guildSync.getConfig().getString(s));
    }
}
