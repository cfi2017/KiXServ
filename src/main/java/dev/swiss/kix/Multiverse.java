package dev.swiss.kix;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;

public class Multiverse {

  private static MultiverseCore core;

  public static MultiverseCore getCore() {
    if (core == null) {
      core = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
    }
    return core;
  }

}
