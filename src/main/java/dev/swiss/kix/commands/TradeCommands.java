package dev.swiss.kix.commands;

import co.aikar.commands.annotation.CommandAlias;
import dev.swiss.kix.ExtendedBaseCommand;
import dev.swiss.kix.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradeCommands extends ExtendedBaseCommand {

    public TradeCommands(Main plugin) {
        this.plugin = plugin;
    }

    private Main plugin;

    @CommandAlias("wtb")
    public void onWantToBuy(CommandSender sender, String args) {
        if (!sender.getClass().equals(Player.class)) {
            return;
        }

        String message = args;

        if (args.contains("{item}")) {
            Player player = (Player) sender;
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            message = message.replace("{item}", heldItem.toString());
        }

        this.plugin.getServer().broadcastMessage("[player] wants to buy " + message);

    }


}
