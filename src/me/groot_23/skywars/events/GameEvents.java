package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class GameEvents implements Listener {
	
	private Main plugin;
	
	public GameEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	void updatePlayerCount(World world, Player deadPlayer) {
		if(world.getName().startsWith("skywars_lobby_")) {
			int count = 0;
			Player potentialWinner = null;
			for(Player p : world.getPlayers()) {
				if(p.getGameMode() == GameMode.SURVIVAL && p != deadPlayer) {
					count++;
					potentialWinner = p;
				}
			}
			if(count == 1) {
				for(Player p : world.getPlayers()) {
					p.sendTitle(Util.chat("&c" + potentialWinner.getName()), Util.chat("&5Hat GEWONNEN"), 3, 30, 3);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					
					@Override
					public void run() {
						// maybe the world was deleted and got overwritten with a new lobby
						if(Bukkit.getWorld(world.getUID()) != null)
							plugin.lobbyManager.stopLobby(world);
					}
				}, 200);
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		updatePlayerCount(e.getEntity().getWorld(), e.getEntity());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if(e.getPlayer().getWorld().getName().startsWith("skywars_lobby_")) {
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
