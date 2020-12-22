package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.commands.PlayerCommand;
import me.groot_23.skywars.Main;

public class SWleave extends PlayerCommand {


	
	public SWleave(JavaPlugin plugin, String name) {
		super(plugin, name, null);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Main.chatPrefix + "Nur Spieler können diesen Befehl verwenden");
			return false;
		}
		Player player = (Player) sender;
		player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		return true;
	}
	
}
