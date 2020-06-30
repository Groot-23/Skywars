package me.groot_23.skywars.game.states;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.groot_23.ming.game.GameState;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.util.Util;

public class GameStateVictory extends GameState<SkywarsData>{

	private Player winner;
	
	public GameStateVictory(GameState<SkywarsData> state, Player winner) {
		super(state);
		this.winner = winner;
	}

	int counter = 15;
	
	@Override
	public void onStart() {
		for (Player player : data.arena.getWorld().getPlayers()) {
			player.sendTitle(Util.chat("&c" + winner.getName()), ChatColor.DARK_PURPLE + 
					game.getTranslation(player, LanguageKeys.VICTORY), 3, 50, 3);
			player.setGameMode(GameMode.SPECTATOR);
		}
	}

	@Override
	public GameState<SkywarsData> onUpdate() {
		if (counter-- <= 0) {
			return null;
		}
		return this;
	}

}
