package me.groot_23.skywars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.skywars.events.RefillChests;
import me.groot_23.skywars.util.Util;

public class GameManager {

	private Main plugin;
	
	public GameManager(Main plugin) {
		this.plugin = plugin;
	}
	
	public void initLobby(World world) {
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				String name = entity.getCustomName();
				if(name.startsWith("skywars_chest_marker")) {
					String sub = name.substring(name.indexOf('|') + 1);
					String loot = sub.substring(0, sub.indexOf('|'));
					int refillTime = Integer.parseInt(sub.substring(sub.indexOf('|') + 1));
					Block block = entity.getLocation().getBlock();
					if(block.getType() == Material.CHEST) {
						block.setMetadata(RefillChests.SKYWARS_LOOT, new FixedMetadataValue(plugin, loot));
						block.setMetadata(RefillChests.SKYWARS_REFILL_TIME, new FixedMetadataValue(plugin, refillTime));
						RefillChests.RefillRunnable.refillChest(block);
					}
				}
			}
		}
	}
	
	private void removeSpawnLobby(World world) {
		int radius = 20;
		int height = 5;
		int spawnX = world.getSpawnLocation().getBlockX();
		int spawnY = world.getSpawnLocation().getBlockY();
		int spawnZ = world.getSpawnLocation().getBlockZ();
		for(int x = spawnX - radius; x <= spawnX + radius; x++) {
			for(int z = spawnZ - radius; z <= spawnZ + radius; z++) {
				for(int y = spawnY - height; y <= spawnY + height; y++) {
					world.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}
	

	
	public static class StartGameRunnable implements Runnable{

		private static HashMap<UUID, Integer> taskId = new HashMap<UUID, Integer>();
		
		private World world;
		private Main plugin;
		private List<Location> spawns;
		private int timer;
		
		public StartGameRunnable(Main plugin, World world, List<Location> spawns) {
			this.plugin = plugin;
			this.world = world;
			this.spawns = spawns;
			this.timer = 11;
		}
		
		public void startSchedule() {
			taskId.put(world.getUID(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20));
		}
		
		
		private void endTask() {
			UUID uuid = world.getUID();
			plugin.getServer().getScheduler().cancelTask(taskId.get(uuid));
			taskId.remove(uuid);
		}
		
		private void removeGlassSpawns(World world, List<Location> spawns) {
			int radius = 4;
			for(Location spawn : spawns) {
				int spawnX = spawn.getBlockX();
				int spawnY = spawn.getBlockY();
				int spawnZ = spawn.getBlockZ();
				for(int x = spawnX - radius; x <= spawnX + radius; x++) {
					for(int z = spawnZ - radius; z <= spawnZ + radius; z++) {
						for(int y = spawnY - radius; y <= spawnY + radius; y++) {
							Block block = world.getBlockAt(x, y, z);
							if(block.getType() == Material.GLASS) {
								block.setType(Material.AIR);
							}
						}
					}
				}
			}
		}
		
		@Override
		public void run() {
			timer--;
			if(timer % 10 == 0) {
				for(Player p : world.getPlayers()) {
					p.sendMessage(Util.chat("Skywars startet in &c" + timer));
				}
			}
			if(timer <= 5) {
				for(Player p : world.getPlayers()) {
					if(timer != 0) {
						p.sendMessage(Util.chat("Skywars startet in &c" + timer));
						p.sendTitle(Util.chat("&a" + Integer.toString(timer)) , Util.chat("&dMache dich bereit!"), 3, 14, 3);
					} else {
						p.sendMessage(Util.chat("&aSkywars gestartet"));
						p.sendTitle(Util.chat("&aSkywars gestartet") , Util.chat("&dder Kampf beginnt!"), 3, 20, 3);
						p.setFallDistance(-1000);
					}

				}
			}
			if(timer <= 0) {
				removeGlassSpawns(world, spawns);
				plugin.gameManager.startGame(world);
				endTask();
			}
		}
		
	}
	
	public void goToSpawns(World world) {
		ArrayList<Location> spawns = new ArrayList<Location>();
		for(Entity entity : world.getEntities()) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				if(entity.getCustomName().equals("skywars_spawn")) {
					spawns.add(entity.getLocation());
				}
			}
		}
		Collections.shuffle(spawns);
		if(spawns.size() < world.getPlayers().size()) {
			Bukkit.getServer().broadcastMessage(Util.chat("&cZu wenige Spawns! Fehler beim Starten von Skywars :("));
			return;
		}
		for(int i = 0; i < world.getPlayers().size(); i++) {
			world.getPlayers().get(i).teleport(spawns.get(i));
		}
		removeSpawnLobby(world);
		new StartGameRunnable(plugin, world, spawns).startSchedule();
		
	}
	
	public void startGame(World world) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(world != Bukkit.getWorld(world.getUID())) {
					System.out.println("Task beendet!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					cancel();
				}
				plugin.skywarsScoreboard.updateGame(world);
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
