package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWspawns implements CommandExecutor, TabCompleter {

	
	public SWspawns(Main plugin) {
		plugin.getCommand("swspawns").setExecutor(this);
		plugin.getCommand("swspawns").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] modes = new String[] {"add", "show"};
			for(String s : modes) {
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		if(args.length == 2 && args[0].equals("show")) {
			String[] options = new String[] {"true", "false"};
			for(String s : options) {
				if(s.startsWith(args[1])) list.add(s);
			}
		}
		return list;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
			return true;
		}
		Player player = (Player)sender;
		if(!sender.hasPermission("skywars.spawns")) {
			player.sendMessage(ChatColor.RED + "Du hast nicht die benötigte Berechtigung: skywars.spawns");
			return true;
		}
		if(args.length == 0) {
			player.sendMessage(Util.chat("&cZu wenige Argumente!"));
			return false;
		}
		if(args[0].equals("add")) {
			ArmorStand entity = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			entity.setCustomName("skywars_spawn");
			entity.setCustomNameVisible(false);
			entity.setGravity(false);
			entity.setMarker(true);
		} else if(args[0].equals("show")) {
			if(args.length < 2) {
				player.sendMessage(Util.chat("&c[true | false]"));
				return true;
			}
			if(args[1].equals("true")) {
				for(Entity entity : player.getWorld().getEntities()) {
					if(entity.getType() == EntityType.ARMOR_STAND) {
						if(entity.getCustomName().equals("skywars_spawn")) {
							((ArmorStand)entity).setVisible(true);
							// They should be markers, but maybe they were placed with an older version
							((ArmorStand)entity).setMarker(true);
						}
					}
				}
			} else if(args[1].equals("false")) {
				for(Entity entity : player.getWorld().getEntities()) {
					if(entity.getType() == EntityType.ARMOR_STAND) {
						if(entity.getCustomName().equals("skywars_spawn")) {
							((ArmorStand)entity).setVisible(false);
							((ArmorStand)entity).setMarker(true);
						}
					}
				}
			} else {
				player.sendMessage(Util.chat("&c[true | false]"));
			}
		}
		return true;
	}

}
