package me.groot_23.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MinG;

public class ToggleSpectator implements CommandExecutor{

	public ToggleSpectator(JavaPlugin plugin) {
		plugin.getCommand("toggleSpectate").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			MinG.setSpectator(p, !MinG.isSpectator(p));
		}
		return true;
	}

}
