package me.groot_23.skywars.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.groot_23.pixel.display.BossBarApi;
import me.groot_23.pixel.game.Lobby;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiItem.UseAction;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.kits.KitApi;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.player.PlayerUtil;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.Util;
import net.md_5.bungee.api.ChatColor;

public class SkyLobby extends Lobby{

	private BossBar bb;
	
	public SkyLobby(String game, String map, int minPlayers, int maxPlayers, int teamSize, int time) {
		super(Main.getInstance(), game, map, minPlayers, maxPlayers, teamSize, time);
		bb = Bukkit.createBossBar(LanguageApi.getDefault(LanguageKeys.BOSSBAR_START), BarColor.BLUE, BarStyle.SOLID);
		bb.setProgress(1);
	}
	
	@Override
	public void createGame() {
		for(Player player : players) BossBarApi.removePlayer(player);
		teamHandler.fillTeams(players);
		
		new SkyGame(plugin, game, world, players, teamHandler);
	}
	
	@Override
	public void onLeave(Player player) {
		for(Player p : players) {
			p.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(p, PixelLangKeys.LEAVE), player.getName()));
		}
		bb.removePlayer(player);
		super.onLeave(player);
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
				Util.chat(LanguageApi.getTranslation(player, PixelLangKeys.OPEN_KIT_SELECTOR)));
		kitSelector.addUseRunnable(new GuiRunnable() {
			@Override
			public void run(Player player, ItemStack item, Inventory inv) {
				KitApi.openGui(player, "skywars");
			}
		}, UseAction.RIGHT_CLICK);
		player.getInventory().setItem(3, kitSelector.getItem());

		// kit shop
		GuiItem kitShop = new GuiItem(Material.DIAMOND, LanguageApi.getTranslation(player, PixelLangKeys.OPEN_KIT_SHOP));
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
		if(teamHandler.teamSize > 1) {
			GuiItem teamSelector = new GuiItem(Material.NAME_TAG, LanguageApi.getTranslation(player, PixelLangKeys.OPEN_TEAM_SELECTOR));
			teamSelector.addUseRunnable(new GuiRunnable() {
				@Override
				public void run(Player player, ItemStack item, Inventory inv) {
					player.openInventory(teamHandler.getTeamSelectorInv());
				}
			});
			player.getInventory().setItem(0, teamSelector.getItem());			
		}
		
		SkywarsScoreboard.resetKills(player);
		SkywarsScoreboard.init(player);
		SkywarsScoreboard.initLobby(player, maxPlayers, time, map, game);
	}
	
	
	@Override
	public void onSecond() {
		int remaining = time;
		if(timer.isActive()) remaining = timer.getRemainingSeconds();
		SkywarsScoreboard.updatePreGame(players, maxPlayers, remaining);
		for (Player player : players) {
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setSaturation(5);
			if(remaining % 10 == 0 && remaining > 0 && timer.isActive()) {
				player.sendMessage(Main.chatPrefix + String.format(LanguageApi.getTranslation(player, LanguageKeys.START_IN), remaining));
			}
		}
	}
	
	@Override
	public void onTick() {
		if(timer.isActive()) {
			bb.setProgress(timer.getRemainingProgress());
			bb.setTitle(LanguageApi.getDefault(LanguageKeys.BOSSBAR_START) + " " + ChatColor.YELLOW + ChatColor.BOLD
					+ timer.getRemainingSeconds() + " " + LanguageApi.getDefault(PixelLangKeys.SECONDS).toUpperCase());
			for (Player player : players) {
				BossBarApi.addPlayer(bb, player);
			}
		}
	}
	
}
