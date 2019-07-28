package dev.swiss.kix.spawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SpawnerDestroyFilter implements Listener {

  private Map<EntityType, ItemStack> typeDrops = new HashMap<>();

  public SpawnerDestroyFilter() {

    ItemStack zombieSpawnEgg = new ItemStack(Material.ZOMBIE_SPAWN_EGG, 1);
    ItemStack blazeSpawnEgg = new ItemStack(Material.BLAZE_SPAWN_EGG, 1);
    ItemStack witherSkeletonSpawnEgg = new ItemStack(Material.WITHER_SKELETON_SPAWN_EGG, 1);
    ItemStack creeperSpawnEgg = new ItemStack(Material.CREEPER_SPAWN_EGG, 1);
    ItemStack skeletonSpawnEgg = new ItemStack(Material.SKELETON_SPAWN_EGG, 1);
    ItemStack spiderSpawnEgg = new ItemStack(Material.SPIDER_SPAWN_EGG, 1);
    typeDrops.put(EntityType.ZOMBIE, zombieSpawnEgg);
    typeDrops.put(EntityType.BLAZE, blazeSpawnEgg);
    typeDrops.put(EntityType.WITHER_SKELETON, witherSkeletonSpawnEgg);
    typeDrops.put(EntityType.CREEPER, creeperSpawnEgg);
    typeDrops.put(EntityType.SKELETON, skeletonSpawnEgg);
    typeDrops.put(EntityType.SPIDER, spiderSpawnEgg);

  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onSpawnerDestroy(BlockBreakEvent event) {
    Block block = event.getBlock();
    if (block.getType() == Material.SPAWNER) {
      CreatureSpawner spawner = (CreatureSpawner) block.getState();
      ItemStack drops = typeDrops.get(spawner.getSpawnedType());
      Location loc = block.getLocation();
      loc.getWorld().dropItemNaturally(loc, drops);
    }
  }

}
