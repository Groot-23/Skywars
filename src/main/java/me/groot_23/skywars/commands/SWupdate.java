package me.groot_23.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWupdate implements CommandExecutor {

	private Main plugin;
	
	public SWupdate(Main plugin) {
		plugin.getCommand("swupdate").setExecutor(this);
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(!sender.hasPermission("skywars.update")) {
			sender.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.update"));
			return true;
		}
		plugin.arenaProvider.update();
		sender.sendMessage(Util.chat("Änderungen erfolgreich übernommen"));
		return true;
	}

}
