package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.ming.player.PlayerUtil;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;

public class GameEvents implements Listener {

	private Main plugin;

	public GameEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

//	@EventHandler
//	public void onDeath(PlayerDeathEvent e) {
//		if (e.getEntity().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			Player killer = e.getEntity().getKiller();
//			if (killer != null) {
//				if (killer.getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//					SkywarsScoreboard.addKill(killer);
//				}
//			}
//			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
//				@Override
//				public void run() {
//					e.getEntity().setGameMode(GameMode.SPECTATOR);
//					e.getEntity().spigot().respawn();
//				}
//			}, 1);
//
//		}
//	}

	@EventHandler
	public void onWorldLeave(PlayerChangedWorldEvent e) {
		if (e.getFrom().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			Player p = e.getPlayer();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run() {
					p.setGameMode(GameMode.ADVENTURE);
					PlayerUtil.resetPlayer(p, plugin);
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
			}, 5);

		}
	}

	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent e) {
		if (e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			e.getBlock().setMetadata("skywars_player_placed", new FixedMetadataValue(plugin, true));
		}
	}

//	@EventHandler
//	public void onBlockDamage(BlockDamageEvent e) {
//		if (e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			if (!e.getBlock().hasMetadata("skywars_player_placed")) {
//				Arena arena = Main.game.getArenaById(e.getBlock().getWorld().getUID());
//				if (arena != null) {
//					if (arena.isInsideMidSpawn(e.getBlock().getLocation())) {
//						e.setCancelled(true);
//					}
//				}
//			}
//		}
//	}
//
//	@EventHandler
//	public void onBlockBreak(BlockBreakEvent e) {
//		if (e.getBlock().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			if (!e.getBlock().hasMetadata("skywars_player_placed")) {
//				Arena arena = Main.game.getArenaById(e.getBlock().getWorld().getUID());
//				if (arena != null) {
//					if (arena.isInsideMidSpawn(e.getBlock().getLocation())) {
//						e.setCancelled(true);
//					}
//				}
//			}
//		}
//	}

//	@EventHandler
//	public void onRespawn(PlayerRespawnEvent e) {
//		if (e.getPlayer().getWorld().getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
//			Arena arena = Main.game.getArenaById(e.getPlayer().getWorld().getUID());
//			if (arena != null) {
//				e.setRespawnLocation(arena.getMidSpawn());
//			} else {
//				e.setRespawnLocation(e.getPlayer().getWorld().getSpawnLocation());
//			}
//		}
//	}

	@EventHandler
	public void preventSpawn(CreatureSpawnEvent e) {
		World world = e.getEntity().getWorld();
		if (!world.getMetadata("skywars_edit_world").isEmpty()
				|| world.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			if (e.getSpawnReason() == SpawnReason.NATURAL) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void lapisOpen(InventoryOpenEvent e) {
		if (e.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) e.getInventory();
			inv.setSecondary(new ItemStack(Material.LAPIS_LAZULI, 64));
		}
	}

	@EventHandler
	public void lapisClose(InventoryCloseEvent e) {
		if (e.getInventory().getType() == InventoryType.ENCHANTING) {
			EnchantingInventory inv = (EnchantingInventory) e.getInventory();
			inv.setSecondary(null);
		}
	}

	@EventHandler
	public void lapisClick(InventoryClickEvent e) {
		if (e.getInventory().getType() == InventoryType.ENCHANTING) {
			if (e.getCurrentItem().getType() == Material.LAPIS_LAZULI) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onLeaveClick(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (new NBTItem(e.getItem()).hasKey("skywars_leave")) {
				e.getPlayer().performCommand("swleave");
			}
		}
	}


//	@EventHandler
//	public void onJoin(GameJoinEvent e) {
//		e.getPlayer().getInventory().clear();
//		Player player = e.getPlayer();
//		
//		// init hotbar
//		GuiItem kitSelector = Main.game.createGuiItem(Material.CHEST,
//				Util.chat(plugin.langManager.getTranslation(player, LanguageKeys.KIT_SELECTOR)));
//		kitSelector.addActionUseRunnable("openGui", UseAction.RIGHT_CLICK);
//		player.getInventory().setItem(4, kitSelector.getItem());
//
//		GuiItem lobbyLeave = Main.game.createGuiItem(Material.MAGMA_CREAM,
//				Util.chat(plugin.langManager.getTranslation(player, LanguageKeys.LEAVE)));
//		lobbyLeave.addActionUse("swleave", UseAction.RIGHT_CLICK, UseAction.LEFT_CLICK);
//		player.getInventory().setItem(8, lobbyLeave.getItem());
//		
//		GuiItem teamSelector = Main.game.createGuiItem(Material.OAK_SIGN);
//		teamSelector.addActionUseRunnable("open_kit_selector");
//		player.getInventory().setItem(0, teamSelector.getItem());
//		
//
//		Arena arena = ((ArenaGame)e.getGame()).getArena();
//		if(e.getPlayerCount() == arena.getMaxPlayers()) {
//			// end lobby task sooner
//			e.getGame().runTaskEarly(SkyGameTasks.GoToSpawn.id);
//		} else if(e.getPlayerCount() == arena.getMinPlayers()) {
//			// start lobby task
//			e.getGame().addTask(new SkyGameTasks.GoToSpawn(e.getGame(), 30 * 20), SkyGameTasks.GoToSpawn.id);
//		}
//		
//		String mode = e.getGame().mode.getName();
//		SkywarsScoreboard.resetKills(player);
//		SkywarsScoreboard.init(player);
//		SkywarsScoreboard.initPreGame(player, 2, 30, arena.getMapName(), mode);
//	}
}
