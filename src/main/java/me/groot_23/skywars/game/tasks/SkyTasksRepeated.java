package me.groot_23.skywars.game.tasks;


import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.pixel.display.BossBarApi;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.game.task.PixelTaskDelayed;
import me.groot_23.pixel.game.task.PixelTaskRepeated;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.SkyArena;
import net.md_5.bungee.api.ChatColor;

public class SkyTasksRepeated {
	public abstract static class Base extends PixelTaskRepeated {
		public SkyGame game;
		public SkyArena arena;
		
		public Base(Game game, long tickRate) {
			super(tickRate);
			this.game = (SkyGame) game;
			arena = this.game.skyArena;
		}
	}
	
	public static class Game20 extends Base {

		public Game20(Game game) {
			super(game, 5);
			for(Player player : game.players) {
				SkywarsScoreboard.initGame(player, this.game.teamHandler.getTeamsAliveCount(),
						LanguageKeys.EVENT_START, 10, this.game.deathMatchBegin, SWconstants.LENGTH_OF_GAME,
						arena.getMapName(), game.name, this.game.teamHandler.teamSize > 1);
			}
		}

		@Override
		protected void onUpdate() {
			PixelTaskDelayed task = game.taskManager.getTask(SkyTasksDelayed.Refill.id);
			PixelTaskDelayed deathMatch = game.taskManager.getTask(SkyTasksDelayed.DeathMatch.id);
			int deathMatchTime = (deathMatch != null) ? (int)deathMatch.getRemainingSeconds() : game.deathMatchBegin;
			String nextEvent = LanguageKeys.EVENT_REFILL;
			if(task == null) {
				task = game.taskManager.getTask(SkyTasksDelayed.StartGame.id);
				nextEvent = LanguageKeys.EVENT_START;
			} else {
				arena.updateChestTimer(task.getRemainingSeconds());
			}
			PixelTaskDelayed draw = game.taskManager.getTask(SkyTasksDelayed.Draw.id);
			int secondsUntilDraw = (draw != null) ? draw.getRemainingSeconds() : SWconstants.LENGTH_OF_GAME;
			
			SkywarsScoreboard.updateGame(arena.getWorld(), game.teamHandler.getTeamsAliveCount(), nextEvent,
					task.getRemainingSeconds(), deathMatchTime, secondsUntilDraw);
		}
		
	}
	
	public static class Game1 extends Base {

		BossBar bb;
		public Game1(Game game) {
			super(game, 5);
			bb = Bukkit.createBossBar(
					ChatColor.LIGHT_PURPLE + "SKYWARS",
					BarColor.RED, BarStyle.SOLID);
			bb.setProgress(1);
			for(Player player : game.players) {
				BossBarApi.addPlayer(bb, player);
			}
		}

		@Override
		protected void onUpdate() {
			PixelTaskDelayed deathMatch = game.taskManager.getTask(SkyTasksDelayed.DeathMatch.id);
			if(deathMatch != null) {
				bb.setTitle(LanguageApi.getDefault(LanguageKeys.BOSSBAR_TITLE) + " " + ChatColor.WHITE + ChatColor.BOLD
					+ Util.minuteSeconds(deathMatch.getRemainingSeconds()));
				bb.setProgress(deathMatch.getRemainingProgress());
			} 
		}
	}
}
