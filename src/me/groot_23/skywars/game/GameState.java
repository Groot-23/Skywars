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
import me.groot_23.skywars.language.LanguageKeys;
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
			for (Player player : world.getPlayers()) {
				player.setHealth(20);
				player.setFoodLevel(20);
				player.setSaturation(5);
			}
			if (numPlayers == arena.getMaxPlayers() || counter <= 0) {
				return new Spawn(this);
			}
			if (numPlayers < arena.getMinPlayers()) {
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
			if (arena.getSpawns().size() < world.getPlayers().size()) {
				Bukkit.getServer()
						.broadcastMessage(Util.chat("&cZu wenige Spawns! Fehler beim Starten von Skywars :("));
				return;
			}
			for (int i = 0; i < world.getPlayers().size(); i++) {
				world.getPlayers().get(i).teleport(arena.getSpawns().get(i));
				plugin.skywarsScoreboard.initGame(world.getPlayers().get(i), arena.getWorld().getPlayers().size(),
						LanguageKeys.EVENT_START, counter, data.getDeathMatchBegin(), SWconstants.LENGTH_OF_GAME,
						arena.getMapName());
			}
			arena.removeLobby();
			arena.disableJoin();
		}

		@Override
		public GameState update() {
			plugin.skywarsScoreboard.updateGame(world, world.getPlayers().size(), LanguageKeys.EVENT_START, counter,
					data.getDeathMatchBegin(), SWconstants.LENGTH_OF_GAME);
			if (counter % 10 == 0) {
				for (Player p : world.getPlayers()) {
					p.sendMessage(plugin.langManager.getTranslation(p, LanguageKeys.START_IN) +  " " + ChatColor.RED + counter);
				}
			}
			if (counter <= 5) {
				for (Player p : world.getPlayers()) {
					if (counter != 0) {
						p.sendMessage(plugin.langManager.getTranslation(p, LanguageKeys.START_IN) +  " " + ChatColor.RED + counter);
						p.sendTitle(ChatColor.GREEN + "" + counter, ChatColor.LIGHT_PURPLE + 
								plugin.langManager.getTranslation(p, LanguageKeys.GET_READY), 3, 14, 3);
					} else {
						String started = ChatColor.GREEN + plugin.langManager.getTranslation(p, LanguageKeys.STARTED);
						p.sendMessage(started);
						p.sendTitle(started, ChatColor.LIGHT_PURPLE + plugin.langManager.getTranslation(p, 
								LanguageKeys.FIGHT_BEGINS), 3, 20, 3);
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
			bossbar = Bukkit.createBossBar(
					Util.chat(plugin.langManager.getDefault(LanguageKeys.BOSSBAR_TITLE)),
					BarColor.PURPLE, BarStyle.SEGMENTED_20);
			bossbar.setProgress(1);
			arena.getWorld().setPVP(true);
			for (Player player : world.getPlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				SkywarsKit kit = null;
				if (player.hasMetadata("skywarsKit")) {
					kit = plugin.kitByName.get(player.getMetadata("skywarsKit").get(0).asString());
				}
				if (kit == null) {
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
			for (Player p : world.getPlayers()) {
				if (p.getGameMode() == GameMode.SURVIVAL) {
					playersLeft++;
					potentialWinner = p;
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + 
									plugin.langManager.getTranslation(p, LanguageKeys.NO_TEAMING)));
				}
			}
			plugin.skywarsScoreboard.updateGame(world, playersLeft, LanguageKeys.EVENT_REFILL, refillCounter,
					deathMatchCounter, counter);
			if (playersLeft == 1) {
				bossbar.removeAll();
				return new Victory(this, potentialWinner);
			}
			arena.updateChestTimer(refillCounter);
			if (refillCounter <= 0) {
				dynamicRefillTime += data.getRefillTimeChange();
				refillCounter = dynamicRefillTime;
				arena.refillChests();
			}
			if (deathMatchCounter == 1) {
				arena.shrinkBorder(data.getDeatchMatchBorderShrinkTime());
				for (Player p : world.getPlayers()) {
					p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Death Match!", ChatColor.RED + ""
							+ ChatColor.BOLD + plugin.langManager.getTranslation(p, LanguageKeys.GO_TO_MID), 3, 100, 3);
				}
			} else {
				bossbar.setProgress((double) deathMatchCounter / data.getDeathMatchBegin());
			}
//			if(counter <= 0) {
//				draw();
//				return null;
//			}
			counter--;
			refillCounter--;
			if (deathMatchCounter > 0) {
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
			for (Player player : world.getPlayers()) {
				player.sendTitle(Util.chat("&c" + winner.getName()), ChatColor.DARK_PURPLE + 
						plugin.langManager.getTranslation(player, LanguageKeys.VICTORY), 3, 50, 3);
				player.setGameMode(GameMode.SPECTATOR);
			}
		}

		@Override
		public GameState update() {
			if (counter-- <= 0) {
				return null;
			}
			return this;
		}

	}
}
