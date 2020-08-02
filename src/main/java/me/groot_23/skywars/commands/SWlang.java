package me.groot_23.skywars.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.commands.LangCommand;

public class SWlang implements CommandExecutor, TabCompleter {

	private MiniGame game;
	
	public SWlang(MiniGame game) {
		this.game = game;
		game.getPlugin().getCommand("swlang").setExecutor(this);
		game.getPlugin().getCommand("swlang").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		return LangCommand.tabComplete(game, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		LangCommand.execute(game, sender, args);
		return true;
	}

}
