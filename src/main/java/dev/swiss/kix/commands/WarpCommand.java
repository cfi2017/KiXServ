package dev.swiss.kix.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.swiss.kix.ExtendedBaseCommand;
import dev.swiss.kix.Main;
import org.bukkit.command.CommandSender;

@CommandAlias("warp|w")
public class WarpCommand extends ExtendedBaseCommand {

  private Main plugin;

  public WarpCommand(Main plugin) {
    this.plugin = plugin;
  }


  @Default
  public void onListWarps(CommandSender sender) {

  }

}
