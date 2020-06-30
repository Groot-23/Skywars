package me.groot_23.skywars.game.states;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.groot_23.ming.game.GameState;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;
import me.groot_23.skywars.world.SkyArena;

public class GameStateSpawn extends GameState<SkywarsData>{

	public GameStateSpawn(GameState<SkywarsData> state) {
		super(state);
	}
	
	int counter = 10;
	SkyArena arena;
	World world;
	
	@Override
	public void onStart() {
		arena = data.arena;
		world = data.arena.getWorld();
		
		
		if (arena.getSpawns().size() < world.getPlayers().size()) {
			Bukkit.getServer()
					.broadcastMessage(Util.chat("&cZu wenige Spawns! Fehler beim Starten von Skywars :("));
			return;
		}
		for (int i = 0; i < world.getPlayers().size(); i++) {
			world.getPlayers().get(i).teleport(arena.getSpawns().get(i));
			SkywarsScoreboard.initGame(world.getPlayers().get(i), arena.getWorld().getPlayers().size(),
					LanguageKeys.EVENT_START, counter, data.deathMatchBegin, SWconstants.LENGTH_OF_GAME,
					arena.getMapName());
		}
		arena.removeLobby();
		arena.disableJoin();
	}

	@Override
	protected GameState<SkywarsData> onUpdate() {
		SkywarsScoreboard.updateGame(world, world.getPlayers().size(), LanguageKeys.EVENT_START, counter,
				data.deathMatchBegin, SWconstants.LENGTH_OF_GAME);
		if (counter % 10 == 0) {
			for (Player p : world.getPlayers()) {
				p.sendMessage(game.getTranslation(p, LanguageKeys.START_IN) +  " " + ChatColor.RED + counter);
			}
		}
		if (counter <= 5) {
			for (Player p : world.getPlayers()) {
				if (counter != 0) {
					p.sendMessage(game.getTranslation(p, LanguageKeys.START_IN) +  " " + ChatColor.RED + counter);
					p.sendTitle(ChatColor.GREEN + "" + counter, ChatColor.LIGHT_PURPLE + 
							game.getTranslation(p, LanguageKeys.GET_READY), 3, 14, 3);
				} else {
					String started = ChatColor.GREEN + game.getTranslation(p, LanguageKeys.STARTED);
					p.sendMessage(started);
					p.sendTitle(started, ChatColor.LIGHT_PURPLE + game.getTranslation(p, 
							LanguageKeys.FIGHT_BEGINS), 3, 20, 3);
					// remove falldamage
					p.setFallDistance(-1000);
					for(Location l : arena.getSpawns()) {
						arena.removeArea(l, 3, 3);
					}
					return new GameStatePlaying(this);
				}

			}
		}
		counter--;
		return this;
	}

}
