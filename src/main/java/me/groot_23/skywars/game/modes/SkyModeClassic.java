package me.groot_23.skywars.game.modes;

import org.bukkit.World;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.GameState;
import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.game.states.GameStateLobby;
import me.groot_23.skywars.world.SkyArena;

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

	@Override
	public GameState<?> getStartingState(Game game) {
		SkywarsData data = new SkywarsData();
		data.deathMatchBegin = plugin.getConfig().getInt("deathMatchBegin");
		data.deathMatchBorderShrinkTime = plugin.getConfig().getInt("deathMatchBorderShrinkTime");
		data.refillTime = plugin.getConfig().getInt("refillTime");
		data.refillTimeChange = plugin.getConfig().getInt("refillTimeChange");

		return new GameStateLobby(data, game);
	}

	@Override
	public Arena createArena(World world, String map) {
		return new SkyArena(this, world, map);
	}

	@Override
	public int getPlayersPerTeam() {
		return teamSize;
	}
}
