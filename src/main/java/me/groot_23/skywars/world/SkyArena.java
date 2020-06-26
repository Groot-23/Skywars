package me.groot_23.skywars.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.groot_23.ming.MiniGame;
import me.groot_23.skywars.game.SkyChest;
import me.groot_23.skywars.util.Util;

public class SkyArena extends me.groot_23.ming.world.Arena{

	private List<SkyChest> chests;
	
	public SkyArena(MiniGame game, World world, String mapName) {
		super(game, world, mapName);
		findChests();
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
		removeArea(midSpawn, 20, 5);
	}
	
	public void removeGlassSpawns() {
		for(Location l : spawns) {
			removeArea(l, 4, 4, Material.GLASS);
		}

	}

}
