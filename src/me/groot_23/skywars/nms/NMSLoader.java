package me.groot_23.skywars.nms;

import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.skywars.nms.api.NMS;

public class NMSLoader {

	public static NMS loadNMS(JavaPlugin plugin) {
		String packageName = plugin.getServer().getClass().getPackage().getName();
		String version = packageName.substring(packageName.lastIndexOf('.') + 1);
		try {
			Class<?> clazz = Class.forName("me.groot_23.skywars.nms." + version + ".NMSHandler");
			if(NMS.class.isAssignableFrom(clazz)) {
				return (NMS) clazz.getConstructor().newInstance();
			}
		} catch(Exception e) {
			 e.printStackTrace();
	         plugin.getLogger().severe("Could not find support for this CraftBukkit version: " + version);
		}
		return null;
	}
}
