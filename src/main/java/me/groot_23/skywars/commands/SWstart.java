package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.commands.PlayerCommand;
import me.groot_23.pixel.game.task.PixelTaskDelayed;
import me.groot_23.pixel.world.GameWorld;
import me.groot_23.pixel.world.LobbyWorld;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed;

public class SWstart extends PlayerCommand {

	public SWstart(JavaPlugin plugin) {
		super(plugin, "swstart", "skywars.start");
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean execute(CommandSender sender, Command arg1, String arg2, String[] args) {
		Player player = (Player) sender;
		LobbyWorld lobby = Pixel.getLobby(player.getWorld().getUID());
		if(lobby != null) {
			lobby.lobby.timer.runTaskEarly();
		}
		return true;
	}

}
