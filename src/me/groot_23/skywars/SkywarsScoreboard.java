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
		if(player.getMetadata("Skywars_kills").isEmpty()) {
			player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, 0));
		}
		int kills = player.getMetadata("Skywars_kills").get(0).asInt() + 1;
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, kills + 1));
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		replaceScore(objective,  5, ChatColor.RED + "Kills: " + kills);
	}
	
	
	public static String getEntryFromScore(Objective o, int score) {
	    if(o == null) return null;
	    if(!hasScoreTaken(o, score)) return null;
	    for (String s : o.getScoreboard().getEntries()) {
	        if(o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
	    }
	    return null;
	}

	public static boolean hasScoreTaken(Objective o, int score) {
	    for (String s : o.getScoreboard().getEntries()) {
	        if(o.getScore(s).getScore() == score) return true;
	    }
	    return false;
	}

	public static void replaceScore(Objective o, int score, String name) {
	    if(hasScoreTaken(o, score)) {
	        if(getEntryFromScore(o, score).equalsIgnoreCase(name)) return;
	        if(!(getEntryFromScore(o, score).equalsIgnoreCase(name))) o.getScoreboard().resetScores(getEntryFromScore(o, score));
	    }
	    o.getScore(name).setScore(score);
	}
	
	public void initPreGame(Player player, int maxPlayers, int timeLeft, String map) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		int playerCount = player.getWorld().getPlayers().size();
		
		objective.getScore(Util.repeat(20, " ")).setScore(8);
		objective.getScore(Integer.toString(playerCount) + "/" + maxPlayers + " Spieler").setScore(7);
		objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(6);
		objective.getScore("Zeit bis zum Start: " + timeLeft).setScore(5);
		objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(4);
		objective.getScore(ChatColor.GOLD + "Map: " + ChatColor.WHITE + map).setScore(3);
		objective.getScore(ChatColor.AQUA + Util.repeat(20, " ")).setScore(2);
		objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
	}

	public void updatePreGame(World world, int maxPlayers, int timeLeft) {
		int playerCount = world.getPlayers().size();
		for(Player player : world.getPlayers()) {
//			Objective objective = resetObjective(player);
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			
//			replaceScore(objective, 8, Util.repeat(20, " "));
			replaceScore(objective, 7, Integer.toString(playerCount) + "/" + maxPlayers + " Spieler");
//			replaceScore(objective, 6, ChatColor.GREEN + Util.repeat(20, " "));
			replaceScore(objective, 5, "Zeit bis zum Start: " + timeLeft);
//			replaceScore(objective, 4, ChatColor.RED + Util.repeat(20, " "));
//			replaceScore(objective, 3, ChatColor.GRAY + "Map: " + ChatColor.WHITE + map);
//			replaceScore(objective, 2, ChatColor.AQUA + Util.repeat(20, " "));
//			replaceScore(objective, 1, ChatColor.YELLOW + "Groot23.mcserv.me");
			
//			objective.getScore(Util.repeat(20, " ")).setScore(8);
//			objective.getScore(Integer.toString(playerCount) + "/" + maxPlayers + " Spieler").setScore(7);
//			objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(6);
//			objective.getScore("Zeit bis zum Start: " + timeLeft).setScore(5);
//			objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(4);
//			objective.getScore(ChatColor.GRAY + "Map: " + ChatColor.WHITE + map).setScore(3);
//			objective.getScore(ChatColor.AQUA + Util.repeat(20, " ")).setScore(2);
//			objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
			
		}
	}
	
	public void initGame(Player player, int playersLeft, String nextEvent, int timeTillEvent, int remainingTime, String map) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		
		replaceScore(objective, 13, Util.repeat(20, " "));
		replaceScore(objective, 12, ChatColor.AQUA + "Death Match:   " + ChatColor.WHITE + Util.minuteSeconds(remainingTime));
		replaceScore(objective, 11, ChatColor.GRAY + "");
		replaceScore(objective, 10, ChatColor.GREEN + "Nächstes Event:");
		replaceScore(objective,  9, nextEvent + ": " + Util.minuteSeconds(timeTillEvent));
		replaceScore(objective,  8, ChatColor.BLUE + Util.repeat(20, " "));
		replaceScore(objective,  7, "Spieler übrig:  " + playersLeft);
		replaceScore(objective,  6, ChatColor.GREEN + Util.repeat(20, " "));
		replaceScore(objective,  5, ChatColor.RED + "Kills: " + 0);
		replaceScore(objective,  4, ChatColor.RED + Util.repeat(20, " "));
		replaceScore(objective,  3, ChatColor.GOLD + "Map: " + ChatColor.WHITE + map);
		replaceScore(objective,  2, ChatColor.AQUA + Util.repeat(20, " "));
		replaceScore(objective,  1, ChatColor.YELLOW + "Groot23.mcserv.me");
	}
	
	public void updateGame(World world, int playersLeft, String nextEvent, int timeTillEvent, int remainingTime) {

		for(Player player : world.getPlayers()) {
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
			
			int kills = player.getMetadata("Skywars_kills").get(0).asInt();
			
//			replaceScore(objective, 13, Util.repeat(20, " "));
			replaceScore(objective, 12, ChatColor.AQUA + "Death Match:   " + ChatColor.WHITE + Util.minuteSeconds(remainingTime));
//			replaceScore(objective, 11, ChatColor.GRAY + "");
//			replaceScore(objective, 10, ChatColor.GREEN + "Nächstes Event:");
			replaceScore(objective,  9, nextEvent + ": " + Util.minuteSeconds(timeTillEvent));
//			replaceScore(objective,  8, ChatColor.BLUE + Util.repeat(20, " "));
			replaceScore(objective,  7, "Spieler übrig: " + playersLeft);
//			replaceScore(objective,  6, ChatColor.GREEN + Util.repeat(20, " "));
//			replaceScore(objective,  5, ChatColor.RED + "Kills: " + kills);
//			replaceScore(objective,  4, ChatColor.RED + Util.repeat(20, " "));
//			replaceScore(objective,  3, ChatColor.GRAY + "Map: " + ChatColor.WHITE + map);
//			replaceScore(objective,  2, ChatColor.AQUA + Util.repeat(20, " "));
//			replaceScore(objective,  1, ChatColor.YELLOW + "Groot23.mcserv.me");
			
//			objective.getScore(Util.repeat(20, " ")).setScore(13);
//			objective.getScore(ChatColor.GRAY + "Zeit übrig:   " + ChatColor.WHITE + Util.minuteSeconds(remainingTime)).setScore(12);
//			objective.getScore(ChatColor.GRAY + "").setScore(11);
//			objective.getScore(ChatColor.GREEN + "Nächstes Event:").setScore(10);
//			objective.getScore(nextEvent + ": " + Util.minuteSeconds(timeTillEvent)).setScore(9);
//			objective.getScore(ChatColor.BLUE + Util.repeat(20, " ")).setScore(8);
//			objective.getScore("Players left: " + playersLeft).setScore(7);
//			objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(6);
//			objective.getScore(ChatColor.RED + "Kills: " + kills).setScore(5);
//			objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(4);
//			objective.getScore(ChatColor.GRAY + "Map: " + ChatColor.WHITE + map).setScore(3);;
//			objective.getScore(ChatColor.AQUA + Util.repeat(20, " ")).setScore(2);
//			objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
			
		}
	}
		
}
