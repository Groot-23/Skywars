package me.groot_23.skywars.game;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.GameState.Lobby;
import me.groot_23.skywars.world.Arena;

public class Game {
	private Arena arena;
	private GameState state;
	private Main plugin;
	private GameData data;
	
	public Game(Main plugin, Arena arena, GameData data) {
		this.plugin = plugin;
		this.arena = arena;
		this.data = data;
		state = new GameState.Lobby(plugin, data, arena);
	}
	
	public Game(Main plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
		int refillTime = plugin.getConfig().getInt("refillTime");
		int refillTimeChange = plugin.getConfig().getInt("refillTimeChange");
		int deathMatchBegin = plugin.getConfig().getInt("deathMatchBegin");
		int deathMatchBorderShrinkTime = plugin.getConfig().getInt("deathMatchBorderShrinkTime");
		data = new GameData(refillTime, refillTimeChange, deathMatchBegin, deathMatchBorderShrinkTime);
		state = new GameState.Lobby(plugin, data, arena);
	}
	
	public void start() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(state == null) {
					plugin.arenaProvider.stopLobby(arena.getWorld());
					cancel();
					return;
				}
				if(Bukkit.getWorld(arena.getWorld().getUID()) == null) {
					System.out.println("Canceled Game in " + arena.getWorld().getName());
					cancel();
					return;
				}
				state = state.update();
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public static class GameData {
		private int refillTime;
		private int refillTimeChange;
		private int deathMatchBegin;
		private int deathMatchBorderShrinkTime;
		
		public GameData(int refillTime, int refillTimeChange, int deathMatchBegin, int deathMatchBorderShrinkTime) {
			this.refillTime = refillTime;
			this.refillTimeChange = refillTimeChange;
			this.deathMatchBegin = deathMatchBegin;
			this.deathMatchBorderShrinkTime = deathMatchBorderShrinkTime;
		}
		
		public int getRefillTime() {
			return refillTime;
		}
		
		public int getRefillTimeChange() {
			return refillTimeChange;
		}
		
		public int getDeathMatchBegin() {
			return deathMatchBegin;
		}
		
		public int getDeatchMatchBorderShrinkTime() {
			return deathMatchBorderShrinkTime;
		}
	}
}
