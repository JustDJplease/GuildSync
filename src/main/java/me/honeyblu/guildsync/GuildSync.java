package me.honeyblu.guildsync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.HashMap;

public class GuildSync extends JavaPlugin {

    JsonArray data;
    HashMap<String, String> chatRanks;
    HashMap<String, WynncraftPlayer> playerData;
    ApiFetcher apiFetcher;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        apiFetcher = new ApiFetcher(this);
        chatRanks = new HashMap<>();
        playerData = new HashMap<>();
        startRunner();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        getCommand("guild").setExecutor(new GuildCommand(this));
        forceUpdateAllPlayers();
    }

    private void forceUpdateAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            requestUpdatePlayer(p.getName());
        }
    }

    @Override
    public void onDisable() {
        data = null;
        apiFetcher = null;
        chatRanks.clear();
        chatRanks = null;
        playerData.clear();
        playerData = null;
    }

    private void startRunner() {
        getServer().getScheduler().runTaskTimer(this, () -> apiFetcher.updateLiveGuildDataAsync(), 20L, 12000L);
    }

    void forceUpdateAll() {
        chatRanks.clear();

        for (int n = 0; n < data.size(); n++) {
            JsonElement entry = data.get(n);
            JsonObject object = entry.getAsJsonObject();
            String name = object.get("name").getAsString();
            String rank = object.get("rank").getAsString();
            chatRanks.put(name, rank);
        }

        getLogger().info("Synchronised with the Wynncraft Live Server! (Refreshed " + data.size() + " ranks)");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) {
                p.sendMessage("§7§o[Server: Refreshed " + data.size() + " ranks]");
            }

            String name = p.getName();
            PermissionUser permissionUser = PermissionsEx.getUser(p);

            if (data == null || chatRanks.isEmpty()) {
                clearGroups(permissionUser);
                permissionUser.addGroup(getGroupName("UNRANKED"));
                return;
            }

            if (getConfig().contains("overrides." + name + ".chat")) {
                clearGroups(permissionUser);
                permissionUser.addGroup(getConfig().getString("overrides." + name + ".permission-group"));
                return;
            }

            if (!chatRanks.containsKey(name)) {
                clearGroups(permissionUser);
                permissionUser.addGroup(getGroupName("UNRANKED"));
                return;
            }

            String rank = chatRanks.get(name);
            clearGroups(permissionUser);
            permissionUser.addGroup(getGroupName(rank));
        }
    }

    private void clearGroups(PermissionUser permissionUser) {
        for (PermissionGroup group : permissionUser.getGroups()) {
            permissionUser.removeGroup(group);
        }
    }

    private String getGroupName(String s) {
        return getConfig().getString("rank." + s + ".permission-group");
    }

    void updatePlayer(String name, JsonObject data) {
        unloadPlayer(name);
        WynncraftPlayer wynncraftPlayer = new WynncraftPlayer(this, data);
        getLogger().info("Updated Wynncraft rank for player " + name + ".");
        playerData.put(name, wynncraftPlayer);
    }

    void requestUpdatePlayer(String name) {
        apiFetcher.updateLivePlayerDataAsync(name);
    }

    void unloadPlayer(String name) {
        playerData.remove(name);
    }
}
