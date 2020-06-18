package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWset implements CommandExecutor, TabCompleter{

	private Main plugin;
	
	public SWset(Main plugin) {
		plugin.getCommand("swset").setExecutor(this);
		plugin.getCommand("swset").setTabCompleter(this);
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"refillTime", "persistentLobbies", "allowDynamic", "refillTimeChange",
					"deathMatchBegin", "deathMatchBorderShrinkTime"};
			for(String s : modes) {
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		if(args.length == 2) {
			if(args[0].equals("allowDynamic")) {
				String[] options = new String[] {"true", "false"};
				for(String s : options) {
					if(s.startsWith(args[0])) list.add(s);
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length == 0) {
			player.sendMessage(Util.chat("&cZu wenige Argumente"));
			return false;
		}
		if(!player.hasPermission("skywars.set")) {
			player.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.set"));
			return true;
		}
		String mode = args[0];
		
		if(mode.equals("persistentLobbies")) {
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib die Anzahl der dauerhaften Lobbys an"));
				return false;
			}
			try {
				int persistentLobbies = Integer.parseInt(args[1]);
				plugin.getConfig().set("persistentLobbies", persistentLobbies);
				plugin.saveConfig();
			}catch(NumberFormatException e) {
				player.sendMessage(Util.chat("&c\"" + args[1] + "\" ist keine erlaubte Zahl"));
				return true;
			}
		} else if(mode.equals("allowDynamic")) {
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib true oder false an!"));
				return false;
			}
			if(args[1].equals("true")) plugin.getConfig().set("allowDynamicLobbies", true);
			else if(args[1].equals("false")) plugin.getConfig().set("allowDynamicLobbies", false);
			else player.sendMessage(Util.chat("&cGib true oder false an!"));
			plugin.saveConfig();
		} else if(mode.equals("refillTime")) {
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib die Zeit an. Entweder in Sekunden oder im Format Minuten:Sekunden"));
				return false;
			}
			writeTimeToConfig("refillTime", args[1], player);
		} else if(mode.equals("refillTimeChange")) {
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib die Zeit an. Entweder in Sekunden oder im Format Minuten:Sekunden"));
				return false;
			}
			writeTimeToConfig("refillTimeChange", args[1], player);
		} else if(mode.equals("deathMatchBegin")) {
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib die Zeit an. Entweder in Sekunden oder im Format Minuten:Sekunden"));
				return false;
			}
			writeTimeToConfig("deathMatchBegin", args[1], player);
		} else if(mode.equals("deathMatchBorderShrinkTime")) {
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib die Zeit an. Entweder in Sekunden oder im Format Minuten:Sekunden"));
				return false;
			}
			writeTimeToConfig("deathMatchBorderShrinkTime", args[1], player);
		}
		
		return true;
	}
	
	public void writeTimeToConfig(String key, String timeStr, Player sender) {
		try {
			int refillTime;
			if(timeStr.contains(":")) {
				refillTime = Util.secondsFromStr(timeStr);
			} else {
				refillTime = Integer.parseInt(timeStr);
			}
			plugin.getConfig().set(key, refillTime);
			plugin.saveConfig();
		}catch(NumberFormatException e) {
			if(sender != null)
				sender.sendMessage(Util.chat("&c\"" + timeStr + "\" ist keine erlaubte Zahl"));
		}
	}

}
