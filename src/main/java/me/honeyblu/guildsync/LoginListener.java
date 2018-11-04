package me.honeyblu.guildsync;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

class LoginListener implements Listener {

    private GuildSync guildSync;

    LoginListener(GuildSync guildSync) {
        this.guildSync = guildSync;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        PermissionUser permissionUser = PermissionsEx.getUser(player);

        if (guildSync.data == null || guildSync.chatPrefix.isEmpty()) {
            player.sendMessage("§4Still fetching your Wynncraft rank... One moment!");
            player.sendMessage("§4Your permissions will be updated as soon as possible.");
            clearGroups(permissionUser);
            permissionUser.addGroup(getGroupName("UNRANKED"));
            return;
        }

        if (guildSync.getConfig().contains("overrides." + name + ".chat")) {
            clearGroups(permissionUser);
            permissionUser.addGroup(guildSync.getConfig().getString("overrides." + name + ".permission-group"));
            return;
        }

        if (!guildSync.chatPrefix.containsKey(name)) {
            clearGroups(permissionUser);
            permissionUser.addGroup(getGroupName("UNRANKED"));
            return;
        }

        String rank = guildSync.chatPrefix.get(name);
        clearGroups(permissionUser);
        permissionUser.addGroup(getGroupName(rank));
    }

    private void clearGroups(PermissionUser permissionUser) {
        for (PermissionGroup group : permissionUser.getGroups()) {
            permissionUser.removeGroup(group);
        }
    }

    private String getGroupName(String s) {
        return guildSync.getConfig().getString("rank." + s + ".permission-group");
    }
}
