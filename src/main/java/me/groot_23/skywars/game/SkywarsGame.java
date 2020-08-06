package me.groot_23.skywars.game;

import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.skywars.game.modes.SkyModeClassic;

public class SkywarsGame extends MiniGame {

	public SkywarsGame(JavaPlugin plugin) {
		super(plugin);
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
