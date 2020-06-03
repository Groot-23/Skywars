package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class GameEvents implements Listener {
	
	private Main plugin;
	
	public GameEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
    	e.getEntity().setGameMode(GameMode.SPECTATOR);
	}
	
	// Later it will be changed to PlayerDeathEvent, but it's helpful for testing it alone
	@EventHandler
	public void onKill(EntityDeathEvent e) {
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
	
}
