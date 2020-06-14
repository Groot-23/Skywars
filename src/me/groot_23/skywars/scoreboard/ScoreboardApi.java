package me.groot_23.skywars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardApi {
	
	public static void init(Objective objective, String... lines) {
		Scoreboard board = objective.getScoreboard();
		for(String entry : board.getEntries()) {
			board.resetScores(entry);
		}
		for(Team team : board.getTeams()) {
			boolean remove = false;
			for(ChatColor c : ChatColor.values()) {
				if(team.getEntries().contains(c + "")) {
					remove = true;
					break;
				}
			}
			if(remove) {
				team.unregister();
			}
		}
		for(int i = 0; i < lines.length; i++) {
			if(lines[i] != null) {
				Team team = board.registerNewTeam(lines[i]);
				team.addEntry(ChatColor.values()[i] + "");
			}
			objective.getScore(ChatColor.values()[i] + "").setScore(i + 1);
		}
	}
	
	public static void setValue(Scoreboard board, String key, String value) {
		Team team = board.getTeam(key);
		if(team != null) {
			team.setPrefix(value);
		}
	}
	
	public static void setValue(Player player, String key, String value) {
		setValue(player.getScoreboard(), key, value);
	}
}
