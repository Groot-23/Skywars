package me.groot_23.skywars.nms.api;

import org.bukkit.inventory.ItemStack;

public interface NMS {
	
	public NMSnbt getNBT(ItemStack item);
	public void setNBT(ItemStack item, NMSnbt nbt);
}
