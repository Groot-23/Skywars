package me.groot_23.skywars.game.states;

import org.bukkit.entity.Player;

import me.groot_23.ming.game.GameState;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;

public class GameStateLobby extends GameState<SkywarsData>{

	public GameStateLobby(GameState<SkywarsData> state) {
		super(state);
	}
	
	public GameStateLobby(SkywarsData data) {
		super(data);
	}

	private int counter = 30;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected GameState<SkywarsData> onUpdate() {
		SkywarsScoreboard.updatePreGame(data.arena.getWorld(), data.arena.getMaxPlayers(), counter);
		int numPlayers = data.arena.getWorld().getPlayers().size();
		for (Player player : data.arena.getWorld().getPlayers()) {
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setSaturation(5);
		}
		if (numPlayers == data.arena.getMaxPlayers() || counter <= 0) {
			return new GameStateSpawn(this);
		}
		if (numPlayers < data.arena.getMinPlayers()) {
			counter = 30;
		} else {
			counter--;
		}
		return this;
	}

}
