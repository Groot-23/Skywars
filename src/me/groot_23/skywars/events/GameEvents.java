package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.Arena;

public class GameEvents implements Listener {
	
	private Main plugin;
	
	public GameEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(e.getEntity().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			Player killer = e.getEntity().getKiller();
			if(killer != null) {
				if(killer.getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
					plugin.skywarsScoreboard.addKill(killer);
				}
			}
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    @Override
			    public void run(){
			    	e.getEntity().setGameMode(GameMode.SPECTATOR);
			    	e.getEntity().spigot().respawn();
			    }
			}, 1);

		}
	}
	
	
	@EventHandler
	public void onWorldLeave(PlayerChangedWorldEvent e) {
		if(e.getFrom().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			Player p = e.getPlayer();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run() {
					p.setGameMode(GameMode.ADVENTURE);
					Util.resetPlayer(p);
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}, 5);

		}
	}
	
	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent e) {
		if(e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			e.getBlock().setMetadata("skywars_player_placed", new FixedMetadataValue(plugin, true));
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent e) {
		if(e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			if(!e.getBlock().hasMetadata("skywars_player_placed")) {
				Arena arena = plugin.arenaProvider.getArenaById(e.getBlock().getWorld().getUID());
				if(arena != null) {
					if(arena.isInsideMidSpawn(e.getBlock().getLocation())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			if(!e.getBlock().hasMetadata("skywars_player_placed")) {
				Arena arena = plugin.arenaProvider.getArenaById(e.getBlock().getWorld().getUID());
				if(arena != null) {
					if(arena.isInsideMidSpawn(e.getBlock().getLocation())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if(e.getPlayer().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			Arena arena = plugin.arenaProvider.getArenaById(e.getPlayer().getWorld().getUID());
			if(arena != null) {
				e.setRespawnLocation(arena.getMidSpawn());
			} else {
				e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
			}
		}
	}
	
	@EventHandler
	public void preventSpawn(CreatureSpawnEvent e) {
		World world = e.getEntity().getWorld();
		if(!world.getMetadata("skywars_edit_world").isEmpty() || world.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			if(e.getSpawnReason() == SpawnReason.NATURAL) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void lapisOpen(InventoryOpenEvent e) {
		if(e.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) e.getInventory();
			inv.setSecondary(new ItemStack(Material.LAPIS_LAZULI, 64));
		}
	}
	
	@EventHandler
	public void lapisClose(InventoryCloseEvent e) {
		if(e.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) e.getInventory();
			inv.setSecondary(null);
		}
	}
	
	@EventHandler
	public void lapisClick(InventoryClickEvent e) {
		if(e.getInventory().getType() == InventoryType.ENCHANTING) {
			if(e.getCurrentItem().getType() == Material.LAPIS_LAZULI) {
				e.setCancelled(true);
			}
		}
	}	
	
}
