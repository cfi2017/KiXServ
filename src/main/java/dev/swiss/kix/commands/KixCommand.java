package dev.swiss.kix.commands;

import co.aikar.commands.annotation.*;
import dev.swiss.kix.ExtendedBaseCommand;
import dev.swiss.kix.Main;
import dev.swiss.kix.Permissions;
import dev.swiss.kix.profanity.ReplacementRule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommandAlias("kix|kixstar|k")
public class KixCommand extends ExtendedBaseCommand {

  public KixCommand(Main plugin) {
    this.plugin = plugin;
  }

  private Main plugin;

  @Default
  @HelpCommand
  public void onHelp(CommandSender sender) {
    sendMsg(sender, heading("Kix Help"));
    sendMsg(sender, "/kix profanity add <profanity>");
    sendMsg(sender, "/kix reload");
  }

  @Subcommand("eval")
  @CommandPermission(Permissions.EVAL)
  public void onEval(CommandSender sender, String command) {
    try {
      Process process = Runtime.getRuntime().exec(command);
      consumeProcess(sender, command, process);
    } catch (IOException e) {
      plugin.getLogger().warning("Error trying to run command: " + e.getMessage());
    }
  }

  private void consumeProcess(CommandSender sender, String command, Process process) {
    consumeInputStream(sender, process.getInputStream(), ChatColor.GREEN);
    consumeInputStream(sender, process.getErrorStream(), ChatColor.RED);
    CompletableFuture.runAsync(() -> {
      try {
        process.waitFor();
        sender.sendMessage(String.format("%sDone (%d): %s", ChatColor.DARK_GREEN, process.exitValue(), command));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  private void consumeInputStream(CommandSender sender, InputStream errorStream, ChatColor red) {
    CompletableFuture.runAsync(() -> {
      try {
        InputStreamReader isr = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null)
          sender.sendMessage(red + line);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    });
  }


  @Subcommand("profanity add")
  @CommandPermission(Permissions.PROFANITY_ADMIN)
  public void onProfanityAdd(CommandSender sender, String profanity) {
    List<String> blacklist = Main.profanityFilter.getBlacklist();
    String trimmedProfanity = profanity.trim();
    if (!"".equals(trimmedProfanity)) {
      if (!blacklist.contains(trimmedProfanity)) {
        blacklist.add(trimmedProfanity);
        this.plugin.getConfig().set("profanity.blacklist", blacklist);
        this.plugin.saveConfig();
        Main.LOGGER.info("Added '{}' to blacklist.", trimmedProfanity);
        sender.sendMessage("Word added to blacklist.");
      } else sendMsg(sender, "Profanity already exists.");
    } else sendMsg(sender, "Please specify a profanity.");

  }

  @Subcommand("profanity remove")
  @CommandPermission(Permissions.PROFANITY_ADMIN)
  public void onProfanityRemove(CommandSender sender, String profanity) {
    List<String> blacklist = Main.profanityFilter.getBlacklist();
    String trimmedProfanity = profanity.trim();
    if (!"".equals(trimmedProfanity)) {
      if (blacklist.contains(profanity)) {
        blacklist.remove(profanity);
        this.plugin.getConfig().set("profanity.blacklist", blacklist);
        this.plugin.saveConfig();
        Main.LOGGER.info("Removed '{}' from blacklist.", trimmedProfanity);
        sender.sendMessage("Word removed from blacklist.");
      } else sendMsg(sender, "Profanity doesn't exist.");
    } else sendMsg(sender, "Please specify a profanity.");
  }

  @Subcommand("profanity list")
  @CommandPermission(Permissions.PROFANITY_ADMIN)
  public void onProfanityList(CommandSender sender) {
    List<String> blacklist = Main.profanityFilter.getBlacklist();
    sendMsg(sender, heading("Profanities:"));
    sendMsg(sender, String.format("%s%s", ChatColor.GREEN, String.join(", ", blacklist)));
  }

  @Subcommand("profanity rule remove")
  @CommandPermission(Permissions.PROFANITY_ADMIN)
  public void onProfanityRuleAdd(CommandSender sender, String from, @Optional String to) {
    List<ReplacementRule> rules = Main.profanityFilter.getRules();
    if (!"".equals(from) && from != null) {
      ReplacementRule rule = new ReplacementRule(from, to);
      if (rules.contains(rule)) {
        rules.remove(rule);
        this.plugin.getConfig().set("profanity.replacements", rules);
        this.plugin.saveConfig();
        Main.LOGGER.info("Removed '{}' from rules.", rule.toString());
        sender.sendMessage(String.format("Rule %s removed from rules.", rule));
      } else sendMsg(sender, "Rule doesn't exist.");
    } else sendMsg(sender, "Please enter the rule you want to remove.");
  }

  @Subcommand("profanity rule add")
  @CommandPermission(Permissions.PROFANITY_ADMIN)
  public void onProfanityRuleRemove(CommandSender sender, String from, @Optional String to) {
    List<ReplacementRule> rules = Main.profanityFilter.getRules();
    if (!"".equals(from) && from != null) {
      ReplacementRule rule = new ReplacementRule(from, to);
      if (!rules.contains(rule)) {
        rules.add(rule);
        this.plugin.getConfig().set("profanity.replacements", rules);
        this.plugin.saveConfig();
        Main.LOGGER.info("Added '{}' to rules.", rule.toString());
        sender.sendMessage(String.format("Rule %s added to rules.", rule));
      } else sendMsg(sender, "Rule already exists.");
    } else sendMsg(sender, "Please enter the string you want to replace.");
  }

  @Subcommand("reload")
  @CommandPermission(Permissions.RELOAD_PLUGIN)
  public void onReload() {
    this.plugin.reload();
  }

  @Subcommand("vd")
  @CommandPermission(Permissions.VIEW_DISTANCE)
  public void onViewDistance(Player player) {
    Integer highest = getHighestViewDistanceForPlayer(player);
    player.sendMessage(String.format("%sYour view distance is %d", ChatColor.GREEN, player.getViewDistance()));
  }

  @Subcommand("vd")
  @CommandPermission(Permissions.VIEW_DISTANCE)
  public void onViewDistance(Player player, Integer distance) {
    Integer highest = getHighestViewDistanceForPlayer(player);

    if (distance < 4) setViewDistance(player, 4);
    else if (distance > highest) {
      setViewDistance(player, highest);
    } else setViewDistance(player, distance);

  }

  private void setViewDistance(Player player, Integer distance) {
    player.sendMessage(String.format("%sSetting your view distance to %d.", ChatColor.GREEN, distance));
    try {
      player.setViewDistance(distance);
    } catch (NotImplementedException ex) {
      player.sendMessage(String.format("%sError: setViewDistance is not available in this version of paper.", ChatColor.RED));
    }
  }

  private Integer getHighestViewDistanceForPlayer(Player player) {
    int defaultDistance = Bukkit.getServer().getViewDistance();
    for (int i = 20; i > defaultDistance; i--) {
      if (player.hasPermission(String.format("%s.%d", Permissions.VIEW_DISTANCE, i))) {
        return i;
      }
    }
    return defaultDistance;
  }

  @Subcommand("warp add")
  @CommandPermission(Permissions.WARP_ADD)
  public void onWarpAdd(CommandSender sender, String warp) {

  }

  @Subcommand("warp remove")
  @CommandPermission(Permissions.WARP_REMOVE)
  public void onWarpRemove(CommandSender sender, String warp) {

  }

  @Subcommand("warp edit")
  @CommandPermission(Permissions.WARP_EDIT)
  public void onWarpEdit(CommandSender sender, String warp) {

  }

  @Subcommand("warp list")
  @CommandPermission(Permissions.WARP_LIST)
  public void onWarpList(CommandSender sender) {

  }

}
