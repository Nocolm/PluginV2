package fr.openmc.core.commands.utils;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import fr.openmc.core.utils.messages.MessagesManager.Message;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class Spawn {

    @Command("spawn")
    @Description("Permet de se rendre au spawn")
    @CommandPermission("omc.commands.spawn")
    public void spawn(CommandSender sender, @Default("me") Player target) {
        
        Location spawnLocation = SpawnManager.getInstance().getSpawnLocation();

        if(sender instanceof Player player && player == target) {
            player.teleport(spawnLocation);
            MessagesManager.sendMessageType(player, "§aVous avez été envoyé au spawn", Prefix.OPENMC, MessageType.SUCCESS, true);
        } else {
            if(!(sender instanceof Player) || ((Player) sender).hasPermission("omc.admin.commands.spawn.others")) {
                target.teleport(spawnLocation);
                MessagesManager.sendMessageType(sender, "§aVous avez envoyé §e" + target.getName() + "§a au spawn", Prefix.OPENMC, MessageType.SUCCESS, true);
                MessagesManager.sendMessageType(target, "§aVous avez été envoyé par §e" + (sender instanceof Player player ? player.getName() : "Console") + "§a au spawn", Prefix.OPENMC, MessageType.WARNING, true);
            } else {
                MessagesManager.sendMessageType(sender, Message.NOPERMISSION.getMessage(), Prefix.OPENMC, MessageType.ERROR, true);
            }
        }
    }
}