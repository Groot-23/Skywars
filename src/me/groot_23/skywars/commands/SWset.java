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
			String[] modes = new String[] {"refillTime", "persistentLobbies", "allowDynamic"};
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
		String mode = args[0];
		
		if(mode.equals("persistentLobbies")) {
			if(!sender.hasPermission("skywars.set.persistentLobbies")) {
				player.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.set.persistentLobbies"));
				return true;
			}
			
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
			if(!sender.hasPermission("skywars.set.allowDynamic")) {
				player.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.set.allowDynamic"));
				return true;
			}
			
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib true oder false an!"));
				return false;
			}
			if(args[1].equals("true")) plugin.getConfig().set("allowDynamicLobbies", true);
			else if(args[1].equals("false")) plugin.getConfig().set("allowDynamicLobbies", false);
			else player.sendMessage(Util.chat("&cGib true oder false an!"));
			plugin.saveConfig();
		} else if(mode.equals("refillTime")) {
			if(!sender.hasPermission("skywars.maps.refillTime")) {
				player.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.maps.refillTime"));
				return true;
			}
			
			if(args.length == 1) {
				player.sendMessage(Util.chat("&cGib die Zeit an. Entweder in Sekunden oder im Format Minuten:Sekunden"));
				return false;
			}
			try {
				int refillTime;
				if(args[1].contains(":")) {
					refillTime = Util.secondsFromStr(args[1]);
				} else {
					refillTime = Integer.parseInt(args[1]);
				}
				plugin.getConfig().set("refillTime", refillTime);
				plugin.saveConfig();
			}catch(NumberFormatException e) {
				player.sendMessage(Util.chat("&c\"" + args[1] + "\" ist keine erlaubte Zahl"));
				return true;
			}
		}
		
		return true;
	}

}
