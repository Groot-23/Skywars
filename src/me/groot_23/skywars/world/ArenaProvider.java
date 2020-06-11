package me.groot_23.skywars.world;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.Game;
import me.groot_23.skywars.util.EmptyChunkGenerator;
import me.groot_23.skywars.util.Pair;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class ArenaProvider {
	
	private Main plugin;
	
	private Arena currentArena = null;
	private Map<String, Integer> instancesPerWorld = new HashMap<String, Integer>();
	private boolean allowDynamic = true;
	private int persistentWorlds = 0;
	private String[] mapPickArray;
	private Random rand = new Random();
	
	private Map<UUID, Arena> arenaByUid = new HashMap<UUID, Arena>();
	
	public ArenaProvider(Main plugin) {
		this.plugin = plugin;
		update();
	}
	
	public Arena getArenaById(UUID uid) {
		return arenaByUid.get(uid);
	}
	
	private void readConfig() {
		instancesPerWorld = new HashMap<String, Integer>();
		ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
		ArrayList<String> mapPickList = new ArrayList<String>();
		if (worlds != null) {
			for (String key : worlds.getKeys(false)) {
				int weight = worlds.getInt(key + ".weight");
				for (int i = 0; i < weight; i++) {
					mapPickList.add(key);
				}
			}
			mapPickArray = mapPickList.toArray(new String[mapPickList.size()]);

			// calc instances per world
			persistentWorlds = plugin.getConfig().getInt("persistentLobbies");
			PriorityQueue<Pair<Float, String>> partialLobbies = new PriorityQueue<Pair<Float, String>>();
			int totalInstances = 0;
			for (String key : worlds.getKeys(false)) {
				int probability = worlds.getInt(key + ".weight");
				float f = (float) probability / mapPickArray.length;
				int instances = (int) ((float) persistentWorlds * f);
				totalInstances += instances;
				f -= (float) instances;
				instancesPerWorld.put(key, instances);
				partialLobbies.add(new Pair<Float, String>(f, key));
			}
			for (int i = 0; i < persistentWorlds - totalInstances; i++) {
				String lobbyStr = partialLobbies.remove().getElement1();
				instancesPerWorld.put(lobbyStr, instancesPerWorld.get(lobbyStr) + 1);
			}
			
		}
		// allow dynamic
		allowDynamic = true;
		if(plugin.getConfig().contains("allowDynamicLobbies")) {
			allowDynamic = plugin.getConfig().getBoolean("allowDynamicLobbies");
		}
	}
	
	private void recreateWorlds() {
		// delete old worlds
		File[] oldWorlds = Bukkit.getWorldContainer().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX);
			}
		});
		for (File f : oldWorlds) {
			Util.deleteWorld(f.getName());
		}

		// create new worlds
		for (Map.Entry<String, Integer> entry : instancesPerWorld.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				Util.copyWorld(entry.getKey(), SWconstants.SW_GAME_WORLD_PREFIX + entry.getKey() + i);
			}
		}
	}
	
	public boolean createNewArena() {
		if(mapPickArray == null || mapPickArray.length == 0) return false;
		String currentMap = mapPickArray[rand.nextInt(mapPickArray.length)];
		System.out.println("[Skywars] map for new arena: " + currentMap);
		currentArena = null;
		for (int i = 0; currentArena == null; i++) {
			String worldName = SWconstants.SW_GAME_WORLD_PREFIX + currentMap + i;
			if (Util.worldExists(worldName)) {
				// check if the world has not been loaded yet
				if (Bukkit.getWorld(worldName) == null) {
					System.out.println("World " + worldName + " is not loaded yet");
					World world = Bukkit.createWorld(getWorldCreator(worldName));
					if(world != null) {
						currentArena = new Arena(world, currentMap);
						System.out.println("World " + worldName + " was loaded");
					} else {
						System.out.println("World " + worldName + " COULD NOT BE LOADED");
					}
				} else {
					System.out.println("World " + worldName + " is ALREADY LOADED");
				}
			} else {
				if(allowDynamic) {
					if (!Util.copyWorld(currentMap, worldName)) {
						return false;
					}
					World world = Bukkit.createWorld(getWorldCreator(worldName));
					// maybe no new world could be loaded for some reason -> prevent endless loop
					if(world != null) {
						currentArena = new Arena(world, currentMap);
					} else {
						return false;
					}
				} else {
					// find a new world if possible
					File[] worlds = Bukkit.getWorldContainer().listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							return pathname.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX);
						}
					});
					for(File f : worlds) {
						if(Bukkit.getWorld(f.getName()) == null) {
							World world = Bukkit.createWorld(getWorldCreator(worldName));
							if(world != null) {
								currentArena = new Arena(world, currentMap);
							}
						}
					}
					// Sadly, no new lobby could be found :(
					if(currentArena == null) return false;
				}

			}
		}

		new Game(plugin, currentArena).start();
		arenaByUid.put(currentArena.getWorld().getUID(), currentArena);

		System.out.println("[Skywars] Successfully created new arena: "+ currentArena.getWorld().getName());
		return true;
	}
	
	public void update() {
		readConfig();
		recreateWorlds();
		createNewArena();
	}
	
	public Arena getArena() {
		if (currentArena == null) {
			System.out.println("Arena == null");
			createNewArena();
		}
		if(Bukkit.getWorld(currentArena.getWorld().getUID()) == null) {
			System.out.println("Arena world not found");
			createNewArena();
		}
		currentArena.getWorld().setAutoSave(false);
		return currentArena;
	}
	
	public void joinPlayer(Player player) {
		currentArena = getArena();
		if(currentArena != null) {
			if(!currentArena.joinPlayer(player)) {
				createNewArena();
				currentArena.joinPlayer(player);
			}
		}
	}
	
	public void stopJoin(Arena arena) {
		if(arena.getWorld().getUID().equals(currentArena.getWorld().getUID())) {
			createNewArena();
		}
	}
	
	public void stopLobby(World world) {
		arenaByUid.remove(world.getUID());
		System.out.println(arenaByUid);
		int numberIndex = 0;
		String name = world.getName();
		for (char c : name.toCharArray()) {
			if (Character.isDigit(c)) {
				break;
			}
			numberIndex++;
		}
		try {
			int number = Integer.parseInt(name.substring(numberIndex));
			for (Player p : world.getPlayers()) {
				p.sendMessage(Util.chat("&eDiese Arena wurde gerade geschlossen!"));
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}

			Bukkit.unloadWorld(world, false);
			
			String map = name.substring(SWconstants.SW_GAME_WORLD_PREFIX.length(), numberIndex);
			if (number >= instancesPerWorld.getOrDefault(map, 0)) {
				// Note that the world might not be deleted because Minecraft or Spigot thinks that it is a
				// good idea to lock the world folder of an unloaded world :(
				Util.deleteWorld(name);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getRegisteredWorlds() {
		Set<String> set = instancesPerWorld.keySet();
		return set.toArray(new String[set.size()]);
	}
	
	public WorldCreator getWorldCreator(String worldName) {
		WorldCreator creator = new WorldCreator(worldName);
		creator.generator(new EmptyChunkGenerator());
		creator.generateStructures(false);
		creator.type(WorldType.FLAT);
		return creator;
	}
}
