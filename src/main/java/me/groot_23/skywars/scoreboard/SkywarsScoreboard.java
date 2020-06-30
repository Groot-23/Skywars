package me.groot_23.skywars.scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.groot_23.ming.display.ScoreboardApi;
import me.groot_23.ming.kits.Kit;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.util.Util;

public class SkywarsScoreboard {

	public static final String COLON = ChatColor.GRAY + ": " + ChatColor.WHITE;

	public static void init(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();

		Objective objective = board.registerNewObjective("skywars", "dummy",
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		player.setScoreboard(board);
	}

	public static void resetKills(Player player) {
		player.setMetadata("Skywars_kills", new FixedMetadataValue(Main.getInstance(), 0));
	}

	public static void addKill(Player player) {
		if (player.getMetadata("Skywars_kills").isEmpty()) {
			player.setMetadata("Skywars_kills", new FixedMetadataValue(Main.getInstance(), 0));
		}
		int kills = player.getMetadata("Skywars_kills").get(0).asInt() + 1;
		player.setMetadata("Skywars_kills", new FixedMetadataValue(Main.getInstance(), kills));
		System.out.println(kills);
		ScoreboardApi.setValue(player, "kills", ChatColor.RED + "Kills" + COLON + kills);
	}

	public static String getKit(Player player) {
		return Main.game.getKit(player).getDisplayName(player);
	}

	public static void initPreGame(Player player, int maxPlayers, int timeLeft, String map) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		int playerCount = player.getWorld().getPlayers().size();

		String kit = getKit(player);

		ScoreboardApi.init(objective, "server", null, "map", null, "kit", "time", null, "players", null);

		ScoreboardApi.setValue(player, "players", ChatColor.GOLD + Integer.toString(playerCount) + "/" + maxPlayers
				+ ChatColor.WHITE + " " + Main.game.getTranslation(player, LanguageKeys.PLAYER));
		ScoreboardApi.setValue(player, "time",
				ChatColor.GREEN + Main.game.getTranslation(player, LanguageKeys.TIME_UNTILL_START) + COLON + timeLeft);
		ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit" + COLON + kit);
		ScoreboardApi.setValue(player, "map",
				ChatColor.GOLD + "Map" + COLON + ChatColor.WHITE + Main.game.getTranslation(player, "map." + map));
		ScoreboardApi.setValue(player, "server", ChatColor.YELLOW + "Groot23.mcserv.me");

	}

	public static void updatePreGame(World world, int maxPlayers, int timeLeft) {
		int playerCount = world.getPlayers().size();
		for (Player player : world.getPlayers()) {
			String kit = getKit(player);

			ScoreboardApi.setValue(player, "players", ChatColor.GOLD + Integer.toString(playerCount) + "/" + maxPlayers
					+ ChatColor.WHITE + " " + Main.game.getTranslation(player, LanguageKeys.PLAYER));
			ScoreboardApi.setValue(player, "time", ChatColor.GREEN
					+ Main.game.getTranslation(player, LanguageKeys.TIME_UNTILL_START) + COLON + timeLeft);
			ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit" + COLON + kit);

		}
	}

	public static void initGame(Player player, int playersLeft, String nextEvent, int timeTillEvent, int deathMatch,
			int remainingTime, String map) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

		String kit = getKit(player);

		ScoreboardApi.init(objective, null, "server", null, "map", null, "kit", "kills", null, "playersLeft", null,
				"event", "eventHeader", null, "deathMatch", "remainingTime", null);

		ScoreboardApi.setValue(player, "server", ChatColor.YELLOW + "Groot23.mcserv.me");
		ScoreboardApi.setValue(player, "map",
				ChatColor.GOLD + "Map" + COLON + ChatColor.WHITE + Main.game.getTranslation(player, "map." + map));
		ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit" + COLON + kit);
		ScoreboardApi.setValue(player, "playersLeft",
				ChatColor.GREEN + Main.game.getTranslation(player, LanguageKeys.PLAYERS_LEFT) + COLON + playersLeft);
		ScoreboardApi.setValue(player, "kills", ChatColor.RED + "Kills" + COLON + 0);
		ScoreboardApi.setValue(player, "event", nextEvent + COLON + Util.minuteSeconds(timeTillEvent));
		ScoreboardApi.setValue(player, "eventHeader",
				ChatColor.GREEN + Main.game.getTranslation(player, LanguageKeys.NEXT_EVENT));
		ScoreboardApi.setValue(player, "deathMatch",
				ChatColor.RED + "Death Match" + COLON + Util.minuteSeconds(deathMatch));
		ScoreboardApi.setValue(player, "remainingTime", ChatColor.GREEN
				+ Main.game.getTranslation(player, LanguageKeys.TIME_LEFT) + COLON + Util.minuteSeconds(remainingTime));
	}

	public static void updateGame(World world, int playersLeft, String nextEvent, int timeTillEvent, int deathMatch,
			int remainingTime) {

		for (Player player : world.getPlayers()) {

			String kit = getKit(player);

			ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit" + COLON + kit);
			ScoreboardApi.setValue(player, "playersLeft",
					ChatColor.GREEN + Main.game.getTranslation(player, LanguageKeys.PLAYERS_LEFT) + COLON + playersLeft);
			ScoreboardApi.setValue(player, "event",
					Main.game.getTranslation(player, nextEvent) + COLON + Util.minuteSeconds(timeTillEvent));
			ScoreboardApi.setValue(player, "deathMatch",
					ChatColor.RED + "Death Match" + COLON + Util.minuteSeconds(deathMatch));
			ScoreboardApi.setValue(player, "remainingTime",
					ChatColor.GREEN + Main.game.getTranslation(player, LanguageKeys.TIME_LEFT) + COLON
							+ Util.minuteSeconds(remainingTime));

		}
	}
}
