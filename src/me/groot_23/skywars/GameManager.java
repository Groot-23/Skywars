package me.groot_23.skywars;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import me.groot_23.skywars.events.RefillChests;
import me.groot_23.skywars.util.Util;

public class GameManager {

	private Main plugin;
	
	public GameManager(Main plugin) {
		this.plugin = plugin;
	}
	
	public void initLobby(World world) {
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				String name = entity.getCustomName();
				if(name.startsWith("skywars_chest_marker")) {
					String sub = name.substring(name.indexOf('|') + 1);
					String loot = sub.substring(0, sub.indexOf('|'));
					int refillTime = Integer.parseInt(sub.substring(sub.indexOf('|') + 1));
					Block block = entity.getLocation().getBlock();
					if(block.getType() == Material.CHEST) {
						block.setMetadata(RefillChests.SKYWARS_LOOT, new FixedMetadataValue(plugin, loot));
						block.setMetadata(RefillChests.SKYWARS_REFILL_TIME, new FixedMetadataValue(plugin, refillTime));
						RefillChests.RefillRunnable.refillChest(block);
					}
				}
			}
		}
	}
	
	public void startGame(World world) {
		Bukkit.getServer().broadcastMessage(Util.chat("&5Skywars wurde gestartet"));
	}
}
