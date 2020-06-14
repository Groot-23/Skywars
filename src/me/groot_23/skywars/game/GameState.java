package me.groot_23.skywars.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.SkywarsKit;
import me.groot_23.skywars.game.Game.GameData;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.Arena;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class GameState {
	
	protected GameData data;
	protected Arena arena;
	protected int counter;
	protected World world;
	Main plugin;
	
	public GameState(Main plugin, GameData data, Arena arena) {
		this.data = data;
		this.arena = arena;
		this.plugin = plugin;
		world = arena.getWorld();
//		init(); // note that some variables might need initialization
	}
	
	public GameState(GameState state) {
		this(state.plugin, state.data, state.arena);
	}
	
	public abstract void init();
	public abstract GameState update();
	
	public static class Lobby extends GameState {

		public Lobby(GameState state) {
			super(state);
			counter = 30;
			init();
		}
		
		public Lobby(Main plugin, GameData data, Arena arena) {
			super(plugin, data, arena);
			counter = 30;
			init();
		}

		@Override
		public GameState update() {
			plugin.skywarsScoreboard.updatePreGame(arena.getWorld(), arena.getMaxPlayers(), counter);
			
			int numPlayers = arena.getWorld().getPlayers().size();
			for(Player player : world.getPlayers()) {
				player.setHealth(20);
				player.setFoodLevel(20);
			}
			if(numPlayers == arena.getMaxPlayers() || counter <= 0) {
				return new Spawn(this);
			}
			if(numPlayers < arena.getMinPlayers()) {
				counter = 30;
			} else {
				counter--;
			}
			return this;
		}

		@Override
		public void init() {
			arena.resetBorder();
			arena.refillChests();
		}
		
	}
	
	public static class Spawn extends GameState {

		public Spawn(GameState state) {
			super(state);
			counter = 10;
			init();
		}

		@Override
		public void init() {
			if(arena.getSpawns().size() < world.getPlayers().size()) {
				Bukkit.getServer().broadcastMessage(Util.chat("&cZu wenige Spawns! Fehler beim Starten von Skywars :("));
				return;
			}
			for(int i = 0; i < world.getPlayers().size(); i++) {
				world.getPlayers().get(i).teleport(arena.getSpawns().get(i));
				plugin.skywarsScoreboard.initGame(world.getPlayers().get(i), arena.getWorld().getPlayers().size(), 
						"Start", counter, data.getDeathMatchBegin() ,SWconstants.LENGTH_OF_GAME, arena.getMapName());			
			}
			arena.removeLobby();
			arena.disableJoin();
		}

		@Override
		public GameState update() {
			plugin.skywarsScoreboard.updateGame(world, world.getPlayers().size(), "Start", counter, data.getDeathMatchBegin(), SWconstants.LENGTH_OF_GAME);
			if(counter % 10 == 0) {
				for(Player p : world.getPlayers()) {
					p.sendMessage(Util.chat("Skywars startet in &c" + counter));
				}
			}
			if(counter <= 5) {
				for(Player p : world.getPlayers()) {
					if(counter != 0) {
						p.sendMessage(Util.chat("Skywars startet in &c" + counter));
						p.sendTitle(Util.chat("&a" + Integer.toString(counter)) , Util.chat("&dMache dich bereit!"), 3, 14, 3);
					} else {
						p.sendMessage(Util.chat("&aSkywars gestartet"));
						p.sendTitle(Util.chat("&aSkywars gestartet") , Util.chat("&dder Kampf beginnt!"), 3, 20, 3);
						// remove falldamage
						p.setFallDistance(-1000);
						arena.removeGlassSpawns();
						return new Playing(this);
					}

				}
			}
			counter--;
			return this;
		}
		
	}
	
	public static class Playing extends GameState {

		int dynamicRefillTime;
		int refillCounter;
		int deathMatchCounter;
		BossBar bossbar;
		
		public Playing(GameState state) {
			super(state);
			counter = SWconstants.LENGTH_OF_GAME;
			dynamicRefillTime = data.getRefillTime();
			refillCounter = dynamicRefillTime;
			deathMatchCounter = data.getDeathMatchBegin();
			init();
		}

		@Override
		public void init() {
			bossbar = Bukkit.createBossBar(new NamespacedKey(plugin, "deathMatch"), ChatColor.GREEN + "Zeit übrig " + ChatColor.YELLOW + "bis zum " + 
		ChatColor.LIGHT_PURPLE + "SKYWARS "+ ChatColor.RED + "Death-Match", BarColor.PURPLE, BarStyle.SEGMENTED_20);
			bossbar.setProgress(1);
			arena.getWorld().setPVP(true);
			for(Player player : world.getPlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				SkywarsKit kit = null;
				if(player.hasMetadata("skywarsKit")) {
					kit = plugin.kitByName.get(player.getMetadata("skywarsKit").get(0).asString());
				} if(kit == null) {
					kit = plugin.kits.get(0);
				}
				kit.applyToPlayer(player);
				bossbar.addPlayer(player);
			}
		}

		@Override
		public GameState update() {
			int playersLeft = 0;
			Player potentialWinner = null;
			for(Player p : world.getPlayers()) {
				if(p.getGameMode() == GameMode.SURVIVAL) {
					playersLeft++;
					potentialWinner = p;
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "Teaming ist verboten!!!"));
				}
			}
			plugin.skywarsScoreboard.updateGame(world, playersLeft, "Kisten Befüllung", refillCounter, deathMatchCounter, counter);
			if(playersLeft == 1) {
				bossbar.removeAll();
				return new Victory(this, potentialWinner);
			}
			arena.updateChestTimer(refillCounter);
			if(refillCounter <= 0) {
				dynamicRefillTime += data.getRefillTimeChange();
				refillCounter = dynamicRefillTime;
				arena.refillChests();
			}
			if(deathMatchCounter == 1) {
				arena.shrinkBorder(data.getDeatchMatchBorderShrinkTime());
				for(Player p : world.getPlayers()) {
					p.sendTitle(ChatColor.BOLD + "" + ChatColor.RED + "Death Match!",ChatColor.BOLD + "" + ChatColor.DARK_RED + "Gelange zur Mitte!", 3, 100, 3);
				}
			} else {
				bossbar.setProgress((double)deathMatchCounter / data.getDeathMatchBegin());
			}
//			if(counter <= 0) {
//				draw();
//				return null;
//			}
			counter--;
			refillCounter--;
			if(deathMatchCounter > 0) {
				deathMatchCounter--;
			}
			return this;
		}
		
	}
	
	public static class Victory extends GameState {

		private Player winner;
		
		public Victory(GameState state, Player winner) {
			super(state);
			this.winner = winner;
			counter = 15;
			init();
		}

		@Override
		public void init() {
			for(Player player : world.getPlayers()) {
				player.sendTitle(Util.chat("&c" + winner.getName()), Util.chat("&5Hat GEWONNEN"), 3, 50, 3);
			    player.setGameMode(GameMode.SPECTATOR);
			}
		}

		@Override
		public GameState update() {
			if(counter-- <= 0) {
				return null;
			}
			return this;
		}
		
	}
}
