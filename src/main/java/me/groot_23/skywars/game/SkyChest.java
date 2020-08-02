package me.groot_23.skywars.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.primitives.Ints;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;

public class SkyChest {
	public Block chest;
	public ArmorStand hologram;
	public Main plugin;
	
	public SkyChest(Entity chestMarker) {
		chest = chestMarker.getLocation().getBlock();
		if(chest.getType() != Material.CHEST) {
			throw new IllegalArgumentException("The Block of the chest marker has to be a chest!");
		}
		World world = chest.getWorld();
		plugin = Main.getInstance();
		
		String name = chestMarker.getCustomName();
		String loot = name.substring(name.indexOf('|') + 1);
		chest.setMetadata(SWconstants.SW_LOOT_TABLE, new FixedMetadataValue(plugin, loot));
		ArmorStand armorStand = null;
		Collection<Entity> nearbyEntities = world.getNearbyEntities(chest.getLocation().add(0.5, 1, 0.5), 0.1, 0.1, 0.1);
		for(Entity ne : nearbyEntities) {
			if(ne.getType() == EntityType.ARMOR_STAND) {
				armorStand = (ArmorStand) ne;
				break;
			}
		}
		if(armorStand != null) {
			hologram = armorStand;
		} else {
			plugin.getLogger().warning("No ArmorStand found for chest at: " + chest.getLocation().toString());
		}
	}
	
	public void updateHologram(String str) {
		hologram.setCustomName(str);
	}
	
	public void refill() {
		String loot = chest.getMetadata(SWconstants.SW_LOOT_TABLE).get(0).asString();
		LootTable lootTable = Bukkit.getLootTable(new NamespacedKey(Main.getInstance(), "chests/" + loot));
		BlockState state = chest.getState();
		Lootable lootable = (Lootable) state;
		lootable.setLootTable(lootTable);
		state.update(true);
		hologram.setCustomName(ChatColor.GREEN + "Kiste voll!");
		
		// Rearrange items:
		// 1. combine stacks if possible
		Inventory inv = ((Chest)state).getBlockInventory();
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(int i = 0; i < inv.getSize(); ++i) {
			ItemStack item = inv.getItem(i);
			if(item != null) {
				int remaining = item.getAmount();
				for(int k = 0; k < list.size(); ++k) {
					if(list.get(k).isSimilar(item)) {
						int move = Math.min(remaining, item.getMaxStackSize() - list.get(k).getAmount());
						remaining -= move;
						list.get(k).setAmount(list.get(k).getAmount() + move);
					}
				}
				if(remaining != 0) {
					item.setAmount(remaining);
					list.add(item);
				}
			}
		}
		inv.clear();
		// 2. shuffle items
		List<Integer> slots = Ints.asList(IntStream.range(0, inv.getSize()).toArray());
		Collections.shuffle(slots);
		for(int i = 0; i < list.size(); ++i) {
			inv.setItem(slots.get(i), list.get(i));
		}
	}
}
