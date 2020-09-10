package me.groot_23.skywars.commands;

import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import me.groot_23.ming.MinG;
import me.groot_23.ming.commands.CommandBase;
import me.groot_23.ming.util.Utf8Config;
import me.groot_23.ming.world.WorldUtil;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWmaps extends CommandBase {

	public static final String PERMISSION = "skywars.maps";

	public SWmaps(Main plugin) {
		super(plugin, "swmaps", "skywars.maps");
	}

	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
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
								&& !pathname.getName().startsWith(MinG.WorldProvider.WORLD_PREFIX);
					}
				});
				for (File f : worlds) {
					list.add(f.getName());
				}
			} else if (args[0].equals("remove")) {
				ConfigurationSection sec = getGroupConfig();
				if(sec != null) {
					for (String s : sec.getKeys(false)) {
						if (s.startsWith(args[1]))
							list.add(s);
					}
				}
			} else if (args[0].equals("set")) {
				ConfigurationSection sec = getWorldConfig();
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
				ConfigurationSection sec = getGroupConfig();
				if(sec != null) {
					for (String s : sec.getKeys(false)) {
						if (s.startsWith(args[2]))
							list.add(s);
					}
				}
			}
			else if(args[0].equals("remove")) {
				List<String> l = getGroupConfig().getStringList(args[1]);
				for (String s : l) {
					if (s.startsWith(args[2]))
						list.add(s);
				}
			}
		}
		return list;
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl ausf�hren");
			return true;
		}
		Player player = (Player) sender;

		if (args.length == 0) return false;
		String mode = args[0];
		if (args.length == 3 && mode.equals("register")) {
			String world = args[1];
			String group = args[2];
			if (!WorldUtil.worldExists(world)) {
				player.sendMessage(Util.chat("&cDie Welt \"" + args[1] + "\" wurde nicht gefunden!"));
				return true;
			}
			addWorldToGroup(world, group);
			
		} else if (mode.equals("list")) {

			ConfigurationSection groups = getGroupConfig();
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
		} else if (args.length == 3 && mode.equals("remove")) {
			String group = args[1];
			String world = args[2];
			removeWorldFromGroup(world, group);
			return true;
		} else if (mode.equals("set") && (args.length > 3 || (args.length == 3 && args[2].equals("midSpawn")))) {
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
		}
		return false;
	}
	
	public Utf8Config getGroupConfig() {
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(new File(Main.getInstance().getDataFolder(), "groups.yml"));
			return cfg;
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void saveGroupConfig(Utf8Config cfg) {
		try {
			cfg.save(new File(Main.getInstance().getDataFolder(), "groups.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addWorldToGroup(String world, String group) {
		Utf8Config cfg = getGroupConfig();
		if(cfg != null) {
			List<String> strings =  cfg.getStringList(group);
			if(!strings.contains(world)) {			
				strings.add(world);
				cfg.set(group, strings);
			}
			saveGroupConfig(cfg);
		}
	}
	
	public void removeWorldFromGroup(String world, String group) {
		Utf8Config cfg = getGroupConfig();
		if(cfg != null) {
			List<String> strings =  cfg.getStringList(group);
			if(!strings.contains(world)) {			
				strings.remove(world);
				cfg.set(group, strings);
			}
			saveGroupConfig(cfg);
		}
	}
	
	public Utf8Config getWorldConfig() {
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(new File(Main.getInstance().getDataFolder(), "worlds.yml"));
			return cfg;
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void saveWorldConfig(Utf8Config cfg) {
		try {
			cfg.save(new File(Main.getInstance().getDataFolder(), "worlds.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setWorldProperty(String world, String property, String intStr, Player sender) {
		try {
			int val = Integer.parseInt(intStr);
			Utf8Config cfg = getWorldConfig();
			if(cfg != null) {	
				ConfigurationSection sec = cfg.getConfigurationSection(world);
				if(sec == null) sec = cfg.createSection(world);
				sec.set(property, val);
				saveWorldConfig(cfg);
			}
		} catch(NumberFormatException e) {
			if(sender != null)
				sender.sendMessage(Util.chat("&c\"" + intStr + "\" ist keine erlaubte Zahl"));
		}

	}
	
	public void setMidSpawn(String world, int x, int y, int z) {
		Utf8Config cfg = getWorldConfig();
		if(cfg != null) {	
			ConfigurationSection sec = cfg.getConfigurationSection(world);
			if(sec == null) sec = cfg.createSection(world);
			sec.set("midSpawn.x", x);
			sec.set("midSpawn.y", y);
			sec.set("midSpawn.z", z);
			saveWorldConfig(cfg);
		}
	}

}
