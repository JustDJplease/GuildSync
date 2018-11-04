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
    HashMap<String, String> chatPrefix;
    ApiFetcher apiFetcher;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        apiFetcher = new ApiFetcher(this);
        chatPrefix = new HashMap<>();
        startRunner(this);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        getCommand("guild").setExecutor(new GuildCommand(this));
    }

    @Override
    public void onDisable() {
        data = null;
        apiFetcher = null;
        chatPrefix.clear();
        chatPrefix = null;
    }

    private void startRunner(GuildSync guildSync) {
        getServer().getScheduler().runTaskTimer(this, () -> apiFetcher.updateLiveDataAsync(), 20L, 12000L);
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

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) {
                p.sendMessage("§7§o[Server: Refreshed " + data.size() + " ranks]");
            }

            String name = p.getName();
            PermissionUser permissionUser = PermissionsEx.getUser(p);

            if (data == null || chatPrefix.isEmpty()) {
                clearGroups(permissionUser);
                permissionUser.addGroup(getGroupName("UNRANKED"));
                return;
            }

            if (getConfig().contains("overrides." + name + ".chat")) {
                clearGroups(permissionUser);
                permissionUser.addGroup(getConfig().getString("overrides." + name + ".permission-group"));
                return;
            }

            if (!chatPrefix.containsKey(name)) {
                clearGroups(permissionUser);
                permissionUser.addGroup(getGroupName("UNRANKED"));
                return;
            }

            String rank = chatPrefix.get(name);
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
}
