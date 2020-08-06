package me.groot_23.skywars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MinG;
import me.groot_23.ming.game.task.GameTaskDelayed;
import me.groot_23.ming.world.Arena;
import me.groot_23.skywars.game.tasks.SkyTasksDelayed;

public class SWstart implements CommandExecutor{

	public SWstart(JavaPlugin plugin) {
		plugin.getCommand("swstart").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
			return false;
		}
		if(!sender.hasPermission("skywars.start")) {
			sender.sendMessage("Du hast nicht die Berechtigung für diesen Befehl: skywars.start");
			return true;
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
