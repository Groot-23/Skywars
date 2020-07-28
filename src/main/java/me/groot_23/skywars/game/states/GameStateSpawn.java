package me.groot_23.skywars.game.states;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.groot_23.ming.game.GameState;
import me.groot_23.ming.player.GameTeam;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.util.Util;

public class GameStateSpawn extends SkyGameState{

	public GameStateSpawn(SkyGameState state) {
		super(state);
	}
	
	int counter = 10;
	
	@Override
	public void onStart() {
		world = arena.getWorld();
		
		
		if (arena.getSpawns().size() < world.getPlayers().size()) {
			Bukkit.getServer()
					.broadcastMessage(Util.chat("&cZu wenige Spawns! Fehler beim Starten von Skywars :("));
			return;
		}
		
		game.fillTeams(world.getPlayers());
		
		int i = 0;
		for(GameTeam team : game.getTeams()) {
			for(Player p : team.getPlayers()) {
				p.teleport(arena.getSpawns().get(i));
				SkywarsScoreboard.initGame(p, game.getTeamsAliveCount(),
						LanguageKeys.EVENT_START, counter, data.deathMatchBegin, SWconstants.LENGTH_OF_GAME,
						arena.getMapName(), game.getMode().getName());
			}
			i++;
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
				p.sendMessage(miniGame.getTranslation(p, LanguageKeys.START_IN) +  " " + ChatColor.RED + counter);
			}
		}
		if (counter <= 5) {
			for (Player p : world.getPlayers()) {
				if (counter != 0) {
					p.sendMessage(miniGame.getTranslation(p, LanguageKeys.START_IN) +  " " + ChatColor.RED + counter);
					p.sendTitle(ChatColor.GREEN + "" + counter, ChatColor.LIGHT_PURPLE + 
							miniGame.getTranslation(p, LanguageKeys.GET_READY), 3, 14, 3);
				} else {
					return new GameStatePlaying(this);
				}

			}
		}
		counter--;
		return this;
	}

}
