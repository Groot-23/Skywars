package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.player.PlayerUtil;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkyGame;

public class GameEvents implements Listener {

	private Main plugin;

	public GameEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

//	@EventHandler
//	public void onDeath(PlayerDeathEvent e) {
//		if (e.getEntity().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			Player killer = e.getEntity().getKiller();
//			if (killer != null) {
//				if (killer.getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//					SkywarsScoreboard.addKill(killer);
//				}
//			}
//			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
//				@Override
//				public void run() {
//					e.getEntity().setGameMode(GameMode.SPECTATOR);
//					e.getEntity().spigot().respawn();
//				}
//			}, 1);
//
//		}
//	}

	@EventHandler
	public void onWorldLeave(PlayerChangedWorldEvent e) {
		if (Pixel.getGame(e.getFrom().getUID()) instanceof SkyGame) {
			Player p = e.getPlayer();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run() {
					p.setGameMode(GameMode.ADVENTURE);
					PlayerUtil.resetPlayer(p);
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}, 5);

		}
	}

//	@EventHandler
//	public void onBlockPlaced(BlockPlaceEvent e) {
//		if (MinG.getGame(e.getPlayer().getWorld().getUID()) instanceof SkyGame) {
//			e.getBlock().setMetadata("skywars_player_placed", new FixedMetadataValue(plugin, true));
//		}
//	}
//
//	@EventHandler
//	public void onBlockDamage(BlockDamageEvent e) {
//		if (e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			if (!e.getBlock().hasMetadata("skywars_player_placed")) {
//				Arena arena = Main.game.getArenaById(e.getBlock().getWorld().getUID());
//				if (arena != null) {
//					if (arena.isInsideMidSpawn(e.getBlock().getLocation())) {
//						e.setCancelled(true);
//					}
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	public void onBlockBreak(BlockBreakEvent e) {
//		if (e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			if (!e.getBlock().hasMetadata("skywars_player_placed")) {
//				Arena arena = Main.game.getArenaById(e.getBlock().getWorld().getUID());
//				if (arena != null) {
//					if (arena.isInsideMidSpawn(e.getBlock().getLocation())) {
//						e.setCancelled(true);
//					}
//				}
//			}
//		}
//	}

//	@EventHandler
//	public void onRespawn(PlayerRespawnEvent e) {
//		if (e.getPlayer().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			Arena arena = Main.game.getArenaById(e.getPlayer().getWorld().getUID());
//			if (arena != null) {
//				e.setRespawnLocation(arena.getMidSpawn());
//			} else {
//				e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
//			}
//		}
//	}
	
	@EventHandler
	public void removeBottle(PlayerItemConsumeEvent e) {
		if(e.getItem().getType() == Material.POTION) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(e.getPlayer().getEquipment().getItemInMainHand().getType() == Material.GLASS_BOTTLE)
						e.getPlayer().getEquipment().setItemInMainHand(null);
					if(e.getPlayer().getEquipment().getItemInOffHand().getType() == Material.GLASS_BOTTLE)
						e.getPlayer().getEquipment().setItemInOffHand(null);
				}
			}.runTaskLater(plugin, 1);
		}
	}

	@EventHandler
	public void preventSpawn(CreatureSpawnEvent e) {
		World world = e.getEntity().getWorld();
		if (!world.getMetadata("skywars_edit_world").isEmpty()
				|| Pixel.getGame(e.getEntity().getWorld().getUID()) instanceof SkyGame) {
			if (e.getSpawnReason() == SpawnReason.NATURAL) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void lapisOpen(InventoryOpenEvent e) {
		if (e.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) e.getInventory();
			inv.setSecondary(new ItemStack(Material.LAPIS_LAZULI, 64));
		}
	}

	@EventHandler
	public void lapisClose(InventoryCloseEvent e) {
		if (e.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) e.getInventory();
			inv.setSecondary(null);
		}
	}

	@EventHandler
	public void lapisClick(InventoryClickEvent e) {
		if (e.getInventory().getType() == InventoryType.ENCHANTING) {
			if (e.getCurrentItem().getType() == Material.LAPIS_LAZULI) {
				e.setCancelled(true);
			}
		}
	}
}
