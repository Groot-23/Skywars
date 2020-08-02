package me.groot_23.skywars.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.ming.MinG;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.ming.gui.GuiItem;
import me.groot_23.ming.gui.GuiItem.UseAction;
import me.groot_23.ming.language.LanguageManager;
import me.groot_23.ming.player.GameTeam;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.SkyArena;

public class SkyGame extends Game {

	public final SkyArena skyArena;

	public int refillTime;
	public int refillTimeChange;
	public int deathMatchBegin;
	public int deathMatchBorderShrinkTime;

	public SkyGame(MiniGameMode mode, String group) {
		super(mode, group);
		skyArena = (SkyArena) arena;
		
		skyArena.initBorder();
		skyArena.refillChests();
		arena.getWorld().setPVP(false);

		JavaPlugin plugin = mode.getPlugin();
		deathMatchBegin = plugin.getConfig().getInt("deathMatchBegin");
		deathMatchBorderShrinkTime = plugin.getConfig().getInt("deathMatchBorderShrinkTime");
		refillTime = plugin.getConfig().getInt("refillTime");
		refillTimeChange = plugin.getConfig().getInt("refillTimeChange");
	}

	@Override
	public Arena createArena(World world, String mapName) {
		return new SkyArena(this, world, mapName);
	}

	@Override
	public void onJoin(Player player) {
		MinG.resetPlayer(player, plugin);
		LanguageManager langManager = miniGame.getLangManager();
		// init hotbar
		GuiItem kitSelector = Main.game.createGuiItem(Material.CHEST,
				Util.chat(langManager.getTranslation(player, LanguageKeys.KIT_SELECTOR)));
		kitSelector.addActionUseRunnable("openGui", UseAction.RIGHT_CLICK);
		player.getInventory().setItem(4, kitSelector.getItem());

		GuiItem lobbyLeave = Main.game.createGuiItem(Material.MAGMA_CREAM,
				Util.chat(langManager.getTranslation(player, LanguageKeys.LEAVE)));
		lobbyLeave.addActionUse("swleave", UseAction.RIGHT_CLICK, UseAction.LEFT_CLICK);
		player.getInventory().setItem(8, lobbyLeave.getItem());

		GuiItem teamSelector = Main.game.createGuiItem(Material.OAK_SIGN);
		teamSelector.addActionUseRunnable("open_kit_selector");
		player.getInventory().setItem(0, teamSelector.getItem());
		
		SkywarsScoreboard.resetKills(player);
		SkywarsScoreboard.init(player);
		SkywarsScoreboard.initPreGame(player, arena.getMaxPlayers(), 30, arena.getMapName(), mode.getName());

		
		if(players.size() == arena.getMinPlayers()) {
			// start lobby task
			taskManager.addTask(new SkyTasksDelayed.GoToSpawn(this, 30 * 20), SkyTasksDelayed.GoToSpawn.id);
		}
		if (players.size() == arena.getMaxPlayers()) {
			// end lobby task sooner
			taskManager.getTask(SkyTasksDelayed.GoToSpawn.id).runTaskEarly();
		}
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().getType() == Material.TNT) {
			event.getBlock().setType(Material.AIR);
			TNTPrimed tnt = (TNTPrimed)event.getPlayer().getWorld().spawnEntity(
					event.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
			tnt.setFuseTicks(40);
		}
	}

	@Override
	public void onDeath(PlayerDeathEvent event) {
//		System.out.println("onDeath from SkyGame!");
		Player killer = event.getEntity().getKiller();
		if(killer == null) {
			killer = MinG.getLastAttacker(event.getEntity());
		}
		if (killer != null) {
//			System.out.println("Killer: " + killer.getName());
			SkywarsScoreboard.addKill(killer);
		} else {
//			System.out.println("Killer: null");
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				event.getEntity().setGameMode(GameMode.SPECTATOR);
				event.getEntity().spigot().respawn();
			}
		}, 1);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(teamHandler.getTeamsAliveCount() == 1) {
					GameTeam winner = teamHandler.getTeamsAlive().get(0);
					taskManager.addTask(new SkyTasksDelayed.Victory(SkyGame.this, 0, winner), SkyTasksDelayed.Victory.id);
				} else if(teamHandler.getTeamsAliveCount() == 0) {
					taskManager.addTask(new SkyTasksDelayed.Draw(SkyGame.this, 0), SkyTasksDelayed.Draw.id);
				}
			}
		}.runTaskLater(plugin, 2);
	}
	
	@Override
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(arena.getMidSpawn());
	}

	@Override
	public void onPlayerLeave(Player player) {
		super.onPlayerLeave(player);
		if (players.size() == 0) {
			endGame();
			System.out.println("[Skywars] lobby stopped: " + arena.getWorld().getName());
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				player.setGameMode(GameMode.ADVENTURE);
				MinG.resetPlayer(player, plugin);
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		}, 5);
	}

}
