package me.groot_23.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.groot_23.ming.game.MiniGameMode;
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
				p.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuf�hren! Ben�tigte Berechtigung: skywars.join"));
				return true;
			}
			MiniGameMode mode = null;
			if(args.length > 0) {
				mode = Main.game.getMode(args[0]);
			} if(mode == null) {
				mode = Main.game.getDefaultMode();
			}
//			mode.getArenaProvider().joinPlayer(p);
			mode.gameProvider.joinPlayer(p);
		}
		return true;
	}
}
