package me.groot_23.skywars;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.skywars.commands.SWchest;
import me.groot_23.skywars.commands.SWedit;
import me.groot_23.skywars.commands.SWleave;
import me.groot_23.skywars.commands.SWmaps;
import me.groot_23.skywars.commands.SWset;
import me.groot_23.skywars.commands.SWspawns;
import me.groot_23.skywars.commands.SWupdate;
import me.groot_23.skywars.events.GameEvents;
import me.groot_23.skywars.events.KitEvents;
import me.groot_23.skywars.events.ChestEvents;
import me.groot_23.skywars.events.StopLobbyLeave;
import me.groot_23.skywars.util.ResourceExtractor;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.ArenaProvider;

public class Main extends JavaPlugin
{
	//public WorldManager lobbyManager;
	//public GameManager gameManager;
	public SkywarsScoreboard skywarsScoreboard;
	public ArenaProvider arenaProvider;
	public List<SkywarsKit> kits;
	public Map<String, SkywarsKit> kitByName;
	private static Main instance;
	
	@Override
	public void onEnable() 
	{
		instance = this;
		
		firstStart();
		
		kits = SkywarsKit.loadKits();
		kitByName = new HashMap<String, SkywarsKit>();
		for(SkywarsKit kit : kits) {
			kitByName.put(kit.getName(), kit);
		}
		
		//lobbyManager = new WorldManager(this);
		skywarsScoreboard = new SkywarsScoreboard(this);
		arenaProvider = new ArenaProvider(this);

		new SWjoin(this);
		new SWleave(this);
		new SWedit(this);
		new SWmaps(this);
		new SWupdate(this);
		new SWchest(this);
		new SWspawns(this);
		new SWset(this);
		
		new StopLobbyLeave(this);
		new ChestEvents(this);
		new GameEvents(this);
		new KitEvents(this);
		
//		new BukkitRunnable() {
//			
//			@Override
//			public void run() {
//				World memLeak = Bukkit.createWorld(new WorldCreator("memory_leak"));
//				Bukkit.unloadWorld(memLeak, true);
//			}
//		}.runTaskTimer(this, 60, 100);
	}
	
	public void firstStart() {
		File resources = Util.getDataPackResources("skywars");
		if(!resources.exists()) {
			getLogger().info("[Skywars] Extracting datapack-");
			ResourceExtractor.extractResources("resources", resources.toPath(), true);
		}
		saveDefaultConfig();
		
		File kitFile = new File(getDataFolder().getAbsolutePath() + File.separator + "kits.yml");
		if(!kitFile.exists()) {
			try {
				Files.copy(getResource("kits.yml"), kitFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Main getInstance() {
		return instance;
	}
}
