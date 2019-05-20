package dev.swiss.kix;

import co.aikar.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class ExtendedBaseCommand extends BaseCommand {

    protected static void sendMsg(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    protected static String heading(String message) {
        return message;
    }

}
