package me.groot_23.skywars.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.EmptyChunkGenerator;
import me.groot_23.skywars.util.Util;

public class SWedit implements CommandExecutor{

	private Main plugin;
	
	public SWedit(Main plugin) {
		plugin.getCommand("swedit").setExecutor(this);
		plugin.getCommand("swedit").setTabCompleter(new Completer());
		this.plugin = plugin;
	}
	
	public class Completer implements TabCompleter {

		@Override
		public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
			List<String> list = new ArrayList<String>();
			if(args.length == 1) {
				for(String s : plugin.lobbyManager.getRegisteredWorlds()) {
					if(s.startsWith(args[0]))
						list.add(s);
				}
			}
			return list;
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;
		if(!sender.hasPermission("skywars.edit")) {
			player.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.edit"));
			return true;
		}
		if(args.length == 0) {
			player.sendMessage(Util.chat("&cGib die Welt an, die du bearbeiten möchtest"));
			return true;
		}
		if(!plugin.getConfig().contains("worlds." + args[0])) {
			player.sendMessage(Util.chat("&cDu kannst nur bereits registrierte Welten bearbeiten. Verwende zunächst /swmaps register"));
			return true;
		}
		World world = Bukkit.getWorld(args[0]);
		if(world == null) {
			if(!new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + args[0]).exists()) {
				player.sendMessage(Util.chat("&cDie Welt \"" + args[0] + "\" wurde nicht gefunden"));
				return true;
			}
			world = Bukkit.createWorld(new WorldCreator(args[0]).generator(new EmptyChunkGenerator()));
			if(world == null) {
				player.sendMessage(Util.chat("&cBeim Laden der Welt ist ein Fehler aufgetreten"));
				return true;
			}
		}
		world.setAutoSave(true);
		player.teleport(world.getSpawnLocation());
		// The change gamemode should not be done to early
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
		    @Override
		    public void run(){
		        player.setGameMode(GameMode.CREATIVE);
		    }
		}, 3L);
		player.setGameMode(GameMode.CREATIVE);
		return true;
	}

}
