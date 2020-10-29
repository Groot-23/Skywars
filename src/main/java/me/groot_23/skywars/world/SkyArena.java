package me.groot_23.skywars.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.world.Arena;
import me.groot_23.pixel.world.WorldMarker;
import me.groot_23.skywars.game.SkyChest;
import me.groot_23.skywars.util.Util;

public class SkyArena extends Arena{

	private List<SkyChest> chests;
	protected List<Location> spawns;
	
	public SkyArena(Game game, World world, String mapName) {
		super(game, world, mapName);
		findChests();
		findPlayerSpawns();
	}
	
	private void findChests() {
		chests = new ArrayList<SkyChest>();
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				String name = entity.getCustomName();
				if(name.startsWith("skywars_chest_marker")) {
					chests.add(new SkyChest(entity));
				}
			}
		}
	}
	
	public void refillChests() {
		for(SkyChest chest : chests) {
			chest.refill();
		}
	}
	
	public void updateChestTimer(int time) {
		String timeStr = Util.minuteSeconds(time);
		for(SkyChest chest : chests) {
			if(chest.hologram.getCustomName().contains(":")) {
				chest.hologram.setCustomName(timeStr);
			}
		} 
	}
	
	public void removeLobby() {
		removeArea(midSpawn, 20, 7);
	}
	
	public void removeGlassSpawns() {
		for(Location l : spawns) {
			removeArea(l, 4, 4, Material.GLASS);
		}

	}

	public List<Location> getSpawns() {
		return spawns;
	}
	
	protected void findPlayerSpawns() {
		spawns = WorldMarker.findMarkers(world, "sky_spawn");
//		for (Entity entity : world.getEntities()) {
//			if (entity.getType() == EntityType.ARMOR_STAND) {
//				if (entity.getCustomName().equals("skywars_spawn")) {
//					spawns.add(entity.getLocation());
//				}
//			}
//		}
		Collections.shuffle(spawns);
	}
}
