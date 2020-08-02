package me.groot_23.skywars;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;
import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.ming.MinG;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.commands.KitCommands;
import me.groot_23.ming.commands.LangCommand;
import me.groot_23.ming.commands.MarkerCommand;
import me.groot_23.ming.util.ResourceExtractor;
import me.groot_23.skywars.commands.SWchest;
import me.groot_23.skywars.commands.SWedit;
import me.groot_23.skywars.commands.SWleave;
import me.groot_23.skywars.commands.SWmaps;
import me.groot_23.skywars.commands.SWset;
import me.groot_23.skywars.commands.SWupdate;
import me.groot_23.skywars.events.GameEvents;
import me.groot_23.skywars.events.KitEvents;
import me.groot_23.skywars.events.ChestEvents;
import me.groot_23.skywars.events.StopLobbyLeave;
import me.groot_23.skywars.game.SkywarsGame;
import me.groot_23.skywars.util.Util;

public class Main extends JavaPlugin
{

	public static MiniGame game;
	
	private static Main instance;
	
	@Override
	public void onEnable() 
	{
		firstStart();
		instance = this;
		MinG.init(this);
		game = new SkywarsGame(this);
		
		
//		kits = SkywarsKit.loadKits();
//		kitByName = new HashMap<String, SkywarsKit>();
//		for(SkywarsKit kit : kits) {
//			kitByName.put(kit.getName(), kit);
//		}

		new SWjoin(this);
		new SWleave(this);
		new SWedit(this);
		new SWmaps(this);
		new SWupdate(this);
		new SWchest(this);
//		new SWspawns(this);
		new SWset(this);
		
		new KitCommands(game, "swkits", "skywars.kits");
		new LangCommand(game, "swlang", "skywars.lang");
		new MarkerCommand(game, "swspawns", "sky_spawn");
		
//		new SWkits(game);
//		new SWlang(game);
		
		new StopLobbyLeave(this);
		new ChestEvents(this);
		new GameEvents(this);
		new KitEvents(this);

	}
	
	public void firstStart() {
		File resources = Util.getDataPackResources("skywars");
		File lootTables = new File(resources, "loot_tables");
		if(!resources.exists()) {
			lootTables.mkdirs();
			getLogger().info("[Skywars] Extracting datapack-");
			ResourceExtractor.extractResources("resources/loot_tables", lootTables.toPath(), true, false);
		}
		saveDefaultConfig();
		
		ResourceExtractor.extractResources("resources", getDataFolder().toPath(), true, false);
	}
	
	public static Main getInstance() {
		return instance;
	}
}
