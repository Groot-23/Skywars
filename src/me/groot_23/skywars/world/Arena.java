package me.groot_23.skywars.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkyChest;
import me.groot_23.skywars.util.Util;

public class Arena {
	
	private Main plugin;
	
	private World world;
	private Location midSpawn;
	private String mapName;
	private int maxPlayers;
	private int minPlayers;
	private int midRadius;
	private int mapRadius;
	
	private boolean allowJoin = true;
	
	private List<Location> spawns;
	private List<SkyChest> chests;
	
	
	public Arena(World world, String mapName) {
		this.world = world;
		this.mapName = mapName;
		this.plugin = Main.getInstance();
		readConfig();
		findPlayerSpawns();
		findChests();
	}

	
	
	public World getWorld() {
		return world;
	}
	
	public Location getMidSpawn() {
		return midSpawn;
	}
	
	public String getMapName() {
		return mapName;
	}
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}
	
	
	public void disableJoin() {
		if(allowJoin) {
			allowJoin = false;
			plugin.arenaProvider.stopJoin(this);
		}
	}
	
	public boolean joinPlayer(Player player) {
		if(allowJoin) {
			player.teleport(midSpawn);
			Util.resetPlayer(player);
			ItemStack kitSelector = new ItemStack(Material.CHEST);
			ItemMeta kitMeta = kitSelector.getItemMeta();
			kitMeta.setDisplayName("Kit Selector");
			kitSelector.setItemMeta(kitMeta);
			player.getInventory().setItem(4, kitSelector);
			plugin.skywarsScoreboard.resetKills(player);
			plugin.skywarsScoreboard.init(player);
			plugin.skywarsScoreboard.initPreGame(player, maxPlayers, 30, mapName);
			// don't change gamemode too early
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    @Override
			    public void run(){
			        player.setGameMode(GameMode.ADVENTURE);
			    }
			}, 1);
			if(world.getPlayers().size() >= maxPlayers) {
				disableJoin();
			}
			return true;
		}
		return false;
	}
	
	private void readConfig() {
		ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection("worlds." + mapName);
		if(section == null) {
			throw new RuntimeException("Skywars Config does not contain '" + "worlds." + mapName + "'");
		}
		minPlayers = section.getInt("minPlayers");
		maxPlayers = section.getInt("maxPlayers");
		midRadius = section.getInt("midRadius");
		mapRadius = section.getInt("mapRadius");
		if(section.contains("spawns")) {
			int spawnX = section.getInt("spawns.x");
			int spawnY = section.getInt("spawns.y");
			int spawnZ = section.getInt("spawns.z");
			midSpawn = new Location(world, spawnX, spawnY, spawnZ);
		} else {
			midSpawn = world.getSpawnLocation();
		}
	}
	
	private void findPlayerSpawns() {
		spawns = new ArrayList<Location>();
		for(Entity entity : world.getEntities()) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				if(entity.getCustomName().equals("skywars_spawn")) {
					spawns.add(entity.getLocation());
				}
			}
		}
		Collections.shuffle(spawns);
	}
	
	
	private void findChests() {
		chests = new ArrayList<SkyChest>();
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				String name = entity.getCustomName();
				if(name.startsWith("skywars_chest_marker")) {
					chests.add(new SkyChest(entity));
				}
			}
		}
	}
	
	public void removeLobby() {
		int radius = 20;
		int height = 5;
		int spawnX = midSpawn.getBlockX();
		int spawnY = midSpawn.getBlockY();
		int spawnZ = midSpawn.getBlockZ();
		for(int x = spawnX - radius; x <= spawnX + radius; x++) {
			for(int z = spawnZ - radius; z <= spawnZ + radius; z++) {
				for(int y = spawnY - height; y <= spawnY + height; y++) {
					world.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}
	
	public void removeGlassSpawns() {
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
	
	public void refillChests() {
		for(SkyChest chest : chests) {
			chest.refill();
		}
	}
	
	public void updateChestTimer(int time) {
		String timeStr = Util.minuteSeconds(time);
		for(SkyChest chest : chests) {
			if(chest.hologram.getCustomName().contains(":")) {
				chest.hologram.setCustomName(timeStr);
			}
		} 
	}
	
	public void resetBorder() {
		System.out.println(midSpawn);
		System.out.println(mapRadius);
		world.getWorldBorder().setCenter(midSpawn);
		world.getWorldBorder().setSize(2 * mapRadius);
	}
	
	public void shrinkBorder(int seconds) {
		world.getWorldBorder().setSize(2 * midRadius, seconds);
	}
	
	public boolean isInsideMidSpawn(Location location) {
		int xDist = midSpawn.getBlockX() - location.getBlockX();
		int zDist = midSpawn.getBlockZ() - location.getBlockZ();
		return (Math.abs(xDist) < midRadius && Math.abs(zDist) < midRadius);
	}
}
