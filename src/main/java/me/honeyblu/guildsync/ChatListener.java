package me.honeyblu.guildsync;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

class ChatListener implements Listener {

    private final Guild guild;

    ChatListener(Guild guild) {
        this.guild = guild;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (guild.data == null || guild.chatPrefix.isEmpty()) {
            player.sendMessage("§4Still fetching live Wynncraft data! Your rank may §cnot§4 be accurate.");
            Bukkit.broadcastMessage("§7[§oLoading...§7] " + player.getName() + "§8: §f" + message);
            return;
        }
        if(!guild.chatPrefix.containsKey(player.getName())){
            Bukkit.broadcastMessage("§6[§eTourist§6] " + player.getName() + "§8: §f" + message);
            return;
        }
        if(player.getName().equals("HoneyBlu")){
            Bukkit.broadcastMessage("§2[§aArchitect§2] " + player.getName() + "§8: §f" + message);
            return;
        }

        String rank = guild.chatPrefix.get(player.getName());
        switch(rank){
            case "RECRUIT":
                Bukkit.broadcastMessage("§3[§bRecruit§3] " + player.getName() + "§8: §f" + message);
                return;
            case "CHIEF":
                Bukkit.broadcastMessage("§3[§bChief§3] " + player.getName() + "§8: §f" + message);
                return;
            case "CAPTAIN":
                Bukkit.broadcastMessage("§3[§bCaptain§3] " + player.getName() + "§8: §f" + message);
                return;
            case "RECRUITER":
                Bukkit.broadcastMessage("§3[§bRecruiter§3] " + player.getName() + "§8: §f" + message);
                return;
            case "OWNER":
                Bukkit.broadcastMessage("§4[§cEmpress§4] " + player.getName() + "§8: §f" + message);
                return;
            default:
        }
    }
}
