package me.groot_23.skywars.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import me.groot_23.ming.MinG;
import me.groot_23.ming.commands.CommandBase;
import me.groot_23.ming.world.ChunkGeneratorVoid;
import me.groot_23.ming.world.WorldUtil;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.Util;

public class SWedit extends CommandBase {

	private Main plugin;
	
	public SWedit(Main plugin) {
		super(plugin, "swedit", "skywars.edit");
		this.plugin = plugin;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		List<String> list = new ArrayList<String>();
		if(args.length == 1) {
			for(File f : Bukkit.getWorldContainer().listFiles()) {
				if(!f.getName().startsWith(MinG.WorldProvider.WORLD_PREFIX) && f.getName().startsWith(args[0]))
					list.add(f.getName());
			}
		}
		return list;
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Nur Spieler können diesen Befehl ausführen");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 1) {
			World world = Bukkit.getWorld(args[0]);
			if(world == null) {
				if(!WorldUtil.worldExists(args[0])) {
					player.sendMessage(Util.chat("&cDie Welt \"" + args[0] + "\" wurde nicht gefunden"));
					return true;
				}
				world = Bukkit.createWorld(new WorldCreator(args[0]).generator(new ChunkGeneratorVoid()));
				if(world == null) {
					player.sendMessage(Util.chat("&cBeim Laden der Welt ist ein Fehler aufgetreten"));
					return true;
				}
			}
			world.setAutoSave(true);
			world.setMetadata("skywars_edit_world", new FixedMetadataValue(plugin, true));
			player.teleport(world.getSpawnLocation());
			// The change gamemode should not be done to early
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
				@Override
				public void run(){
					player.setGameMode(GameMode.CREATIVE);
				}
			}, 3L);
			return true;
		}
		return false;
	}

}
