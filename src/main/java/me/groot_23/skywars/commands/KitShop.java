package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.commands.PlayerCommand;
import me.groot_23.pixel.kits.KitApi;

public class KitShop extends PlayerCommand{

	public KitShop(JavaPlugin plugin, String name) {
		super(plugin, name, null);
	}


	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
		KitApi.openShop((Player)sender, "skywars");
		return true;
	}

}
