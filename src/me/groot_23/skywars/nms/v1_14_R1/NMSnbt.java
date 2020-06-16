package me.groot_23.skywars.nms.v1_14_R1;

import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class NMSnbt implements me.groot_23.skywars.nms.api.NMSnbt {
	
	private NBTTagCompound tag;
	
	public NMSnbt(NBTTagCompound tag) {
		this.tag = tag;
	}
	
	public NBTTagCompound getTag() {
		return tag;
	}
	
	@Override
	public void setString(String key, String value) {
		tag.setString(key, value);
	}
	@Override
	public void setInt(String key, int value) {
		tag.setInt(key, value);
	}
	@Override
	public void setBool(String key, boolean value) {
		tag.setBoolean(key, value);
	}
	
	
	@Override
	public String getString(String key) {
		return tag.getString(key);
	}
	@Override
	public int getInt(String key) {
		return tag.getInt(key);
	}
	@Override
	public boolean getBool(String key) {
		return tag.getBoolean(key);
	}
	
	
	@Override
	public boolean contains(String key) {
		return tag.hasKey(key);
	}
}
