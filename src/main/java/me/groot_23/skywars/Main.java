package me.groot_23.skywars;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import me.groot_23.skywars.commands.SWjoin;
import me.groot_23.ming.MinG;
import me.groot_23.ming.commands.KitCommands;
import me.groot_23.ming.commands.LangCommand;
import me.groot_23.ming.commands.MarkerCommand;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.GameCreator;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.language.LanguageHolder;
import me.groot_23.ming.util.ResourceExtractor;
import me.groot_23.ming.util.Utf8Config;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.commands.SWchest;
import me.groot_23.skywars.commands.SWedit;
import me.groot_23.skywars.commands.SWforce;
import me.groot_23.skywars.commands.SWleave;
import me.groot_23.skywars.commands.SWmaps;
import me.groot_23.skywars.commands.SWset;
import me.groot_23.skywars.commands.SWstart;
import me.groot_23.skywars.commands.SWupdate;
import me.groot_23.skywars.commands.ToggleSpectator;
import me.groot_23.skywars.events.GameEvents;
import me.groot_23.skywars.events.KitEvents;
import me.groot_23.skywars.events.ChestEvents;
import me.groot_23.skywars.events.StopLobbyLeave;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.util.Util;

public class Main extends JavaPlugin
{

	public static LanguageHolder langHolder;
	
	private static Main instance;
	
	@Override
	public void onEnable() 
	{
		firstStart();
		instance = this;
		
		MinG.registerGame("skywars-solo", new GameCreator() {
			@Override
			public Game createGame(String options) {
				return new SkyGame("skywars-solo", options, 1);
			}
		});
		MinG.registerGame("skywars-duo", new GameCreator() {
			@Override
			public Game createGame(String options) {
				return new SkyGame("skywars-duo", options, 2);
			}
		});
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(new File(this.getDataFolder(), "groups.yml"));
			for(String s : cfg.getStringList("default")) {
				MinG.registerGameOption("skywars-solo", s);
				MinG.registerGameOption("skywars-duo", s);
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		langHolder = MinG.getLanguageManager().addLanguageHolder(new File(getDataFolder(), "lang"));
		
		File kitFile = new File(getDataFolder(), "kits.yml");
		MinG.loadKits(kitFile, "skywars");

		new SWjoin(this);
		new SWleave(this);
		new SWedit(this);
		new SWmaps(this);
		new SWupdate(this);
		new SWchest(this);
//		new SWspawns(this);
		new SWset(this);
		new SWforce(this);
		new SWstart(this);
		
		new ToggleSpectator(this);
		
		new KitCommands(this, langHolder, kitFile, "skywars", "swkits", "skywars.kits");
		new LangCommand(this, langHolder, "swlang", "skywars.lang");
		new MarkerCommand(this, "swspawns", "sky_spawn");
		
//		new SWkits(game);
//		new SWlang(game);
		
		new StopLobbyLeave(this);
		new ChestEvents(this);
		new GameEvents(this);
		new KitEvents(this);
		
		MinG.registerGuiRunnable("openGui", new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv) {
				KitEvents.openGui(player);
			}
		});
		MinG.registerGuiRunnable("open_kit_selector", new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv) {
				Arena arena = MinG.getArena(player.getWorld().getUID());
				if (arena != null) {
					if (arena.getGame() instanceof SkyGame) {
						player.openInventory(((SkyGame) arena.getGame()).teamHandler.getTeamSelectorInv());
					}
				}
			}
		});

	}
	
	public void firstStart() {
		File resources = Util.getDataPackResources("skywars");
		File lootTables = new File(resources, "loot_tables");
		if(!resources.exists()) {
			lootTables.mkdirs();
			getLogger().info("[Skywars] Extracting datapack-");
			ResourceExtractor.extractResources("resources/loot_tables", lootTables.toPath(), false, this.getClass());
		}
		saveDefaultConfig();
		
		ResourceExtractor.extractResources("resources", getDataFolder().toPath(), false, this.getClass());
	}
	
	public static Main getInstance() {
		return instance;
	}
}
