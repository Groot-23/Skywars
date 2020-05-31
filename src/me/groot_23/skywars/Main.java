package me.groot_23.skywars;

import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.skywars.commands.SWapply;
import me.groot_23.skywars.commands.SWchest;
import me.groot_23.skywars.commands.SWedit;
import me.groot_23.skywars.commands.SWleave;
import me.groot_23.skywars.commands.SWmaps;
import me.groot_23.skywars.commands.SWspawns;
import me.groot_23.skywars.commands.SWupdate;
import me.groot_23.skywars.events.GameEvents;
import me.groot_23.skywars.events.RefillChests;
import me.groot_23.skywars.events.StopLobbyLeave;

public class Main extends JavaPlugin
{
	public LobbyManager lobbyManager;
	public GameManager gameManager;
	public SkywarsScoreboard skywarsScoreboard;
	private static Main instance;
	
	@Override
	public void onEnable() 
	{
		instance = this;
		
		gameManager = new GameManager(this);
		lobbyManager = new LobbyManager(this);
		skywarsScoreboard = new SkywarsScoreboard(this);

		new SWjoin(this);
		new SWleave(this);
		new SWedit(this);
		new SWapply(this);
		new SWmaps(this);
		new SWupdate(this);
		new SWchest(this);
		new SWspawns(this);
		
		new StopLobbyLeave(this);
		new RefillChests(this);
		new GameEvents(this);
	}
	
	public static Main getInstance() {
		return instance;
	}
}
