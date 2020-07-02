package me.groot_23.skywars.game.states;

import org.bukkit.World;

import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.GameState;
import me.groot_23.skywars.game.SkywarsData;
import me.groot_23.skywars.world.SkyArena;

public abstract class SkyGameState extends GameState<SkywarsData> {

	protected SkyArena arena;
	protected World world;
	
	public SkyGameState(GameState<SkywarsData> state) {
		super(state);
		this.arena = ((SkyGameState)state).arena;
		this.world = arena.getWorld();
	}
	
	public SkyGameState(SkywarsData data, Game game) {
		super(data, game);
		this.arena = (SkyArena)game.getArena();
		this.world = arena.getWorld();
	}

}
