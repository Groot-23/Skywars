package me.groot_23.skywars.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.display.BossBarApi;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.game.task.GameTaskDelayed;
import me.groot_23.pixel.kits.Kit;
import me.groot_23.pixel.kits.KitApi;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.player.team.GameTeam;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.world.SkyArena;

public class SkyTasksDelayed {
	
	public static abstract class Base extends GameTaskDelayed {
		// shortcuts
		public SkyGame skyGame;
		public SkyArena arena;
		public World world;
		
		public Base(Game game, long delay) {
			super(game, delay);
			this.skyGame = (SkyGame) game;
			this.arena = skyGame.skyArena;
			this.world = arena.getWorld();
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
					p.sendTitle(ChatColor.GOLD + "Map" + SkywarsScoreboard.COLON + ChatColor.WHITE +
							LanguageApi.getTranslation(p, "map." + arena.getMapName()), 
							ChatColor.AQUA + "Builder" + SkywarsScoreboard.COLON + ChatColor.WHITE + arena.getBuilder(),
							10, 80, 10);
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
			game.arena.getWorld().setPVP(true);
			for (Player player : world.getPlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				// remove fall damage
				player.setFallDistance(-1000);
				Kit kit = KitApi.getSelectedKit(player, "skywars");
				if(kit == null) {
					kit = KitApi.getKits("skywars").get(0);
				}
				kit.applyToPlayer(player);
				
				String started = ChatColor.GREEN + LanguageApi.getTranslation(player, LanguageKeys.STARTED);
				player.sendMessage(started);
				player.sendTitle(started, ChatColor.LIGHT_PURPLE + LanguageApi.getTranslation(player, 
						LanguageKeys.FIGHT_BEGINS), 3, 20, 3);
			}
			
			game.taskManager.addTask(new Draw(game, SWconstants.LENGTH_OF_GAME * 20), Draw.id);
//			game.taskManager.addTask(new EnablePVP(game, 30 * 20), EnablePVP.id);
			game.taskManager.addTask(new Refill(game, skyGame.refillTime * 20), Refill.id);
			game.taskManager.addTask(new DeathMatch(game, skyGame.deathMatchBegin * 20), DeathMatch.id);
		}
	}
	
//	public static class EnablePVP extends Base {
//		public static String id = "enablePVP";
//		public EnablePVP(Game game, long delay) {
//			super(game, delay);
//		}
//		
//		@Override
//		public void run() {
//			game.arena.getWorld().setPVP(true);
//			for(Player p : game.players) {
//				p.sendTitle(ChatColor.RED + "SCHUTZ VORBEI", "", 5, 30, 5);
//			}
//			game.taskManager.addTask(new DeathMatch(game, skyGame.deathMatchBegin * 20), DeathMatch.id);
//		}
//	}
	
	public static class DeathMatch extends Base {
		public static final String id = "deathMatch";
		public DeathMatch(Game game, long delay) {
			super(game, delay);
		}
		
		@Override
		public void run() {
			arena.shrinkBorder(skyGame.deathMatchBorderShrinkTime);
			BossBar newbb = Bukkit.createBossBar(ChatColor.YELLOW + "Überlebe das " + ChatColor.DARK_RED + "Death Match" + 
					ChatColor.GRAY + "!", BarColor.RED, BarStyle.SOLID);
			for (Player p : game.players) {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Death Match!", ChatColor.RED + ""
						+ ChatColor.BOLD + LanguageApi.getTranslation(p, LanguageKeys.GO_TO_MID), 3, 100, 3);
				BossBarApi.removePlayer(p);
				BossBarApi.addPlayer(newbb, p);
			}
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
				player.sendTitle(GameTeam.toChatColor(winner.getColor()) + "Team " + LanguageApi.translateColor(player, winner.getColor()),
						ChatColor.DARK_PURPLE + LanguageApi.getTranslation(player, LanguageKeys.VICTORY), 3, 50, 3);
				Pixel.setSpectator(player, true);
			}
			game.taskManager.removeTask(Draw.id);
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
