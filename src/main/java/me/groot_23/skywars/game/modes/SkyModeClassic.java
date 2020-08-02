package me.groot_23.skywars.game.modes;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.game.tasks.SkyTasksRepeated;

public class SkyModeClassic extends MiniGameMode {

	private int teamSize;
	private String name;

	public SkyModeClassic(MiniGame miniGame, int teamSize, String name) {
		super(miniGame);
		this.teamSize = teamSize;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}


//	@Override
//	public Arena createArena(World world, String map) {
//		return new SkyArena(this, world, map);
//	}

	@Override
	public int getPlayersPerTeam() {
		return teamSize;
	}

	@Override
	public Game createNewGame() {
		Game game = new SkyGame(this, "default");
		game.taskManager.addRepeated(new SkyTasksRepeated.Lobby1(game), "lobby1");
		game.taskManager.addRepeated(new SkyTasksRepeated.Lobby20(game), "lobby20");
		return game;
	}
}
