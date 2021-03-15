package me.groot_23.skywars.game;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.player.PlayerUtil;
import me.groot_23.pixel.player.team.GameTeam;
import me.groot_23.pixel.player.team.TeamHandler;
import me.groot_23.pixel.world.GameplayModifier;
import me.groot_23.pixel.world.PixelWorld;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed.Draw;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed.EndGame;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed.StartGame;
import me.groot_23.skywars.game.tasks.SkyTasksRepeated;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.world.SkyArena;

public class SkyGame extends Game {

	public final SkyArena skyArena;

	public int refillTime;
	public int refillTimeChange;
	public int deathMatchBegin;
	public int deathMatchBorderShrinkTime;

	public SkyGame(JavaPlugin plugin, String name, PixelWorld world, Set<Player> players, TeamHandler teams) {
		super(plugin, name, world, players, teams);

		skyArena = new SkyArena(this, world);

		teamHandler.chatPrefix = Main.chatPrefix;
		
		skyArena.initBorder();
		skyArena.refillChests();
		arena.getWorld().setPVP(true);
		
		GameplayModifier mod = GameplayModifier.old();
		mod.canSwimWater = true;
		GameplayModifier.set(world.getWorld(), mod);

		deathMatchBegin = plugin.getConfig().getInt("deathMatchBegin");
		deathMatchBorderShrinkTime = plugin.getConfig().getInt("deathMatchBorderShrinkTime");
		refillTime = plugin.getConfig().getInt("refillTime");
		refillTimeChange = plugin.getConfig().getInt("refillTimeChange");
		
		int i = 0;
		for (GameTeam team : teamHandler.getTeams()) {
			for (Player p : team.getPlayers()) {
				p.teleport(skyArena.getSpawns().get(i));
				// remove team selector if present!
				p.getInventory().setItem(0, null);
				p.sendTitle(
						ChatColor.GOLD + "Map" + SkywarsScoreboard.COLON + ChatColor.WHITE
								+ LanguageApi.getTranslation(p, "map." + arena.getMapName()),
						ChatColor.AQUA + "Builder" + SkywarsScoreboard.COLON + ChatColor.WHITE + arena.getBuilder(),
						10, 80, 10);
			}
			i++;
		}
		skyArena.removeLobby();

		taskManager.addRepeated(new SkyTasksRepeated.Game1(this), "game1");
		taskManager.addRepeated(new SkyTasksRepeated.Game20(this), "game20");

		taskManager.addTask(new StartGame(this), 10 * 20, StartGame.id);
	}
	

