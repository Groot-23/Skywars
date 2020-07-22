package me.groot_23.skywars;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.skywars.commands.SWkits;
import me.groot_23.skywars.commands.SWlang;
import me.groot_23.ming.MinG;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.commands.KitCommands;
import me.groot_23.ming.commands.LangCommand;
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

public class Main extends JavaPlugin
{

	public static List<SkywarsKit> kits;
	public static Map<String, SkywarsKit> kitByName;
	public LanguageManager langManager;
//	public NMS nms;

	public static MiniGame game;
	
	private static Main instance;
	
	@Override
	public void onEnable() 
	{
		MinG.init(this);
		instance = this;
		game = new SkywarsGame(this);
		
		firstStart();
		
		kits = SkywarsKit.loadKits();
		kitByName = new HashMap<String, SkywarsKit>();
		for(SkywarsKit kit : kits) {
			kitByName.put(kit.getName(), kit);
		}
		
		
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
		
		new KitCommands(game, "swkits", "skywars.kits");
		new LangCommand(game, "swlang", "skywars.lang");
		
//		new SWkits(game);
//		new SWlang(game);
		
		new StopLobbyLeave(this);
		new ChestEvents(this);
		new GameEvents(this);
		new KitEvents(this);

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
