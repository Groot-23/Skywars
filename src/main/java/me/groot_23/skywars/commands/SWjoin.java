package me.groot_23.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.groot_23.ming.MinG;
import me.groot_23.ming.game.Game;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWjoin implements CommandExecutor {

	public SWjoin(Main plugin) {
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
			String name = "skywars-solo";
			if(args.length > 0) {
				if(args[0].equals("duo")) name = "skywars-duo";
			}
			Game game = null;
			if(args.length > 1) {
				game = MinG.GameProvider.provideGame(name, args[1]);
			} else {
				game = MinG.GameProvider.provideGame(name, MinG.GameProvider.ProvideType.MOST_PLAYERS);
			}
			if(game != null) game.joinPlayer(p);
		}
		return true;
	}
}
