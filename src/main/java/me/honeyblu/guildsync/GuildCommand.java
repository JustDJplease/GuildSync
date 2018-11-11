package me.honeyblu.guildsync;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class GuildCommand implements CommandExecutor {

    private final GuildSync guildSync;

    GuildCommand(GuildSync guildSync) {
        this.guildSync = guildSync;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage("§cYou do not have permission.");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("update")) {
                commandSender.sendMessage("§aRequesting synchronisation... One moment please!");
                guildSync.apiFetcher.updateLiveGuildDataAsync();
                return true;
            }
        }
        commandSender.sendMessage("§2Available commands:");
        commandSender.sendMessage("§a/" + label + " update §8-§7 Tries to update to the live Wynncraft ranks!");
        return true;
    }
}
