package me.groot_23.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;

public class SWforce implements CommandExecutor {

	public SWforce(Main plugin) {
		plugin.getCommand("swforce").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!sender.hasPermission("skywars.force")) {
			sender.sendMessage("Du hast nicht die Berechtigung für diesen Befehl: skywars.force");
			return true;
		}
		if(args.length < 2) {
			sender.sendMessage("Zu wenige Argumente");
			return false;
		}
		Player player = Bukkit.getPlayer(args[0]);
		if(player == null) {
			sender.sendMessage("Spieler" + args[0] + " existiert nicht!");
			return false;
		}
		StringBuilder command = new StringBuilder();
		for(int i = 1; i < args.length; ++i) {
			command.append(args[i]);
			if(i != args.length - 1) {
				command.append(" ");
			}
		}
		player.performCommand(command.toString());
		return true;
	}
}
