package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.groot_23.skywars.Main;

public class StopLobbyLeave implements Listener {

	Main plugin;

	public StopLobbyLeave(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	private void stopWorld(World w) {
		// if world is lobby
		if (w.getName().startsWith("skywars_lobby_")) {
			plugin.lobbyManager.stopLobby(w);
			System.out.println("[Skywars] lobby stopped: " + w.getName());
		}
		// if world is a registered skywars world
		else if (plugin.getConfig().contains("worlds." + w.getName())) {
			Bukkit.unloadWorld(w, true);
			System.out.println("[Skywars] unloaded world after edit: " + w.getName());
		}
		// else we don't know if it is supposed to be stopped
		// -> don't stop it, maybe another plugin wants it to keep loaded
	}


	@EventHandler
	public void playerLeave(PlayerChangedWorldEvent e) {
		World w = e.getFrom();
		if (w.getPlayers().size() == 0) {
			stopWorld(w);
		}

	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		World w = e.getPlayer().getWorld();
		// quit event seems to be fired before the player disconnects
		if (w.getPlayers().size() == 1) {
			stopWorld(w);
		}
	}
}
