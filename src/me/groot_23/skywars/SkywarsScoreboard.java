package me.groot_23.skywars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.groot_23.skywars.util.Util;

public class SkywarsScoreboard {
	
	private Main plugin;
	
	public SkywarsScoreboard(Main plugin) {
		this.plugin = plugin;
	}
	
	public void init(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		
		Objective objective = board.registerNewObjective("skywars", "dummy", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		player.setScoreboard(board);
	}
	
	public Objective resetObjective(Player player) {
		Scoreboard board = player.getScoreboard();
		Objective objective = board.getObjective("skywars");
		objective.unregister();
		
		objective = board.registerNewObjective("skywars", "dummy", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		return objective;
	}
	
	public void resetKills(Player player) {
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, 0));
	}
	
	public void addKill(Player player) {
		int kills = player.getMetadata("Skywars_kills").get(0).asInt();
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, kills + 1));
	}
	
	public void updatePreGame(World world, int maxPlayers, int timeLeft) {
		int playerCount = world.getPlayers().size();
		for(Player player : world.getPlayers()) {
			Objective objective = resetObjective(player);
			
			objective.getScore(Util.repeat(20, " ")).setScore(6);
			objective.getScore(Integer.toString(playerCount) + "/" + maxPlayers + " Spieler").setScore(5);
			objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(4);
			objective.getScore("Zeit bis zum Start: " + timeLeft).setScore(3);
			objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(2);
			objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
			
		}
	}
	
	public void updateGame(World world, int playersLeft, String nextEvent, int timeTillEvent) {

		for(Player player : world.getPlayers()) {
			Objective objective = resetObjective(player);
			
			int kills = player.getMetadata("Skywars_kills").get(0).asInt();
			
			objective.getScore(Util.repeat(20, " ")).setScore(9);
			objective.getScore(ChatColor.GREEN + "Nächstes Event:").setScore(8);
			objective.getScore(nextEvent + ": " + Util.minuteSeconds(timeTillEvent)).setScore(7);
			objective.getScore(ChatColor.BLUE + Util.repeat(20, " ")).setScore(6);
			objective.getScore("Players left: " + playersLeft).setScore(5);
			objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(4);
			objective.getScore(ChatColor.RED + "Kills: " + kills).setScore(3);
			objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(2);
			objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
			
		}
	}
		
}
