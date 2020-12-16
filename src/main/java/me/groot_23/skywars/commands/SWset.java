package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.groot_23.pixel.commands.CommandBase;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWset extends CommandBase {

	private Main plugin;
	
	public SWset(Main plugin) {
		super(plugin, "swset", "skywars.set");
		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"refillTime", "refillTimeChange",
					"deathMatchBegin", "deathMatchBorderShrinkTime"};
			for(String s : modes) {
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		return list;
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.chatPrefix + "Nur Spieler können diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length < 2) return false;

		String mode = args[0];
		
		if( mode.equals("refillTime")) {
			writeTimeToConfig("refillTime", args[1], player);
			
		} else if(mode.equals("refillTimeChange")) {
			writeTimeToConfig("refillTimeChange", args[1], player);
		} else if(mode.equals("deathMatchBegin")) {
			writeTimeToConfig("deathMatchBegin", args[1], player);
		} else if(mode.equals("deathMatchBorderShrinkTime")) {
			writeTimeToConfig("deathMatchBorderShrinkTime", args[1], player);
		} else {
			return false;
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
				sender.sendMessage(Main.chatPrefix + Util.chat("&c\"" + timeStr + "\" ist keine erlaubte Zahl"));
		}
	}

}
