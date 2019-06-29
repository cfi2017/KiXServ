package dev.swiss.kix.model;

import org.bukkit.entity.Player;
import org.joda.time.DateTime;

public class Violation {

  private DateTime time;
  private Player player;

  public Violation(Player player) {
    this.player = player;
    this.time = new DateTime();
  }

  public DateTime getTime() {
    return time;
  }

  public Player getPlayer() {
    return player;
  }

}