	@Override
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(Main.chatPrefix + String.format(LanguageApi.getDefault(PixelLangKeys.DEATH), event.getEntity().getName()));
		Player killer = event.getEntity().getKiller();
		if(killer == null) {
			killer = PlayerUtil.getLastAttacker(event.getEntity());
		}
		if (killer != null && killer != event.getEntity()) {
			Pixel.getEconomy().depositPlayer(killer, Main.getInstance().getConfig().getInt("coins_kill"));
			killer.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(killer, LanguageKeys.KILL_COINS), Pixel.getEconomy().format(Main.getInstance().getConfig().getInt("coins_kill"))));
			SkywarsScoreboard.addKill(killer);
			String msg = "Herzen von " + killer.getName() + ": ";
			for(int i = 0; i < 10; ++i) {
				if(killer.getHealth() - 2*i > 1) msg += ChatColor.RED + "\u2764";
				else if(killer.getHealth() - 2*i > 0) msg += ChatColor.RED + "\u2765";
				else msg += ChatColor.WHITE +  "\u2764";
			}
			event.getEntity().sendMessage(Main.chatPrefix + msg);
		} 
		PlayerUtil.clear(event.getEntity());
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				event.getEntity().spigot().respawn();
			}
		}, 1);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(teamHandler.getTeamsAliveCount() == 1) {
					GameTeam winner = teamHandler.getTeamsAlive().get(0);
					victory(winner);
				} else if(teamHandler.getTeamsAliveCount() == 0) {
					if(taskManager.getTask(SkyTasksDelayed.Draw.id) != null) {
						taskManager.getTask(SkyTasksDelayed.Draw.id).runTaskEarly();
					}
				}
			}
		}.runTaskLater(plugin, 5);
	}
	
	private void victory(GameTeam winner) {
		for (Player player : players) {
			if (teamHandler.teamSize > 1) {
				player.sendTitle(
						GameTeam.toChatColor(winner.getColor()) + "Team " + LanguageApi.translateColor(player, winner.getColor()),
						ChatColor.DARK_PURPLE + LanguageApi.getTranslation(player, LanguageKeys.VICTORY), 3, 50, 3);
				player.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(player, LanguageKeys.VICTORY_TEAM),
						GameTeam.toChatColor(winner.getColor()) + LanguageApi.translateColor(player, winner.getColor())));
			} else {
				player.sendTitle(ChatColor.GOLD + winner.getPlayers().get(0).getDisplayName(), ChatColor.DARK_PURPLE + LanguageApi.getTranslation(player, LanguageKeys.VICTORY), 3, 50, 3);
				player.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(player, LanguageKeys.VICTORY_PLAYER), winner.getPlayers().get(0).getDisplayName()));
			}
			Pixel.setSpectator(player, true);
		}
		for (Player player : winner.getPlayers()) {
			Pixel.getEconomy().depositPlayer(player, Main.getInstance().getConfig().getInt("coins_victory"));
			player.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(player, PixelLangKeys.MORE_COINS),
					Pixel.getEconomy().format(Main.getInstance().getConfig().getInt("coins_victory"))));
		}
		taskManager.removeTask(Draw.id);
		taskManager.addTask(new EndGame(this), 200, EndGame.id);
	}
	
	@Override
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(arena.getMidSpawn());
		new BukkitRunnable() {
			@Override
			public void run() {
				event.getPlayer().setGameMode(GameMode.ADVENTURE);
				Pixel.setSpectator(event.getPlayer(), true);
			}
		}.runTaskLater(plugin, 2);

	}

	@Override
	public void onPlayerLeave(Player player) {
		if(teamHandler.getTeamsAliveCount() == 1) {
			GameTeam winner = teamHandler.getTeamsAlive().get(0);
			victory(winner);
		} else if(teamHandler.getTeamsAliveCount() == 0) {
			if(taskManager.getTask(SkyTasksDelayed.Draw.id) != null) {
				taskManager.getTask(SkyTasksDelayed.Draw.id).runTaskEarly();
			}
		}
		
		if (players.size() == 0) {
			endGame();
		}
	}

	
	/*
	 * ================================================================
	 * ================    Utility Events    ==========================
	 * ================================================================
	 */
	
	/*
	 * TNT explosion after placing
	 */
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().getType() == Material.TNT) {
			event.getBlock().setType(Material.AIR);
			TNTPrimed tnt = (TNTPrimed)event.getPlayer().getWorld().spawnEntity(
					event.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
			tnt.setFuseTicks(40);
		}
	}
	
	/*
	 * Remove bottle after drinking a potion
	 */
	
	@Override
	public void onItemConsume(PlayerItemConsumeEvent event) {
		if(event.getItem().getType() == Material.POTION) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(event.getPlayer().getEquipment().getItemInMainHand().getType() == Material.GLASS_BOTTLE)
						event.getPlayer().getEquipment().setItemInMainHand(null);
					if(event.getPlayer().getEquipment().getItemInOffHand().getType() == Material.GLASS_BOTTLE)
						event.getPlayer().getEquipment().setItemInOffHand(null);
				}
			}.runTaskLater(plugin, 1);
		}
	}
	
	/*
	 * always lapis in enchanting table
	 */
	
	@Override
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) event.getInventory();
			inv.setSecondary(new ItemStack(Material.LAPIS_LAZULI, 64));
		}
	}
	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getType() == InventoryType.ENCHANTING) {
			if (event.getCurrentItem().getType() == Material.LAPIS_LAZULI) {
				event.setCancelled(true);
			}
		}
	}
	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) event.getInventory();
			inv.setSecondary(null);
		}
	}


}
