package me.groot_23.skywars.game.states;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.ming.game.GameState;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.SkywarsKit;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.SkyArena;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GameStatePlaying extends GameState<SkywarsData>{

	public GameStatePlaying(GameState<SkywarsData> state) {
		super(state);
	}
	
	int counter;
	int refillCounter;
	int dynamicRefillTime;
	int deathMatchCounter;
	SkyArena arena;
	World world;
	BossBar bossbar;
	
	@Override
	protected void onStart() {
		counter = SWconstants.LENGTH_OF_GAME;
		refillCounter = data.refillTime;
		dynamicRefillTime = refillCounter;
		deathMatchCounter = data.deathMatchBegin;
		
		arena = data.arena;
		arena.removeGlassSpawns();
		world = data.arena.getWorld();
		bossbar = Bukkit.createBossBar(
				Util.chat(Main.game.getDefualtTranslation(LanguageKeys.BOSSBAR_TITLE)),
				BarColor.PURPLE, BarStyle.SEGMENTED_20);
		bossbar.setProgress(1);
		arena.getWorld().setPVP(true);
		for (Player player : world.getPlayers()) {
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			bossbar.addPlayer(player);
			
			Main.game.applyKitToPlayer(player);
			
		}
	}

	@Override
	protected GameState<SkywarsData> onUpdate() {
		int playersLeft = 0;
		Player potentialWinner = null;
		for (Player p : world.getPlayers()) {
			if (p.getGameMode() == GameMode.SURVIVAL) {
				playersLeft++;
				potentialWinner = p;
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + 
								Main.game.getTranslation(p, LanguageKeys.NO_TEAMING)));
			}
		}
		SkywarsScoreboard.updateGame(world, playersLeft, LanguageKeys.EVENT_REFILL, refillCounter,
				deathMatchCounter, counter);
		if (playersLeft == 1) {
			bossbar.removeAll();
			return new GameStateVictory(this, potentialWinner);
		}
		arena.updateChestTimer(refillCounter);
		if (refillCounter <= 0) {
			dynamicRefillTime += data.refillTimeChange;
			refillCounter = dynamicRefillTime;
			arena.refillChests();
		}
		if (deathMatchCounter == 1) {
			arena.shrinkBorder(data.deathMatchBorderShrinkTime);
			for (Player p : world.getPlayers()) {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Death Match!", ChatColor.RED + ""
						+ ChatColor.BOLD + Main.game.getTranslation(p, LanguageKeys.GO_TO_MID), 3, 100, 3);
			}
		} else {
			bossbar.setProgress((double) deathMatchCounter / data.deathMatchBegin);
		}
//		if(counter <= 0) {
//			draw();
//			return null;
//		}
		counter--;
		refillCounter--;
		if (deathMatchCounter > 0) {
			deathMatchCounter--;
		}
		return this;
	}

}
