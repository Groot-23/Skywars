package me.groot_23.skywars.game.states;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.display.BossBarManager;
import me.groot_23.ming.game.GameState;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import net.md_5.bungee.api.ChatColor;

public class GameStateLobby extends GameState<SkywarsData> {

	public GameStateLobby(GameState<SkywarsData> state) {
		super(state);
	}

	public GameStateLobby(SkywarsData data, MiniGame game) {
		super(data, game);
	}

	private int counter = 30;
	private BossBar bb;

	@Override
	public void onStart() {
		bb = Bukkit.createBossBar(game.getDefaultTranslation(LanguageKeys.BOSSBAR_START), BarColor.BLUE,
				BarStyle.SOLID);
		bb.setProgress(1);
		data.arena.initBorder();
		data.arena.refillChests();
	}

	@Override
	protected GameState<SkywarsData> onUpdate() {
		bb.setProgress((double) counter / 30);
		bb.setTitle(game.getDefaultTranslation(LanguageKeys.BOSSBAR_START) + " " + ChatColor.YELLOW + ChatColor.BOLD
				+ counter + " " + game.getDefaultTranslation(LanguageKeys.SECONDS).toUpperCase());
		SkywarsScoreboard.updatePreGame(data.arena.getWorld(), data.arena.getMaxPlayers(), counter);
		int numPlayers = data.arena.getWorld().getPlayers().size();
		for (Player player : data.arena.getWorld().getPlayers()) {
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setSaturation(5);
			BossBarManager.addPlayer(bb, player);
		}
		if (numPlayers == data.arena.getMaxPlayers() || counter <= 0) {
			return new GameStateSpawn(this);
		}
		if (numPlayers < data.arena.getMinPlayers()) {
			counter = 30;
		} else {
			counter--;
		}
		return this;
	}

	@Override
	protected void onEnd() {
		for (Player p : data.arena.getWorld().getPlayers()) {
			BossBarManager.removePlayer(p);
		}
	}

}
