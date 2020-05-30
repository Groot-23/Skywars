package me.groot_23.skywars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
		ArrayList<Location> spawns = new ArrayList<Location>();
		for(Entity entity : world.getEntities()) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				if(entity.getCustomName().equals("skywars_spawn")) {
					spawns.add(entity.getLocation());
				}
			}
		}
		Collections.shuffle(spawns);
		if(spawns.size() < world.getPlayers().size()) {
			Bukkit.getServer().broadcastMessage(Util.chat("&cZu wenige Spawns! Fehler beim Starten von Skywars :("));
			return;
		}
		for(int i = 0; i < world.getPlayers().size(); i++) {
			world.getPlayers().get(i).teleport(spawns.get(i));
		}
		Bukkit.getServer().broadcastMessage(Util.chat("&5Skywars wurde gestartet"));
	}
}
