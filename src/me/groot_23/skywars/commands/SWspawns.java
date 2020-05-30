package me.groot_23.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWspawns implements CommandExecutor {

	private Main plugin;
	
	public SWspawns(Main plugin) {
		plugin.getCommand("swspawns").setExecutor(this);
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
			return true;
		}
		Player player = (Player)sender;
		if(args.length == 0) {
			player.sendMessage(Util.chat("&cZu wenige Argumente!"));
			return false;
		}
		if(args[0].equals("add")) {
			ArmorStand entity = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			entity.setCustomName("skywars_spawn");
			entity.setCustomNameVisible(false);
			entity.setGravity(false);
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
						}
					}
				}
			} else if(args[1].equals("false")) {
				for(Entity entity : player.getWorld().getEntities()) {
					if(entity.getType() == EntityType.ARMOR_STAND) {
						if(entity.getCustomName().equals("skywars_spawn")) {
							((ArmorStand)entity).setVisible(false);
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
