package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.metadata.FixedMetadataValue;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWchest implements CommandExecutor {

	private Main plugin;
	private Random random = new Random();
	
	public SWchest(Main plugin) {
		plugin.getCommand("swchest").setExecutor(this);
		this.plugin = plugin;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler dürfen diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;
		if(!sender.hasPermission("skywars.chest")) {
			player.sendMessage(Util.chat("&cDu hast nicht die benötigte Berechtigung: skywars.chest"));
			return true;
		}

		if(args.length < 2) {
			player.sendMessage(Util.chat("&cZu wenig Argumente"));
			return false;
		}
		
		int time = 0;
		if(args[1].contains(":")) {
			time = Util.secondsFromStr(args[1]);
		} else {
			time = Integer.parseInt(args[1]);
		}
		
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Skywars Kiste");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(args[0]);
		lore.add(Integer.toString(time));
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		
//		Location location = player.getLocation();
//		try {
//			if(args.length > 1) {
//				if(args.length >= 4) {
//					location.setX(Double.parseDouble(args[1]));
//					location.setY(Double.parseDouble(args[2]));
//					location.setZ(Double.parseDouble(args[3]));
//				}
//				else {
//					sender.sendMessage(Util.chat("&cZu wenig Argumente für die Koordinaten"));
//					return false;
//				}
//			}
//		}catch(NumberFormatException e) {
//			sender.sendMessage(Util.chat("&cKoordinaten ungültig. Hinweis: '~' wird noch nicht unterstützt :("));
//			return true;
//		}
//		Block block = location.getBlock();
//		block.setType(Material.CHEST);
//		LootTable lootTable = Bukkit.getLootTable(new NamespacedKey(plugin, "chests/chest"));
//		BlockState state = block.getState();
//		Lootable lootable = (Lootable) state;
//		lootable.setLootTable(lootTable);
//		state.update(true);
//		// use block.location to floor the values
//		ArmorStand entity = (ArmorStand)player.getWorld().spawnEntity(block.getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
//		entity.setCustomNameVisible(true);
//		entity.setCustomName(Util.chat("&aKiste voll!"));
//		entity.setVisible(false);
//		entity.setGravity(false);
//		entity.setCollidable(false);
//		entity.setMetadata("secondsTillRefill", new FixedMetadataValue(plugin, 0));
		
		return true;
	}

}
