package me.groot_23.skywars.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.display.BossBarManager;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.task.GameTaskDelayed;
import me.groot_23.ming.player.GameTeam;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.SkyArena;

public class SkyTasksDelayed {
	
	public static abstract class Base extends GameTaskDelayed {
		// shortcuts
		public SkyGame skyGame;
		public SkyArena arena;
		public World world;
		public MiniGame miniGame;
		
		public Base(Game game, long delay) {
			super(game, delay);
			this.skyGame = (SkyGame) game;
			this.arena = skyGame.skyArena;
			this.world = arena.getWorld();
			this.miniGame = game.miniGame;
		}
	}
	
	public static class GoToSpawn extends Base {
		public static final String id = "goToSpawn";
		public GoToSpawn(Game game, long delay) {
			super(game, delay);
		}

		@Override
		public void run() {
			skyGame.teamHandler.fillTeams(skyGame.players);
			int i = 0;
			for(GameTeam team : skyGame.teamHandler.getTeams()) {
				for(Player p : team.getPlayers()) {
					p.teleport(arena.getSpawns().get(i));
					// remove team selector if present!
					p.getInventory().setItem(0, null);
				}
				i++;
			}
			arena.removeLobby();
			
			game.stopJoin();
			
			game.taskManager.removeRepeated("lobby1");
			game.taskManager.removeRepeated("lobby20");
			game.taskManager.addRepeated(new SkyTasksRepeated.Game1(game), "game1");
			game.taskManager.addRepeated(new SkyTasksRepeated.Game20(game), "game20");
			
			game.taskManager.addTask(new StartGame(game, 10 * 20), StartGame.id);
		}
		
	}
	
	public static class StartGame extends Base {
		public static final String id = "startGame";
		public StartGame(Game game, long delay) {
			super(game, delay);
		}

		@Override
		public void run() {
			arena.removeGlassSpawns();
			world.setPVP(true);
			for (Player player : world.getPlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				// remove fall damage
				player.setFallDistance(-1000);
				Main.game.applyKitToPlayer(player);
				
				String started = ChatColor.GREEN + miniGame.getTranslation(player, LanguageKeys.STARTED);
				player.sendMessage(started);
				player.sendTitle(started, ChatColor.LIGHT_PURPLE + miniGame.getTranslation(player, 
						LanguageKeys.FIGHT_BEGINS), 3, 20, 3);
			}
			
			game.taskManager.addTask(new Draw(game, SWconstants.LENGTH_OF_GAME * 20), Draw.id);
			game.taskManager.addTask(new DeathMatch(game, skyGame.deathMatchBegin * 20), DeathMatch.id);
			game.taskManager.addTask(new Refill(game, skyGame.refillTime * 20), Refill.id);
		}
	}
	
	public static class DeathMatch extends Base {
		public static final String id = "deathMatch";
		public DeathMatch(Game game, long delay) {
			super(game, delay);
		}
		
		@Override
		public void run() {
			arena.shrinkBorder(skyGame.deathMatchBorderShrinkTime);
			game.taskManager.getRepeated("game1").stop();
		}
	}
	
	public static class Refill extends Base {
		public static final String id = "refill";
		public Refill(Game game, long delay) {
			super(game, delay);
		}

		@Override
		public void run() {
			arena.refillChests();
			game.taskManager.addTask(new Refill(game, delay + skyGame.refillTimeChange * 20), id);
		}
	}
	
	public static class Draw extends Base {
		public static final String id = "draw";
		public Draw(Game game, long delay) {
			super(game, delay);
		}

		@Override
		public void run() {
			for(Player p : game.players) {
				p.sendTitle("DRAW", "", 8, 30, 8);
			}
			game.taskManager.addTask(new EndGame(game, 200), EndGame.id);
		}
	}
	
	public static class Victory extends Base {
		public static final String id = "victory";
		public final GameTeam winner;
		public Victory(Game game, long delay, GameTeam winner) {
			super(game, delay);
			this.winner = winner;
		}
		@Override
		public void run() {
			for (Player player : world.getPlayers()) {
				player.sendTitle(Util.chat(winner.getColor() + "Team " + winner.getColor().name()), ChatColor.DARK_PURPLE + 
						miniGame.getTranslation(player, LanguageKeys.VICTORY), 3, 50, 3);
				player.setGameMode(GameMode.SPECTATOR);
			}
			game.taskManager.addTask(new EndGame(game, 200), EndGame.id);
		}
	}
	
	public static class EndGame extends Base {
		public static final String id = "endGame";
		public EndGame(Game game, long delay) {
			super(game, delay);
		}
		@Override
		public void run() {
			game.endGame();
		}
		
	}
}
