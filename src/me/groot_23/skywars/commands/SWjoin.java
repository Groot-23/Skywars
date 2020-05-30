package me.groot_23.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWjoin implements CommandExecutor {
	private Main plugin;

	public SWjoin(Main plugin) {
		this.plugin = plugin;

		plugin.getCommand("swjoin").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;
			if(!sender.hasPermission("skywars.join")) {
				p.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.join"));
				return true;
			}
			plugin.lobbyManager.joinPlayer(p);
		}
		return true;
	}
}
