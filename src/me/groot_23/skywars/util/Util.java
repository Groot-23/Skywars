package me.groot_23.skywars.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Util {

	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String getWorldPath(String world) {
		return Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world;
	}
	
	public static boolean worldExists(String name) {
		return new File(getWorldPath(name)).exists();
	}
	
	public static boolean copyWorld(String src, String dst) {
		if(worldExists(src) && !worldExists(dst)) {
			try {
				FileUtil.copyFileOrFolder(new File(getWorldPath(src)), new File(getWorldPath(dst)));
				// IMPORTANT: DELETE uid.dat or world will NOT be loaded!
				new File(getWorldPath(dst + File.separator + "uid.dat")).delete();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static void deleteWorld(String name) {
		if(worldExists(name)) {
			World w = Bukkit.getWorld(name);
			if(w != null) {
				for(Player p : w.getPlayers()) {
					p.teleport(Bukkit.getWorld("world").getSpawnLocation());
				}
				Bukkit.unloadWorld(w, false);
			}
			FileUtil.delete(new File(getWorldPath(name)));
		}
	}
	
	public static String repeat(int count, String with) {
	    return new String(new char[count]).replace("\0", with);
	}
	
	public static String minuteSeconds(int seconds) {
		return Integer.toString(seconds / 60) + ":" + Integer.toString(seconds % 60);
	}
	
	public static int secondsFromStr(String str) {
		int i = 0;
		for(char c : str.toCharArray()) {
			if(c == ':') break;
			i++;
		}
		int minutes = Integer.parseInt(str.substring(0, i));
		int seconds = Integer.parseInt(str.substring(i + 1));
		return 60 * minutes + seconds;
	}
}
