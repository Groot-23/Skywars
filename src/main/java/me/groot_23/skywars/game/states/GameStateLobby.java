package me.groot_23.skywars.game.states;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.ming.display.BossBarManager;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.GameState;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import net.md_5.bungee.api.ChatColor;

public class GameStateLobby extends SkyGameState {

	public GameStateLobby(GameState<SkywarsData> state) {
		super(state);
	}

	public GameStateLobby(SkywarsData data, Game game) {
		super(data, game);
	}

	private int counter = 30;
	private BossBar bb;

	@Override
	public void onStart() {
		bb = Bukkit.createBossBar(miniGame.getDefaultTranslation(LanguageKeys.BOSSBAR_START), BarColor.BLUE,
				BarStyle.SOLID);
		bb.setProgress(1);
		arena.initBorder();
		arena.refillChests();
		world.setPVP(false);
	}

	@Override
	protected GameState<SkywarsData> onUpdate() {
		bb.setProgress((double) counter / 30);
		bb.setTitle(miniGame.getDefaultTranslation(LanguageKeys.BOSSBAR_START) + " " + ChatColor.YELLOW + ChatColor.BOLD
				+ counter + " " + miniGame.getDefaultTranslation(LanguageKeys.SECONDS).toUpperCase());
		SkywarsScoreboard.updatePreGame(arena.getWorld(), arena.getMaxPlayers(), counter);
		int numPlayers = arena.getWorld().getPlayers().size();
		for (Player player : arena.getWorld().getPlayers()) {
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setSaturation(5);
			BossBarManager.addPlayer(bb, player);
		}
		if (numPlayers == arena.getMaxPlayers() || counter <= 0) {
			return new GameStateSpawn(this);
		}
		if (numPlayers < arena.getMinPlayers()) {
			counter = 30;
		} else {
			counter--;
		}
		return this;
	}

	@Override
	protected void onEnd() {
		for (Player p : arena.getWorld().getPlayers()) {
			BossBarManager.removePlayer(p);
		}
	}

}
