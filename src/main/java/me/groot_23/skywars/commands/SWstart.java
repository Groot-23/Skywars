package me.groot_23.skywars.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MinG;
import me.groot_23.ming.commands.CommandBase;
import me.groot_23.ming.game.task.GameTaskDelayed;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed;

public class SWstart extends CommandBase {

	public SWstart(JavaPlugin plugin) {
		super(plugin, "swstart", "skywars.start");
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean execute(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
			return false;
		}
		Player player = (Player) sender;
		Arena arena = MinG.getArena(player.getWorld().getUID());
		if(arena != null) {
			GameTaskDelayed task = arena.getGame().taskManager.getTask(SkyTasksDelayed.GoToSpawn.id);
			if(task != null) {
				task.runTaskEarly();
			}
		}
		return true;
	}

}
