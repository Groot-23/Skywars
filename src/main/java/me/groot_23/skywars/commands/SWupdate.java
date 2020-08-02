package me.groot_23.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import me.groot_23.ming.world.WorldMarker;
import me.groot_23.skywars.Main;

public class SWupdate implements CommandExecutor {

	public SWupdate(Main plugin) {
		plugin.getCommand("swupdate").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
//		if(!sender.hasPermission("skywars.update")) {
//			sender.sendMessage(Util.chat("&cDu hast nicht die Berechtigung, diesen Befehl auszuführen! Benötigte Berechtigung: skywars.update"));
//			return true;
//		}
//		Main.game.getArenaProvider().update();
//		sender.sendMessage(Util.chat("Änderungen erfolgreich übernommen!"));
		Player player = (Player) sender;
		
//		LanguageHolder lang = Main.game.getLangManager().getLanguageHolder(1);
//		Utf8Config cfg = lang.getConfig("en_us");
//		for(Material material : Material.values())
//		{
//			String name = material.name().toLowerCase();
//			cfg.set("material." + name, name.replace('_', ' '));
//		}
//		lang.saveConfig(cfg, "en_us");
		
		for(ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
			if(armorStand.getCustomName().equals("skywars_spawn")) {
				armorStand.setCustomName(WorldMarker.MARKER_PREFIX + "sky_spawn");
			}
		}
		
		return true;
	}

}
