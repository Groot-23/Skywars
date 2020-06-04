package me.groot_23.skywars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.skywars.events.ChestEvents;
import me.groot_23.skywars.util.Pair;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class GameManager {

	private Main plugin;
	private World world;
	private List<Pair<Block, ArmorStand>> chests;
	private int refillTime;
	
	public GameManager(Main plugin, World world) {		
		this.plugin = plugin;
		this.world = world;
		
		refillTime = plugin.getConfig().getInt("refillTime");
		
		world.setPVP(false);
		this.chests = new ArrayList<Pair<Block,ArmorStand>>();
		findChests();
		refillAllChests();
	}
	
	private void findChests() {
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				String name = entity.getCustomName();
				if(name.startsWith("skywars_chest_marker")) {
					String loot = name.substring(name.indexOf('|') + 1);
					Block block = entity.getLocation().getBlock();
					if(block.getType() == Material.CHEST) {
						block.setMetadata(SWconstants.SW_LOOT_TABLE, new FixedMetadataValue(plugin, loot));
						ArmorStand armorStand = null;
						Collection<Entity> nearbyEntities = world.getNearbyEntities(block.getLocation().add(0.5, 1, 0.5), 0.1, 0.1, 0.1);
						for(Entity ne : nearbyEntities) {
							if(ne.getType() == EntityType.ARMOR_STAND) {
								armorStand = (ArmorStand) ne;
								break;
							}
						}
						if(armorStand != null) {
							chests.add(new Pair<Block, ArmorStand>(block, armorStand));
						} else {
							plugin.getLogger().warning("No ArmorStand found for chest at: " + block.getLocation().toString());
						}
					}
				}
			}
		}
	}
	
	
	private void refillAllChests() {
		for(Pair<Block, ArmorStand> pair : chests) {
			ChestEvents.refillChest(pair.getElement0());
			pair.getElement1().setCustomName(ChatColor.GREEN + "Kiste voll");
		}
	}
	
	private void updateChestTimer(int time) {
		String timeStr = Util.minuteSeconds(time);
		for(Pair<Block, ArmorStand> pair : chests) {
			if(pair.getElement1().getCustomName().contains(":")) {
				pair.getElement1().setCustomName(timeStr);
			}
		}
	}
	
	private void removeSpawnLobby() {
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
	

	public class SpawnRunnable extends BukkitRunnable {

		private World world;
		private Main plugin;
		private List<Location> spawns;
		private int timer;
		
		public SpawnRunnable(Main plugin, World world, List<Location> spawns) {
			this.plugin = plugin;
			this.world = world;
			this.spawns = spawns;
			this.timer = 10;
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
			if(Bukkit.getWorld(world.getUID()) == null) {
				cancel();
			}
			plugin.skywarsScoreboard.updateGame(world, world.getPlayers().size(), "Start", timer);
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
						// remove falldamage
						p.setFallDistance(-1000);
					}

				}
			}
			if(timer <= 0) {
				removeGlassSpawns(world, spawns);
				startGame();
				cancel();
			}
			timer--;
		}
		
	}
	
	public void goToSpawns() {
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
		removeSpawnLobby();
		new SpawnRunnable(plugin, world, spawns).runTaskTimer(plugin, 0, 20);
		
	}
	
	public void startGame() {
		world.setPVP(true);
		for(Player player : world.getPlayers()) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		new BukkitRunnable() {
			
			int refillCounter = refillTime;
			
			@Override
			public void run() {
				if(world != Bukkit.getWorld(world.getUID())) {
					cancel();
				}
				int playersLeft = 0;
				Player potentialWinner = null;
				for(Player p : world.getPlayers()) {
					if(p.getGameMode() == GameMode.SURVIVAL) {
						playersLeft++;
						potentialWinner = p;
					}
				}
				if(playersLeft == 1) {
					winner(potentialWinner);
					cancel();
				}
				plugin.skywarsScoreboard.updateGame(world, playersLeft, "Refill", refillCounter);
				updateChestTimer(refillCounter);
				if(refillCounter <= 0) {
					refillCounter = refillTime;
					refillAllChests();
				}
				refillCounter--;
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	public void winner(Player player) {
		for(Player p : world.getPlayers()) {
			p.sendTitle(Util.chat("&c" + player.getName()), Util.chat("&5Hat GEWONNEN"), 3, 30, 3);
	    	p.setGameMode(GameMode.SPECTATOR);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				// maybe the world was deleted and got overwritten with a new lobby
				if(Bukkit.getWorld(world.getUID()) != null)
					plugin.lobbyManager.stopLobby(world);
			}
		}, 200);
	}
}
