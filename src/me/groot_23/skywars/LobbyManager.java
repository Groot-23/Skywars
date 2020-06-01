package me.groot_23.skywars;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.skywars.util.EmptyChunkGenerator;
import me.groot_23.skywars.util.Pair;
import me.groot_23.skywars.util.Util;

public class LobbyManager {

	private World currentLobby = null;
	private GameManager currentGameManager = null;
	private String currentMap;
	private BukkitRunnable task = null;
	private Main plugin;
	private Map<String, Integer> playersPerWorld = new HashMap<String, Integer>();
	private Map<String, Integer> lobbiesPerWorld = new HashMap<String, Integer>();
	private String[] worldPickArray;
	private Random rand = new Random();
	private int persistentLobbies;
	private boolean allowDynamic = true;

	public LobbyManager(Main plugin) {
		this.plugin = plugin;
		update();
	}

	private void readConfig() {
		playersPerWorld = new HashMap<String, Integer>();
		lobbiesPerWorld = new HashMap<String, Integer>();
		ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
		ArrayList<String> worldPickList = new ArrayList<String>();
		if (worlds != null) {
			for (String key : worlds.getKeys(false)) {
				int probability = worlds.getInt(key + ".probability");
				for (int i = 0; i < probability; i++) {
					worldPickList.add(key);
				}
				playersPerWorld.put(key, worlds.getInt(key + ".numPlayers"));
			}
			worldPickArray = worldPickList.toArray(new String[worldPickList.size()]);

			// calc instances per world
			persistentLobbies = plugin.getConfig().getInt("persistentLobbies");
			PriorityQueue<Pair<Float, String>> partialLobbies = new PriorityQueue<Pair<Float, String>>();
			int totalInstances = 0;
			for (String key : worlds.getKeys(false)) {
				int probability = worlds.getInt(key + ".probability");
				float f = (float) probability / worldPickArray.length;
				int instances = (int) ((float) persistentLobbies * f);
				totalInstances += instances;
				f -= (float) instances;
				lobbiesPerWorld.put(key, instances);
				partialLobbies.add(new Pair<Float, String>(f, key));
			}
			for (int i = 0; i < persistentLobbies - totalInstances; i++) {
				String lobbyStr = partialLobbies.remove().getElement1();
				lobbiesPerWorld.put(lobbyStr, lobbiesPerWorld.get(lobbyStr) + 1);
			}
		}
		// allow dynamic
		allowDynamic = true;
		if(plugin.getConfig().contains("allowDynamicLobbies")) {
			allowDynamic = plugin.getConfig().getBoolean("allowDynamicLobbies");
		}
	}

	private void recreateLobbies() {
		// delete old worlds
		File[] oldWorlds = Bukkit.getWorldContainer().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith("skywars_lobby_");
			}
		});
		for (File f : oldWorlds) {
			Util.deleteWorld(f.getName());
		}

		// create new worlds
		for (Map.Entry<String, Integer> entry : lobbiesPerWorld.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				Util.copyWorld(entry.getKey(), "skywars_lobby_" + entry.getKey() + i);
			}
		}
	}

	public void update() {
		readConfig();
		recreateLobbies();
		createNewLobby();
	}

	public boolean createNewLobby() {
		if(worldPickArray == null || worldPickArray.length == 0) return false;
		currentMap = worldPickArray[rand.nextInt(worldPickArray.length)];
		System.out.println("[Skywars] map for new lobby: " + currentMap);
		currentLobby = null;
		for (int i = 0; currentLobby == null; i++) {
			String worldName = "skywars_lobby_" + currentMap + i;
			if (Util.worldExists(worldName)) {
				// check if the world has not been loaded yet
				if (Bukkit.getWorld(worldName) == null) {
					currentLobby = Bukkit.createWorld(new WorldCreator(worldName).generator(new EmptyChunkGenerator()));
				}
			} else {
				if(allowDynamic) {
					if (!Util.copyWorld(currentMap, worldName)) {
						return false;
					}
					currentLobby = Bukkit.createWorld(new WorldCreator(worldName).generator(new EmptyChunkGenerator()));
					// maybe no new world could be loaded for some reason -> prevent endless loop
					if (currentLobby == null)
						return false;
				} else {
					// find a new lobby if possible
					File[] worlds = Bukkit.getWorldContainer().listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							return pathname.getName().startsWith("skywars_lobby_");
						}
					});
					for(File f : worlds) {
						if(Bukkit.getWorld(f.getName()) == null) {
							currentLobby = Bukkit.createWorld(new WorldCreator(f.getName()).generator(new EmptyChunkGenerator()));
						}
					}
					// Sadly, no new lobby could be found :(
					if(currentLobby == null) return false;
				}

			}
		}
		currentGameManager = new GameManager(plugin, currentLobby);
		System.out.println("[Skywars] Successfully created new lobby: "+ currentLobby.getName());
		return true;
	}

	public World getLobby() {
		if (currentLobby == null) {
			createNewLobby();
		}
		if(Bukkit.getWorld(currentLobby.getName()) == null) {
			createNewLobby();
		}
		currentLobby.setAutoSave(false);
		return currentLobby;
	}
	
	private void cancelTask() {
		if(task != null) {
			task.cancel();
			task = null;
		}
	}
	
	public void joinPlayer(Player player) {
		currentLobby = getLobby();
		if(currentLobby != null) {
			if(currentLobby.getPlayers().size() >= playersPerWorld.get(currentMap)) {
				currentGameManager.goToSpawns();
				cancelTask();
				createNewLobby();
			}
			if(currentLobby != null) {
				player.teleport(currentLobby.getSpawnLocation());
				plugin.skywarsScoreboard.resetKills(player);
				// don't change gamemode too early
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
				    @Override
				    public void run(){
				        player.setGameMode(GameMode.ADVENTURE);
				    }
				}, 3L);
				if(currentLobby.getPlayers().size() >= playersPerWorld.get(currentMap)) {
					currentGameManager.goToSpawns();
					cancelTask();
					createNewLobby();
				} else if(task == null){
					task = new BukkitRunnable() {
						int counter = 30;
						@Override
						public void run() {
							plugin.skywarsScoreboard.updatePreGame(currentLobby, playersPerWorld.get(currentMap), counter);
							if(counter <= 0) {
								currentGameManager.goToSpawns();
								createNewLobby();
								cancelTask();
							}
							counter--;
						}
					};
					task.runTaskTimer(plugin, 0, 20);
				}
			}
		}
		
	}

	public void stopLobby(World world) {
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
				p.sendMessage(Util.chat("&eDiese Lobby wurde gerade geschlossen!"));
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
			Bukkit.unloadWorld(world, false);

			if (number >= persistentLobbies) {
				Util.deleteWorld(name);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getRegisteredWorlds() {
		Set<String> set = playersPerWorld.keySet();
		return set.toArray(new String[set.size()]);
	}
}
