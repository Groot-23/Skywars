package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;

public class GameEvents implements Listener {
	
	private Main plugin;
	
	public GameEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(e.getEntity().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
	    	e.getEntity().setGameMode(GameMode.SPECTATOR);
		}
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player killer = e.getEntity().getKiller();
		if(killer != null) {
			if(killer.getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
				plugin.skywarsScoreboard.addKill(killer);
			}
		}
	}
	
	@EventHandler
	public void onWorldLeave(PlayerChangedWorldEvent e) {
		if(e.getFrom().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			Player p = e.getPlayer();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run() {
					p.setGameMode(GameMode.ADVENTURE);
					p.getInventory().clear();
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}, 5);

		}
	}
	
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if(e.getPlayer().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    @Override
			    public void run(){
			    	e.getPlayer().setGameMode(GameMode.SPECTATOR);
			    }
			}, 3L);
		}
	}
	
	@EventHandler
	public void preventSpawn(CreatureSpawnEvent e) {
		World world = e.getEntity().getWorld();
		if(!world.getMetadata("skywars_edit_world").isEmpty() || world.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			if(e.getSpawnReason() == SpawnReason.NATURAL) {
				e.setCancelled(true);
			}
		}
	}
	
}
