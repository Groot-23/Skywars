package me.groot_23.skywars.game;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.metadata.FixedMetadataValue;

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
	}
}
