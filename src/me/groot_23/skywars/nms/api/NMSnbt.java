package me.groot_23.skywars.nms.api;

public interface NMSnbt {
	
	public void setString	(String key, String value);
	public void setInt		(String key, int value);
	public void setBool		(String key, boolean value);
	
	public String 	getString	(String key);
	public int 		getInt		(String key);
	public boolean 	getBool		(String key);
	
	public boolean contains(String key);
}
