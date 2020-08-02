package me.groot_23.skywars.commands;

import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.groot_23.ming.world.WorldUtil;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class SWmaps implements CommandExecutor, TabCompleter {

	public static final String PERMISSION = "skywars.maps";

	public SWmaps(Main plugin) {
		plugin.getCommand("swmaps").setExecutor(this);
		plugin.getCommand("swmaps").setTabCompleter(this);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			String[] modes = new String[] { "register", "list", "remove", "set"};
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
				ConfigurationSection sec = Main.game.worldProvider.getConfig().getConfigurationSection("groups");
				if(sec != null) {
					for (String s : sec.getKeys(false)) {
						if (s.startsWith(args[1]))
							list.add(s);
					}
				}
			} else if (args[0].equals("set")) {
				ConfigurationSection sec = Main.game.worldProvider.getConfig().getConfigurationSection("worlds");
				if(sec != null) {
					for(String s : sec.getKeys(false)) {
						if(s.startsWith(args[1]))  {
							list.add(s);
						}
					}
				}
			}
		} else if(args.length == 3) {
			if (args[0].equals("set")) {
				String[] modes = new String[] { "minPlayers", "maxPlayers", "midRadius", "mapRadius", "midSpawn" };
				for (String s : modes) {
					if (s.startsWith(args[2]))
						list.add(s);
				}
			}
			else if(args[0].equals("register")) {
				ConfigurationSection sec = Main.game.worldProvider.getConfig().getConfigurationSection("groups");
				if(sec != null) {
					for (String s : sec.getKeys(false)) {
						if (s.startsWith(args[2]))
							list.add(s);
					}
				}
			}
			else if(args[0].equals("remove")) {
				List<String> l = Main.game.worldProvider.getConfig().getStringList("groups." + args[1]);
				for (String s : l) {
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
			sender.sendMessage("Nur Spieler können diesen Befehl ausf�hren");
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
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.maps.register"));
				return true;
			}
			// get world
			if (args.length < 3) {
				player.sendMessage(Util.chat("&cZu wenige Argumente. Gib Welt und Gruppe an"));
				return false;
			}
			String world = args[1];
			String group = args[2];
			if (!WorldUtil.worldExists(world)) {
				player.sendMessage(Util.chat("&cDie Welt \"" + args[1] + "\" wurde nicht gefunden!"));
				return true;
			}

			Main.game.worldProvider.addWorldToGroup(world, group);
			
		} else if (mode.equals("list")) {
			if (!sender.hasPermission("skywars.maps.list")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.maps.list"));
				return true;
			}

			ConfigurationSection groups = Main.game.worldProvider.getConfig().getConfigurationSection("groups");
			if (groups == null) {
				player.sendMessage("Es sind keine Daten vorhanden");
				return true;
			}
			for (String key : groups.getValues(false).keySet()) {
				player.sendMessage(ChatColor.GREEN + key + ":");
				for(String s : groups.getStringList(key)) {
					player.sendMessage(" - " + s);
				}
			}
		} else if (mode.equals("remove")) {
			if (!sender.hasPermission("skywars.maps.remove")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.maps.remove"));
				return true;
			}

			if (args.length < 3) {
				player.sendMessage(Util.chat("&cZu wenige Argumente. Gib Gruppe und Welt an"));
				return false;
			}
			String group = args[1];
			String world = args[2];
			Main.game.worldProvider.removeWorldFromGroup(world, group);
			return true;
		} else if (mode.equals("set")) {
			if (!sender.hasPermission("skywars.maps.set")) {
				player.sendMessage(Util.chat(
						"&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.maps.set"));
				return true;
			}
			// get world
			if (args.length <= 3 && !(args.length == 3 && args[2].equals("midSpawn"))) {
				player.sendMessage(Util.chat("&cZu wenige Argumente"));
				return false;
			}
			String world = args[1];
			
			if(args[2].equals("minPlayers")) {
				setWorldProperty(world, "minPlayers", args[3], player);
			} else if(args[2].equals("maxPlayers")) {
				setWorldProperty(world, "maxPlayers", args[3], player);
			} else if(args[2].equals("midRadius")) {
				setWorldProperty(world, "midRadius", args[3], player);
			} else if(args[2].equals("mapRadius")) {
				setWorldProperty(world, "mapRadius", args[3], player);
			} else if(args[2].equals("midSpawn")) {
				if(args.length == 3) {
					Location l = player.getLocation();
					setMidSpawn(world, l.getBlockX(), l.getBlockY(), l.getBlockZ());
				} else if(args.length >= 6) {
					try {
						int x = Integer.parseInt(args[3]);
						int y = Integer.parseInt(args[4]);
						int z = Integer.parseInt(args[5]);
						setMidSpawn(world, x, y, z);
					}
					catch(NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "Ungültige Koordinaten (Es müssen ganze Zahlen sein)");
					}
				} else {
					player.sendMessage(ChatColor.RED + "Zu wenige Argumente für die Koordinaten. Gib keine an, wenn es deine Position sein soll!");
				}
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
			Main.game.worldProvider.getWorldSection(world).set(property, val);
			Main.game.worldProvider.saveConfig();
		} catch(NumberFormatException e) {
			if(sender != null)
				sender.sendMessage(Util.chat("&c\"" + intStr + "\" ist keine erlaubte Zahl"));
		}

	}
	
	public void setMidSpawn(String world, int x, int y, int z) {
		ConfigurationSection section = Main.game.worldProvider.getWorldSection(world);
		section.set("midSpawn.x", x);
		section.set("midSpawn.y", y);
		section.set("midSpawn.z", z);
		Main.game.worldProvider.saveConfig();
	}

}
