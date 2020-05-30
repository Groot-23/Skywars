package me.groot_23.skywars.events;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class RefillChests implements Listener {

	Main plugin;

	public static final String SKYWARS_LOOT = "skywarsLoot";
	public static final String SKYWARS_REFILL_TIME = "skywarsRefillTime";
	public static final String SKYWARS_TIME_TILL_REFILL = "skywarsTimeTillRefill";

	public RefillChests(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	public static class RefillRunnable implements Runnable {

		private static HashMap<UUID, Integer> taskId = new HashMap<UUID, Integer>();

		private Entity entity;
		private JavaPlugin plugin;
		private Block block;

		public RefillRunnable(JavaPlugin plugin, Entity entity, Block block) {
			this.plugin = plugin;
			this.entity = entity;
			this.block = block;
		}

		public void startSchedule() {
			taskId.put(entity.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20));
		}

		private void endTask() {
			UUID uuid = entity.getUniqueId();
			int id = taskId.get(uuid);
			plugin.getServer().getScheduler().cancelTask(id);
			taskId.remove(entity.getUniqueId());
		}

		public static void refillChest(Block block) {
			block.setMetadata(SKYWARS_TIME_TILL_REFILL, new FixedMetadataValue(Main.getInstance(), 0));
			String loot = block.getMetadata(SKYWARS_LOOT).get(0).asString();
			LootTable lootTable = Bukkit.getLootTable(new NamespacedKey(Main.getInstance(), "chests/" + loot));
			BlockState state = block.getState();
			Lootable lootable = (Lootable) state;
			lootable.setLootTable(lootTable);
			state.update(true);
		}

		@Override
		public void run() {
			if (entity.isDead()) {
				endTask();
			}
			int timeTillRefill = block.getMetadata(SKYWARS_TIME_TILL_REFILL).get(0).asInt() - 1;
			block.setMetadata(SKYWARS_TIME_TILL_REFILL, new FixedMetadataValue(plugin, timeTillRefill));
			if (timeTillRefill > 0) {
				entity.setCustomName(Util.minuteSeconds(timeTillRefill));

			} else {
				entity.setCustomName(Util.chat("&aKiste voll!"));
				refillChest(block);
				endTask();
			}
		}

	}

	@EventHandler
	public void onChestOpen(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.CHEST) {
				if (!block.getMetadata(SKYWARS_TIME_TILL_REFILL).isEmpty()) {
					int timeTillRefill = block.getMetadata(SKYWARS_TIME_TILL_REFILL).get(0).asInt();
					if (timeTillRefill <= 0) {
						List<Entity> entities = event.getPlayer().getWorld().getEntities();
						for (Entity entity : entities) {
							if (entity.getType() == EntityType.ARMOR_STAND) {
								if (entity.getLocation().distanceSquared(block.getLocation()) < 2) {
									block.setMetadata(SKYWARS_TIME_TILL_REFILL, new FixedMetadataValue(plugin,
											block.getMetadata(SKYWARS_REFILL_TIME).get(0).asInt()));
									new RefillRunnable(plugin, entity, block).startSchedule();
									break;
								}
							}
						}
					}
				}
			}

		}
	}

	@EventHandler
	public void onChestPlace(BlockPlaceEvent event) {
		ItemMeta meta = event.getItemInHand().getItemMeta();
		if (meta.getDisplayName().equals("Skywars Kiste")) {
			World world = event.getPlayer().getWorld();
			Block block = event.getBlock();
			if (block.getType() == Material.CHEST) {
				block.setMetadata(SKYWARS_LOOT, new FixedMetadataValue(plugin, meta.getLore().get(0)));
				block.setMetadata(SKYWARS_REFILL_TIME,
				new FixedMetadataValue(plugin, Integer.parseInt(meta.getLore().get(1))));
				block.setMetadata(SKYWARS_TIME_TILL_REFILL, new FixedMetadataValue(plugin, 0));

				RefillRunnable.refillChest(block);
				// use block.location to floor the values
				ArmorStand entity = (ArmorStand) world.spawnEntity(block.getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
				entity.setCustomNameVisible(true);
				entity.setCustomName(Util.chat("&aKiste voll!"));
				entity.setVisible(false);
				entity.setGravity(false);
				entity.setCollidable(false);
				// place persistent data holder armorstand
				ArmorStand dataHolder = (ArmorStand) world.spawnEntity(block.getLocation(), EntityType.ARMOR_STAND);
				dataHolder.setCustomNameVisible(true);
				dataHolder.setCustomName("skywars_chest_marker" + "|" + meta.getLore().get(0) + "|" + meta.getLore().get(1));
				dataHolder.setGravity(false);
				dataHolder.setCollidable(false);
				dataHolder.setVisible(false);
			}
		}
	}

	@EventHandler
	public void onChestDestroy(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.CHEST) {
			System.out.println("Chest break");
			if (!event.getBlock().getMetadata("skywarsLoot").isEmpty()) {
				System.out.println("Skywarscehst break");
				if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
					System.out.println("Gamemode creative");
					List<Entity> entities = event.getPlayer().getWorld().getEntities();
					for (Entity entity : entities) {
						if (entity.getType() == EntityType.ARMOR_STAND) {
							if (entity.getLocation().distanceSquared(event.getBlock().getLocation()) < 2) {
								entity.remove();
							}
						}
					}
					// get rid of drops
					((Chest) event.getBlock().getState()).getInventory().clear();
				}
			}
		}
	}
}
