package me.groot_23.skywars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.groot_23.skywars.util.Util;

public class SkywarsScoreboard {
	
	private Main plugin;
	
	private Scoreboard board;
	private Objective objective;
	
	public SkywarsScoreboard(Main plugin) {
		this.plugin = plugin;
		init();
	}
	
	public void init() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		
		objective = board.registerNewObjective("skywars", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "Skywars");
	}
	
	public void resetObjective() {
		objective.unregister();
		
		objective = board.registerNewObjective("skywars", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "Skywars");
	}
	
	
	public void addKill(Player player) {
		int kills = player.getMetadata("Skywars_kills").get(0).asInt();
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, kills + 1));
	}
	
	public void updatePreGame(World world, int maxPlayers, int timeLeft) {
		int playerCount = world.getPlayers().size();
		for(Player player : world.getPlayers()) {
			resetObjective();
			
			objective.getScore(Util.repeat(20, " ")).setScore(9);
			objective.getScore(Integer.toString(playerCount) + "/" + maxPlayers + " Spiler").setScore(8);;
			objective.getScore(Util.repeat(20, " ")).setScore(7);
			objective.getScore("Zeit bis zum Start: " + timeLeft).setScore(6);
			objective.getScore(Util.repeat(20, " ")).setScore(5);
			
			board.resetScores(player);
			player.setScoreboard(board);
		}
	}
	
	public void updateGame(World world) {
		int playersLeft = 0;
		for(Player p : world.getPlayers()) {
			if(p.getGameMode() == GameMode.SURVIVAL) {
				playersLeft++;
			}
		}
		
		for(Player player : world.getPlayers()) {
			resetObjective();
			// TODO
			int kills = 0;
			
			objective.getScore(Util.repeat(20, " ")).setScore(9);
			objective.getScore("Players left: " + playersLeft).setScore(8);;
			objective.getScore(Util.repeat(20, " ")).setScore(7);
			objective.getScore(ChatColor.RED + "Kills: " + kills).setScore(6);
			objective.getScore(Util.repeat(20, " ")).setScore(5);
			
			board.resetScores(player);
			player.setScoreboard(board);
		}
	}
		
}
