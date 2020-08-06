package me.groot_23.skywars.game.tasks;


import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.ming.MinG;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.display.BossBarManager;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.task.GameTaskDelayed;
import me.groot_23.ming.game.task.GameTaskRepeated;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.SkyArena;
import net.md_5.bungee.api.ChatColor;

public class SkyTasksRepeated {
	public abstract static class Base extends GameTaskRepeated {
		public MiniGame miniGame;
		public SkyGame skyGame;
		public SkyArena arena;
		public Base(Game game, long tickRate) {
			super(game, tickRate);
			miniGame = game.miniGame;
			skyGame = (SkyGame) game;
			arena = skyGame.skyArena;
		}
	}
	
	public static class Lobby20 extends Base {
		public Lobby20(Game game) {
			super(game, 20);
		}

		@Override
		protected void onUpdate() {
			GameTaskDelayed task = game.taskManager.getTask(SkyTasksDelayed.GoToSpawn.id);
			if(task != null) {
				SkywarsScoreboard.updatePreGame(arena.getWorld(), arena.getMaxPlayers(), task.getRemainingSeconds());
				for (Player player : arena.getWorld().getPlayers()) {
					player.setHealth(20);
					player.setFoodLevel(20);
					player.setSaturation(5);
				}
			}
		}
		

	}
	
	public static class Lobby1 extends Base {
		BossBar bb;
		public Lobby1(Game game) {
			super(game, 1);
			bb = Bukkit.createBossBar(MinG.getLanguageManager().getDefault(LanguageKeys.BOSSBAR_START), BarColor.BLUE,
					BarStyle.SOLID);
			bb.setProgress(1);
		}
		
		@Override
		protected void onUpdate() {
			GameTaskDelayed task = game.taskManager.getTask(SkyTasksDelayed.GoToSpawn.id);
			if(task != null) {
				bb.setProgress(task.getRemainingProgress());
				bb.setTitle(MinG.getLanguageManager().getDefault(LanguageKeys.BOSSBAR_START) + " " + ChatColor.YELLOW + ChatColor.BOLD
						+ task.getRemainingSeconds() + " " + MinG.getLanguageManager().getDefault(LanguageKeys.SECONDS).toUpperCase());
				for (Player player : arena.getWorld().getPlayers()) {
					BossBarManager.addPlayer(bb, player);
				}
			}
		}
		
		@Override
		protected void onStop() {
			for (Player p : arena.getWorld().getPlayers()) {
				BossBarManager.removePlayer(p);
			}
		}
	}
	
	public static class Game20 extends Base {

		public Game20(Game game) {
			super(game, 5);
			for(Player player : game.players) {
				SkywarsScoreboard.initGame(player, skyGame.teamHandler.getTeamsAliveCount(),
						LanguageKeys.EVENT_START, 10, skyGame.deathMatchBegin, SWconstants.LENGTH_OF_GAME,
						arena.getMapName(), game.mode.getName());
			}
		}

		@Override
		protected void onUpdate() {
			GameTaskDelayed task = game.taskManager.getTask(SkyTasksDelayed.Refill.id);
			GameTaskDelayed deathMatch = game.taskManager.getTask(SkyTasksDelayed.DeathMatch.id);
			int deathMatchTime = (deathMatch != null) ? (int)deathMatch.getRemainingSeconds() : skyGame.deathMatchBegin;
			String nextEvent = LanguageKeys.EVENT_REFILL;
			if(task == null) {
				task = game.taskManager.getTask(SkyTasksDelayed.StartGame.id);
				nextEvent = LanguageKeys.EVENT_START;
			} else {
				arena.updateChestTimer(task.getRemainingSeconds());
			}
			GameTaskDelayed draw = game.taskManager.getTask(SkyTasksDelayed.Draw.id);
			int secondsUntilDraw = (draw != null) ? draw.getRemainingSeconds() : SWconstants.LENGTH_OF_GAME;
			
			SkywarsScoreboard.updateGame(arena.getWorld(), skyGame.teamHandler.getTeamsAliveCount(), nextEvent,
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
				BossBarManager.addPlayer(bb, player);
			}
		}

		@Override
		protected void onUpdate() {
			GameTaskDelayed deathMatch = game.taskManager.getTask(SkyTasksDelayed.DeathMatch.id);
			if(deathMatch != null) {
				bb.setTitle(MinG.getLanguageManager().getDefault(LanguageKeys.BOSSBAR_TITLE) + " " + ChatColor.WHITE + ChatColor.BOLD
					+ Util.minuteSeconds(deathMatch.getRemainingSeconds()));
				bb.setProgress(deathMatch.getRemainingProgress());
			} else {
				GameTaskDelayed enablePVP = game.taskManager.getTask(SkyTasksDelayed.EnablePVP.id);
				if(enablePVP != null) {
					bb.setTitle(ChatColor.RED + "SCHUTZ Zeit" + ChatColor.WHITE + " " 
							+ Util.minuteSeconds(enablePVP.getRemainingSeconds()));
						bb.setProgress(enablePVP.getRemainingProgress());
				}
			}
		}
		
	}
}
