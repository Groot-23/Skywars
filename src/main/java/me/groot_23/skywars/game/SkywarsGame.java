package me.groot_23.skywars.game;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.GameState;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.events.KitEvents;
import me.groot_23.skywars.game.states.GameStateLobby;
import me.groot_23.skywars.world.SkyArena;

public class SkywarsGame extends MiniGame {

	public SkywarsGame(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public GameState<?> getStartingState(Arena arena) {
		SkywarsData data = new SkywarsData();
		data.arena = (SkyArena) arena;
		data.deathMatchBegin = plugin.getConfig().getInt("deathMatchBegin");
		data.deathMatchBorderShrinkTime = plugin.getConfig().getInt("deathMatchBorderShrinkTime");
		data.refillTime = plugin.getConfig().getInt("refillTime");
		data.refillTimeChange = plugin.getConfig().getInt("refillTimeChange");

		return new GameStateLobby(data);
	}
	
	@Override
	public Arena createArena(World world, String map) {
		return new SkyArena(this, world, map);
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

}
