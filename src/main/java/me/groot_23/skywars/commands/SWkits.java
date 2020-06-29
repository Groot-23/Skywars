package me.groot_23.skywars.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.commands.KitCommands;
import me.groot_23.skywars.util.Util;

public class SWkits implements CommandExecutor, TabCompleter{

	private MiniGame game;
	
	public SWkits(MiniGame game) {
		this.game = game;
		game.getPlugin().getCommand("swkits").setExecutor(this);
		game.getPlugin().getCommand("swkits").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		return KitCommands.tabComplete(game, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!sender.hasPermission("skywars.kits")) {
			sender.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.kits"));
			return true;
		}
		KitCommands.execute(game, sender, args);
		return true;
	}

}
