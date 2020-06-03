package me.groot_23.skywars.events;

import java.util.Collection;
import java.util.List;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.metadata.FixedMetadataValue;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class ChestEvents implements Listener {

	Main plugin;



	public ChestEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	public static void refillChest(Block block) {
		String loot = block.getMetadata(SWconstants.SW_LOOT_TABLE).get(0).asString();
		LootTable lootTable = Bukkit.getLootTable(new NamespacedKey(Main.getInstance(), "chests/" + loot));
		BlockState state = block.getState();
		Lootable lootable = (Lootable) state;
		lootable.setLootTable(lootTable);
		state.update(true);
	}


	@EventHandler
	public void onChestOpen(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.CHEST) {
				Collection<Entity> near = block.getWorld().getNearbyEntities(block.getLocation().add(0.5, -1, 0.5), 0.1, 0.1, 0.1);
				for(Entity n : near) {
					if(n.getType() == EntityType.ARMOR_STAND) {
						if(!n.getCustomName().contains(":")) {
							n.setCustomName(":");
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
				block.setMetadata(SWconstants.SW_LOOT_TABLE, new FixedMetadataValue(plugin, meta.getLore().get(0)));

				refillChest(block);
				// use block.location to floor the values
				ArmorStand entity = (ArmorStand) world.spawnEntity(block.getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
				entity.setCustomNameVisible(true);
				entity.setCustomName(Util.chat("&aKiste voll!"));
				entity.setVisible(false);
				entity.setGravity(false);
				entity.setCollidable(false);
				// place persistent data holder armorstand
				ArmorStand dataHolder = (ArmorStand) world.spawnEntity(block.getLocation(), EntityType.ARMOR_STAND);
				dataHolder.setCustomNameVisible(false);
				dataHolder.setCustomName("skywars_chest_marker" + "|" + meta.getLore().get(0));
				dataHolder.setGravity(false);
				dataHolder.setCollidable(false);
				dataHolder.setVisible(false);
			}
		}
	}

	@EventHandler
	public void onChestDestroy(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.CHEST) {
			if (!event.getBlock().getMetadata("skywarsLoot").isEmpty()) {
				if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
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
	
	// Players should not be able to break their chests
	@EventHandler
	public void onChestHit(BlockDamageEvent event) {
		if(event.getBlock().getType() == Material.CHEST) {
			if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
				if(event.getPlayer().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
					event.setCancelled(true);
				}
			}
		}
	}
}
