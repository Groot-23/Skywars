package me.groot_23.skywars.commands;

import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class SWmaps implements CommandExecutor, TabCompleter {

	private Main plugin;
	public static final String PERMISSION = "skywars.maps";

	public SWmaps(Main plugin) {
		plugin.getCommand("swmaps").setExecutor(this);
		plugin.getCommand("swmaps").setTabCompleter(this);
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			String[] modes = new String[] { "register", "list", "remove", "set" };
			for (String s : modes) {
				if (s.startsWith(args[0]))
					list.add(s);
			}
		}
		if (args.length == 2) {
			if (args[0].equals("register")) {
				File[] worlds = Bukkit.getWorldContainer().listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().startsWith(args[1])
								&& !pathname.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX);
					}
				});
				for (File f : worlds) {
					list.add(f.getName());
				}
			} else if (args[0].equals("remove")) {
				for (String s : Main.game.getArenaProvider().getRegisteredWorlds()) {
					if (s.startsWith(args[1]))
						list.add(s);
				}
			} else if (args[0].equals("set")) {
				for(String key : plugin.getConfig().getConfigurationSection("worlds").getKeys(false)) {
					if(key.startsWith(args[1]))  {
						list.add(key);
					}
				}
			}
		} else if(args.length == 3) {
			if (args[0].equals("set")) {
				String[] modes = new String[] { "minPlayers", "maxPlayers", "midRadius", "mapRadius" };
				for (String s : modes) {
					if (s.startsWith(args[2]))
						list.add(s);
				}
			}
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler k�nnen diesen Befehl ausf�hren");
			return true;
		}
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(Util.chat("&cZu wenige Argumente"));
			return false;
		}
		String mode = args[0];
		if (mode.equals("register")) {
			if (!sender.hasPermission("skywars.maps.register")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuf�hren! Ben�tigte Berechtigung: skywars.maps.register"));
				return true;
			}
			// get world
			if (args.length == 1) {
				player.sendMessage(Util.chat("&cDu musst die Welt angeben, die du registrieren m�chtest"));
				return false;
			}
			String world = args[1];
			if (!Util.worldExists(world)) {
				player.sendMessage(Util.chat("&cDie Welt \"" + args[1] + "\" wurde nicht gefunden!"));
				return true;
			}

			plugin.getConfig().createSection("worlds." + world);
			plugin.saveConfig();
			
		} else if (mode.equals("list")) {
			if (!sender.hasPermission("skywars.maps.list")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuf�hren! Ben�tigte Berechtigung: skywars.maps.list"));
				return true;
			}

			ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
			if (worlds == null) {
				player.sendMessage("Es sind keine Daten vorhanden");
				return true;
			}
			for (String key : worlds.getValues(false).keySet()) {
				ConfigurationSection section = worlds.getConfigurationSection(key);
				if (section != null) {
					player.sendMessage(ChatColor.GREEN + key + ":");
					for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
						player.sendMessage("     " + entry.getKey() + ": " + entry.getValue().toString());
					}
				}
			}
		} else if (mode.equals("remove")) {
			if (!sender.hasPermission("skywars.maps.remove")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuf�hren! Ben�tigte Berechtigung: skywars.maps.remove"));
				return true;
			}

			if (args.length == 1) {
				player.sendMessage(Util.chat("&cDu musst die Welt angeben, die du entfernen m�chtest"));
				return false;
			}
			String world = args[1];
			ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
			if (worlds != null) {
				if (worlds.contains(world)) {
					worlds.set(world, null);
					plugin.saveConfig();
					player.sendMessage("Die Welt wurde erfolgreich entfernt");
					return true;
				}
			}
			// If the world was not found
			player.sendMessage(Util.chat("&cDie Welt wurde nicht gefunden"));
			return true;
		} else if (mode.equals("set")) {
			if (!sender.hasPermission("skywars.maps.set")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuf�hren! Ben�tigte Berechtigung: skywars.maps.set"));
				return true;
			}
			// get world
			if (args.length <= 3) {
				player.sendMessage(Util.chat("&cZu wenige Argumente"));
				return false;
			}
			String world = args[1];
			if (!plugin.getConfig().contains("worlds." + world)) {
				player.sendMessage(Util.chat("&cDie Welt ist nicht registriert! (/swmaps register " + world +")"));
				return true;
			}
			
			if(args[2].equals("minPlayers")) {
				setWorldProperty(world, "minPlayers", args[3], player);
			} else if(args[2].equals("maxPlayers")) {
				setWorldProperty(world, "maxPlayers", args[3], player);
			} else if(args[2].equals("midRadius")) {
				setWorldProperty(world, "midRadius", args[3], player);
			} else if(args[2].equals("mapRadius")) {
				setWorldProperty(world, "mapRadius", args[3], player);
			} else if(args[2].equals("weight")) {
				setWorldProperty(world, "weight", args[3], player);
			}
		} else {
			player.sendMessage(Util.chat("&c\"" + mode + "\" ist ein unbekannter Befehl"));
			return false;
		}
		return true;
	}
	
	public void setWorldProperty(String world, String property, String intStr, Player sender) {
		try {
			int val = Integer.parseInt(intStr);
			System.out.println(plugin.getConfig().get("worlds." + world + "." + property));
			plugin.getConfig().set("worlds." + world + "." + property, val);
			plugin.saveConfig();
		} catch(NumberFormatException e) {
			if(sender != null)
				sender.sendMessage(Util.chat("&c\"" + intStr + "\" ist keine erlaubte Zahl"));
		}

	}

}
