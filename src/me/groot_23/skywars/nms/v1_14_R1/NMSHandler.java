package me.groot_23.skywars.nms.v1_14_R1;

import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import me.groot_23.skywars.nms.api.NMSnbt;
import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class NMSHandler implements me.groot_23.skywars.nms.api.NMS {

	@Override
	public NMSnbt getNBT(ItemStack item) {
		net.minecraft.server.v1_14_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
		return new me.groot_23.skywars.nms.v1_14_R1.NMSnbt(nbt);
	}

	@Override
	public void setNBT(ItemStack item, NMSnbt nbt) {
		net.minecraft.server.v1_14_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
		nmsCopy.setTag(((me.groot_23.skywars.nms.v1_14_R1.NMSnbt)nbt).getTag());
		item.setItemMeta(CraftItemStack.asBukkitCopy(nmsCopy).getItemMeta());
	}

}
