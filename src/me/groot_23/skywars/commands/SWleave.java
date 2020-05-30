package me.groot_23.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;

public class SWleave implements CommandExecutor {

	public SWleave(Main plugin) {
		plugin.getCommand("swleave").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
			return false;
		}
		Player player = (Player) sender;
		String world = player.getWorld().getName();
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
		return true;
	}
	
}
