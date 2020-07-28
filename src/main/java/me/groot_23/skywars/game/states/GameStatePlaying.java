package me.groot_23.skywars.game.states;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.ming.display.BossBarManager;
import me.groot_23.ming.game.GameState;
import me.groot_23.ming.player.GameTeam;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameStatePlaying extends SkyGameState{

	public GameStatePlaying(SkyGameState state) {
		super(state);
	}
	
	int counter;
	int refillCounter;
	int dynamicRefillTime;
	int deathMatchCounter;

	BossBar bossbar;
	
	@Override
	public void onStart() {
		counter = SWconstants.LENGTH_OF_GAME;
		refillCounter = data.refillTime;
		dynamicRefillTime = refillCounter;
		deathMatchCounter = data.deathMatchBegin;

		arena.removeGlassSpawns();
		world = arena.getWorld();
		bossbar = Bukkit.createBossBar(
				miniGame.getDefaultTranslation(LanguageKeys.BOSSBAR_TITLE),
				BarColor.PURPLE, BarStyle.SEGMENTED_20);
		bossbar.setProgress(1);
		world.setPVP(true);
		for (Player player : world.getPlayers()) {
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.setFallDistance(-1000);
			
			String started = ChatColor.GREEN + miniGame.getTranslation(player, LanguageKeys.STARTED);
			player.sendMessage(started);
			player.sendTitle(started, ChatColor.LIGHT_PURPLE + miniGame.getTranslation(player, 
					LanguageKeys.FIGHT_BEGINS), 3, 20, 3);
			
			BossBarManager.addPlayer(bossbar, player);
			
			Main.game.applyKitToPlayer(player);
			
		}
	}

	@Override
	protected GameState<SkywarsData> onUpdate() {
		List<GameTeam> teamsLeft = game.getTeamsAlive();
//		int playersLeft = 0;
//		Player potentialWinner = null;
		for (Player p : world.getPlayers()) {
			if (p.getGameMode() == GameMode.SURVIVAL) {
//				playersLeft++;
//				potentialWinner = p;
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + 
								Main.game.getTranslation(p, LanguageKeys.NO_TEAMING)));
			}
		}
		SkywarsScoreboard.updateGame(world, teamsLeft.size(), LanguageKeys.EVENT_REFILL, refillCounter,
				deathMatchCounter, counter);
		if (teamsLeft.size() == 1) {
			bossbar.removeAll();
			return new GameStateVictory(this, teamsLeft.get(0));
		} else if(teamsLeft.size() == 0) {
			// cancel game
			return null;
		}
		arena.updateChestTimer(refillCounter);
		if (refillCounter <= 0) {
			dynamicRefillTime += data.refillTimeChange;
			refillCounter = dynamicRefillTime;
			arena.refillChests();
		}
		if (deathMatchCounter == 1) {
			arena.shrinkBorder(data.deathMatchBorderShrinkTime);
			for (Player p : world.getPlayers()) {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Death Match!", ChatColor.RED + ""
						+ ChatColor.BOLD + miniGame.getTranslation(p, LanguageKeys.GO_TO_MID), 3, 100, 3);
			}
		} else {
			bossbar.setProgress((double) deathMatchCounter / data.deathMatchBegin);
			bossbar.setTitle(miniGame.getDefaultTranslation(LanguageKeys.BOSSBAR_TITLE) + " " + Util.minuteSeconds(deathMatchCounter));
		}
//		if(counter <= 0) {
//			draw();
//			return null;
//		}
		counter--;
		refillCounter--;
		if (deathMatchCounter > 0) {
			deathMatchCounter--;
		}
		return this;
	}

}
