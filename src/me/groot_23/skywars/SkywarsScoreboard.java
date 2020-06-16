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

import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.ScoreboardApi;
import me.groot_23.skywars.util.Util;

public class SkywarsScoreboard {

	private Main plugin;

	public SkywarsScoreboard(Main plugin) {
		this.plugin = plugin;
	}

	public void init(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();

		Objective objective = board.registerNewObjective("skywars", "dummy",
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		player.setScoreboard(board);
	}

	public Objective resetObjective(Player player) {
		Scoreboard board = player.getScoreboard();

		Objective objective = board.getObjective("skywars");
		objective.unregister();

		objective = board.registerNewObjective("skywars", "dummy",
				ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		return objective;
	}

	public void resetKills(Player player) {
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, 0));
	}

	public void addKill(Player player) {
		if (player.getMetadata("Skywars_kills").isEmpty()) {
			player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, 0));
		}
		int kills = player.getMetadata("Skywars_kills").get(0).asInt() + 1;
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, kills));
		System.out.println(kills);
		ScoreboardApi.setValue(player, "kills", ChatColor.RED + "Kills: " + kills);
	}

	public String getKit(Player player) {
		String kit = null;
		if (player.hasMetadata("skywarsKit")) {
			SkywarsKit skyKit = plugin.kitByName.get(player.getMetadata("skywarsKit").get(0).asString());
			if (skyKit != null) {
				kit = skyKit.getDisplayName(player);
			}
		}
		if (kit == null) {
			kit = plugin.kits.get(0).getDisplayName(player);
		}
		return kit;
	}

	public static String getEntryFromScore(Objective o, int score) {
		if (o == null)
			return null;
		if (!hasScoreTaken(o, score))
			return null;
		for (String s : o.getScoreboard().getEntries()) {
			if (o.getScore(s).getScore() == score)
				return o.getScore(s).getEntry();
		}
		return null;
	}

	public static boolean hasScoreTaken(Objective o, int score) {
		for (String s : o.getScoreboard().getEntries()) {
			if (o.getScore(s).getScore() == score)
				return true;
		}
		return false;
	}

	public static void replaceScore(Objective o, int score, String name) {
		if (hasScoreTaken(o, score)) {
			if (getEntryFromScore(o, score).equalsIgnoreCase(name))
				return;
			if (!(getEntryFromScore(o, score).equalsIgnoreCase(name)))
				o.getScoreboard().resetScores(getEntryFromScore(o, score));
		}
		o.getScore(name).setScore(score);
	}

	public void initPreGame(Player player, int maxPlayers, int timeLeft, String map) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		int playerCount = player.getWorld().getPlayers().size();

		String kit = getKit(player);

		ScoreboardApi.init(objective, "server", null, "map", null, "kit", "time", null, "players", null);

		ScoreboardApi.setValue(player, "players", Integer.toString(playerCount) + "/" + maxPlayers + " "
				+ plugin.langManager.getTranslation(player, LanguageKeys.PLAYER));
		ScoreboardApi.setValue(player, "time",
				plugin.langManager.getTranslation(player, LanguageKeys.TIME_UNTILL_START) + ": " + timeLeft);
		ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit: " + kit);
		ScoreboardApi.setValue(player, "map", ChatColor.GOLD + "Map: " + ChatColor.WHITE +
				plugin.langManager.getTranslation(player, "map." + map));
		ScoreboardApi.setValue(player, "server", ChatColor.YELLOW + "Groot23.mcserv.me");

	}

	public void updatePreGame(World world, int maxPlayers, int timeLeft) {
		int playerCount = world.getPlayers().size();
		for (Player player : world.getPlayers()) {
//			Objective objective = resetObjective(player);
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

			String kit = getKit(player);

			ScoreboardApi.setValue(player, "players", Integer.toString(playerCount) + "/" + maxPlayers + " "
					+ plugin.langManager.getTranslation(player, LanguageKeys.PLAYER));
			ScoreboardApi.setValue(player, "time",
					plugin.langManager.getTranslation(player, LanguageKeys.TIME_UNTILL_START) + ": " + timeLeft);
			ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit: " + kit);

		}
	}

	public void initGame(Player player, int playersLeft, String nextEvent, int timeTillEvent, int deathMatch,
			int remainingTime, String map) {
		Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

		String kit = getKit(player);

		ScoreboardApi.init(objective, null, "server", null, "map", null, "kit", "kills", null, "playersLeft", null,
				"event", "eventHeader", null, "deathMatch", "remainingTime", null);

		ScoreboardApi.setValue(player, "server", ChatColor.YELLOW + "Groot23.mcserv.me");
		ScoreboardApi.setValue(player, "map", ChatColor.GOLD + "Map: " + ChatColor.WHITE +
				plugin.langManager.getTranslation(player, "map." + map));
		ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit: " + kit);
		ScoreboardApi.setValue(player, "playersLeft",
				plugin.langManager.getTranslation(player, LanguageKeys.PLAYERS_LEFT) + ":  " + playersLeft);
		ScoreboardApi.setValue(player, "kills", ChatColor.RED + "Kills: " + 0);
		ScoreboardApi.setValue(player, "event", nextEvent + ": " + Util.minuteSeconds(timeTillEvent));
		ScoreboardApi.setValue(player, "eventHeader",
				ChatColor.GREEN + plugin.langManager.getTranslation(player, LanguageKeys.NEXT_EVENT));
		ScoreboardApi.setValue(player, "deathMatch",
				ChatColor.RED + "Death Match:   " + ChatColor.WHITE + Util.minuteSeconds(deathMatch));
		ScoreboardApi.setValue(player, "remainingTime",
				ChatColor.AQUA + plugin.langManager.getTranslation(player, LanguageKeys.TIME_LEFT) + ":   "
						+ ChatColor.WHITE + Util.minuteSeconds(remainingTime));
	}

	public void updateGame(World world, int playersLeft, String nextEvent, int timeTillEvent, int deathMatch,
			int remainingTime) {

		for (Player player : world.getPlayers()) {
			Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

			String kit = getKit(player);

			ScoreboardApi.setValue(player, "kit", ChatColor.LIGHT_PURPLE + "Kit: " + kit);
			ScoreboardApi.setValue(player, "playersLeft",
					plugin.langManager.getTranslation(player, LanguageKeys.PLAYERS_LEFT) + ":  " + playersLeft);
			ScoreboardApi.setValue(player, "event",
					plugin.langManager.getTranslation(player, nextEvent) + ": " + Util.minuteSeconds(timeTillEvent));
			ScoreboardApi.setValue(player, "deathMatch",
					ChatColor.RED + "Death Match:   " + ChatColor.WHITE + Util.minuteSeconds(deathMatch));
			ScoreboardApi.setValue(player, "remainingTime",
					ChatColor.AQUA + plugin.langManager.getTranslation(player, LanguageKeys.TIME_LEFT) + ":   "
							+ ChatColor.WHITE + Util.minuteSeconds(remainingTime));

		}
	}

}
