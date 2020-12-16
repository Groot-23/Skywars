package me.groot_23.skywars.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.gui.GuiItem.UseAction;
import me.groot_23.pixel.kits.KitApi;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.player.PlayerUtil;
import me.groot_23.pixel.player.team.GameTeam;
import me.groot_23.pixel.player.team.TeamHandler;
import me.groot_23.pixel.world.Arena;
import me.groot_23.pixel.world.ArenaCreator;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed;
import me.groot_23.skywars.game.tasks.SkyTasksRepeated;
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

	public SkyGame(String name, String option, int teamSize) {
		super(name, option, Main.getInstance(), teamSize);
		

		arena = Pixel.WorldProvider.provideArena(option, this, new ArenaCreator() {
			@Override
			public Arena createArena(Game game, World world, String map) {
				return new SkyArena(SkyGame.this, world, map);
			}
		});
		skyArena = (SkyArena) arena;
		teamHandler = new TeamHandler(arena.getMaxPlayers(), teamSize);
		
		skyArena.initBorder();
		skyArena.refillChests();
		arena.getWorld().setPVP(false);

		deathMatchBegin = plugin.getConfig().getInt("deathMatchBegin");
		deathMatchBorderShrinkTime = plugin.getConfig().getInt("deathMatchBorderShrinkTime");
		refillTime = plugin.getConfig().getInt("refillTime");
		refillTimeChange = plugin.getConfig().getInt("refillTimeChange");
		
		taskManager.addRepeated(new SkyTasksRepeated.Lobby1(this), "lobby1");
		taskManager.addRepeated(new SkyTasksRepeated.Lobby20(this), "lobby20");
	}

	@Override
	public void onJoin(Player player) {
		for(Player p : players) {
			p.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(p, PixelLangKeys.JOIN), player.getName()));
		}
		
		PlayerUtil.resetPlayer(player);
		// ====== init hotbar ==========
		// kit selector
		GuiItem kitSelector = new GuiItem(Material.CHEST,
				Util.chat(LanguageApi.getTranslation(player, PixelLangKeys.KIT_SELECTOR)));
		kitSelector.addUseRunnable(new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv) {
				KitApi.openGui(player, "skywars");
			}
		}, UseAction.RIGHT_CLICK);
		player.getInventory().setItem(3, kitSelector.getItem());

		// kit shop
		GuiItem kitShop = new GuiItem(Material.DIAMOND, ChatColor.GOLD + "" + ChatColor.BOLD + "Kit Shop");
		kitShop.addUseRunnable(new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv) {
				KitApi.openShop(player, "skywars");
			}
		}, UseAction.RIGHT_CLICK);
		player.getInventory().setItem(5, kitShop.getItem());
		
		// leave
		GuiItem lobbyLeave = new GuiItem(Material.MAGMA_CREAM,
				Util.chat(LanguageApi.getTranslation(player, LanguageKeys.LEAVE)));
		lobbyLeave.addUseCommand("swleave", UseAction.RIGHT_CLICK, UseAction.LEFT_CLICK);
		player.getInventory().setItem(8, lobbyLeave.getItem());

		// team selector
		GuiItem teamSelector = new GuiItem(Material.OAK_SIGN);
		teamSelector.addUseRunnable(new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv) {
				Arena arena = Pixel.getArena(player.getWorld().getUID());
				if (arena != null) {
					if (arena.getGame() instanceof SkyGame) {
						player.openInventory(((SkyGame) arena.getGame()).teamHandler.getTeamSelectorInv());
					}
				}
			}
		});
		
		player.getInventory().setItem(0, teamSelector.getItem());
		
		SkywarsScoreboard.resetKills(player);
		SkywarsScoreboard.init(player);
		SkywarsScoreboard.initPreGame(player, arena.getMaxPlayers(), 30, arena.getMapName(), name);

		
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
		event.setDeathMessage(Main.chatPrefix + String.format(LanguageApi.getDefault(PixelLangKeys.DEATH), event.getEntity().getName()));
		Player killer = event.getEntity().getKiller();
		if(killer == null) {
			killer = PlayerUtil.getLastAttacker(event.getEntity());
		}
		if (killer != null) {
			SkywarsScoreboard.addKill(killer);
			String msg = "Herzen von " + killer.getName() + ": ";
			for(int i = 0; i < 10; ++i) {
				if(killer.getHealth() - 2*i > 1) msg += ChatColor.RED + "\u2764";
				else if(killer.getHealth() - 2*i > 0) msg += ChatColor.RED + "\u2765";
				else msg += ChatColor.WHITE +  "\u2764";
			}
			event.getEntity().sendMessage(Main.chatPrefix + msg);
		} 
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
					taskManager.addTask(new SkyTasksDelayed.Victory(SkyGame.this, 0, winner), SkyTasksDelayed.Victory.id);
				} else if(teamHandler.getTeamsAliveCount() == 0) {
					if(taskManager.getTask(SkyTasksDelayed.Draw.id) != null) {
						taskManager.getTask(SkyTasksDelayed.Draw.id).runTaskEarly();
					}
				}
			}
		}.runTaskLater(plugin, 5);
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
		super.onPlayerLeave(player);
		for(Player p : players) {
			p.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(p, PixelLangKeys.LEAVE), player.getName()));
		}
		player.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(player, PixelLangKeys.LEAVE), player.getName()));
		
		if (players.size() == 0) {
			endGame();
			System.out.println("[Skywars] lobby stopped: " + arena.getWorld().getName());
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				player.setGameMode(GameMode.ADVENTURE);
				PlayerUtil.resetPlayer(player);
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		}, 5);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
	}

}
