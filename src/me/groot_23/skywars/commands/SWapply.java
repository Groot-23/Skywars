package me.groot_23.skywars.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.FileUtil;
import me.groot_23.skywars.util.Util;

public class SWapply implements CommandExecutor {

	public SWapply(Main plugin) {
		plugin.getCommand("swapply").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;
		String worldName = player.getWorld().getName();
		// get number of copies
		int numberOfCopies = 0;
		if(args.length == 0) {
			for(File f : Bukkit.getWorldContainer().listFiles()) {
				if(f.getName().contains("skywars_lobby_" + worldName)) {
					numberOfCopies++;
				}
			}
		}
		else {
			try {
				numberOfCopies = Integer.parseInt(args[0]);
			}catch(NumberFormatException e) {
				player.sendMessage(Util.chat("&c\"" + numberOfCopies + "\" ist keine gültige Zahl"));
				for(File f : Bukkit.getWorldContainer().listFiles()) {
					if(f.getName().contains("skywars_lobby_" + worldName)) {
						numberOfCopies++;
					}
				}
			}
		}
		// delete old copies
		for(File f : Bukkit.getWorldContainer().listFiles()) {
			if(f.getName().contains("skywars_lobby_" + worldName)) {
				World world = Bukkit.getWorld(f.getName());
				if(world != null) {
					for(Player p : world.getPlayers()) {
						p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
					}
					Bukkit.unloadWorld(world, false);
				}
				FileUtil.delete(f);
			}
		}
		// copy new world
		String src = Bukkit.getWorldContainer() + File.separator + worldName;
		String dst = Bukkit.getWorldContainer() + File.separator + "skywars_lobby_" + worldName;
		for(int i = 0; i < numberOfCopies; i++) {
			try {
				FileUtil.copyFileOrFolder(new File(src), new File(dst + i));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
