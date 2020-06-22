package me.groot_23.skywars.game;

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

public class SkywarsGame extends MiniGame {

	public SkywarsGame(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public GameState<?> getStartingState(Arena arena) {
		SkywarsData data = new SkywarsData();
		data.arena = arena;
		data.deathMatchBegin = 30;
		data.deathMatchBorderShrinkTime = 10;
		data.refillTime = 20;
		data.refillTimeChange = 1;

		return new GameStateLobby(data);
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
