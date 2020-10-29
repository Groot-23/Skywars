package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.groot_23.pixel.commands.CommandBase;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWchest extends CommandBase {
	
	public SWchest(Main plugin) {
		super(plugin, "swchest", "skywars.chest");
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			String[] defaultLootTables = new String[] {"normal", "good"};
			for(String s : defaultLootTables) {
				if(s.startsWith(args[0])) list.add(s);
			}
		}
		return list;
	}
	
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler dürfen diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;

		if(args.length == 0) {
			player.sendMessage(Util.chat("&cGib den LootTable an"));
			return false;
		}

		
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Skywars Kiste");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(args[0]);
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		
		return true;
	}

}
