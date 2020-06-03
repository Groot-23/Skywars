package me.groot_23.skywars;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.skywars.commands.SWchest;
import me.groot_23.skywars.commands.SWedit;
import me.groot_23.skywars.commands.SWleave;
import me.groot_23.skywars.commands.SWmaps;
import me.groot_23.skywars.commands.SWset;
import me.groot_23.skywars.commands.SWspawns;
import me.groot_23.skywars.commands.SWupdate;
import me.groot_23.skywars.events.GameEvents;
import me.groot_23.skywars.events.ChestEvents;
import me.groot_23.skywars.events.StopLobbyLeave;
import me.groot_23.skywars.util.ResourceExtractor;
import me.groot_23.skywars.util.Util;

public class Main extends JavaPlugin
{
	public WorldManager lobbyManager;
	public GameManager gameManager;
	public SkywarsScoreboard skywarsScoreboard;
	private static Main instance;
	
	@Override
	public void onEnable() 
	{
		instance = this;
		
		File resources = Util.getDataPackResources("skywars");
		if(!resources.exists()) {
			getLogger().info("[Skywars] Extracting datapack-");
			ResourceExtractor.extractResources("resources", resources.toPath(), true);
		}
		saveDefaultConfig();
		
		lobbyManager = new WorldManager(this);
		skywarsScoreboard = new SkywarsScoreboard(this);

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
	}
	
	public static Main getInstance() {
		return instance;
	}
}
