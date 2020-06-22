package me.groot_23.skywars;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.ming.MinGapi;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.gui.GuiRunnable;
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
import me.groot_23.skywars.game.SkywarsGame;
import me.groot_23.skywars.language.LanguageManager;
import me.groot_23.skywars.util.ResourceExtractor;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.ArenaProvider;

public class Main extends JavaPlugin
{
	//public WorldManager lobbyManager;
	//public GameManager gameManager;
	public SkywarsScoreboard skywarsScoreboard;
	public ArenaProvider arenaProvider;
	public static List<SkywarsKit> kits;
	public static Map<String, SkywarsKit> kitByName;
	public LanguageManager langManager;
//	public NMS nms;

	public static MiniGame game;
	
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
		
		langManager = new LanguageManager("de_de");
		langManager.loadLanguages(new File(getDataFolder(), "lang"));

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
		
		game = new SkywarsGame(this);

	}
	
	public void firstStart() {
		File resources = Util.getDataPackResources("skywars");
		if(!resources.exists()) {
			getLogger().info("[Skywars] Extracting datapack-");
			ResourceExtractor.extractResources("resources", resources.toPath(), true, false);
		}
		saveDefaultConfig();
		
		ResourceExtractor.extractResources("resources", getDataFolder().toPath(), true, false);
	}
	
	public static Main getInstance() {
		return instance;
	}
}
