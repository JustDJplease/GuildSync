package me.honeyblu.guildsync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Guild extends JavaPlugin {

    JsonArray data;
    final HashMap<String, String> chatPrefix = new HashMap<>();
    Getter getter;
    private Guild instance;

    @Override
    public void onEnable() {
        instance = this;
        getter = new Getter();
        startRunner();
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("guild").setExecutor(new GuildCommand(this));
    }

    private void startRunner() {
        getServer().getScheduler().runTaskTimer(this, () -> getter.updateLiveDataAsync(instance), 20L, 12000L);
    }

    @Override
    public void onDisable() {
        data = null;
        getter = null;
        instance = null;
    }

    void forceUpdateAll() {
        chatPrefix.clear();
        for (int n = 0; n < data.size(); n++) {
            JsonElement entry = data.get(n);
            JsonObject object = entry.getAsJsonObject();
            String name = object.get("name").getAsString();
            String rank = object.get("rank").getAsString();
            chatPrefix.put(name, rank);
        }
        getLogger().info("Synchronised with the Wynncraft Live Server! (Refreshed " + data.size() + " ranks)");
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.isOp()){
                p.sendMessage("§7§o[Server: Refreshed " + data.size() + " ranks]");
            }
        }
    }
}
