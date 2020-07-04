package me.groot_23.skywars.game;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.skywars.events.KitEvents;
import me.groot_23.skywars.game.modes.SkyModeClassic;

public class SkywarsGame extends MiniGame {

	public SkywarsGame(JavaPlugin plugin) {
		super(plugin);
	}


	@Override
	public void registerGuiRunnables() {
		super.registerGuiRunnables();
		registerGuiRunnable("openGui", new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv, MiniGame game) {
				KitEvents.openGui(player);
			}
		});
		registerGuiRunnable("open_kit_selector", new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv, MiniGame game) {
				Game g = getGameById(player.getWorld().getUID());
				if(g != null) {
					player.openInventory(g.getTeamSelectorInv());
				}
			}
		});
	}

	@Override
	public String getWorldPrefix() {
		return "skywars_gameworld_";
	}

	@Override
	public String getDefaultLanguage() {
		return "de_de";
	}

	@Override
	public String getName() {
		return "skywars";
	}

	private MiniGameMode defaultMode;

	@Override
	public void registerModes() {
		defaultMode = new SkyModeClassic(this, 1, "solo");
		registerMode(defaultMode);
		registerMode(new SkyModeClassic(this, 2, "duo"));
	}


	@Override
	public MiniGameMode getDefaultMode() {
		return defaultMode;
	}

}
