package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.SkywarsKit;

public class KitEvents implements Listener {

	Main plugin;
	
	public KitEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	public void openGui(Player player) {
		Inventory inv = Bukkit.createInventory(player, 45, "Kit Selector");
		
		for(int y = 0; y <= 4; y += 4) {
			for(int x = 0; x < 9; x++) {
				inv.setItem(9*y + x, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
			}
		}
		for(int y = 1; y < 4; y++) {
			for(int x = 0; x <= 8; x += 8) {
				inv.setItem(9 * y + x, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
			}
		}
		
		ItemStack leaveItem = new ItemStack(Material.BARRIER);
		ItemMeta leaveItemMeta = leaveItem.getItemMeta();
		leaveItemMeta.setDisplayName("Kit Auswahl verlassen");
		leaveItem.setItemMeta(leaveItemMeta);
		inv.setItem(9*4 + 4, leaveItem);
		
		int i = 0;
		for(int y = 1; y < 4; y++) {
			for(int x = 1; x < 9; x++) {
				if(i < plugin.kits.size()) {
					ItemStack stack = plugin.kits.get(i).getDisplayItem();
					// set selected
					if(!player.getMetadata("skywarsKit").isEmpty()) {
						String selected = player.getMetadata("skywarsKit").get(0).asString();
						ItemMeta meta = stack.getItemMeta();
						if(meta.getDisplayName().equals(selected)) {
							meta.setDisplayName(meta.getDisplayName() + "    (ausgewählt)");
							stack.setItemMeta(meta);
						}
					}

					inv.setItem(9 * y + x, stack);
					i++;
				} else break;
			}
		}
		
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onClickEvent(InventoryClickEvent e) {
		if(e.getView().getTitle().equals("Kit Selector") && e.getCurrentItem() != null) {
			if(e.getCurrentItem().getType() == Material.BARRIER) {
				e.getWhoClicked().closeInventory();
			} else {
				String name = e.getCurrentItem().getItemMeta().getDisplayName();
				if(name.contains("    (ausgewählt)")) {
					name = name.substring(0, name.indexOf("    (ausgewählt)"));
				}
				SkywarsKit kit = plugin.kitByName.get(name);
				if(kit != null) {
					for(ItemStack item : e.getInventory().getContents()) {
						if(item != null) {
							ItemMeta meta = item.getItemMeta();
							String n = meta.getDisplayName();
							if(n.contains("    (ausgewählt)")) {
								meta.setDisplayName(n.substring(0, n.indexOf("    (ausgewählt)")));
								item.setItemMeta(meta);
								break; // Only one kit can be selected!
							}
						}
					}
					ItemMeta meta = e.getCurrentItem().getItemMeta();
					meta.setDisplayName(name + "    (ausgewählt)");
					e.getCurrentItem().setItemMeta(meta);
					e.getWhoClicked().setMetadata("skywarsKit", new FixedMetadataValue(plugin, kit.getName()));
					//kit.applyToPlayer((Player)e.getWhoClicked());
				}
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void clickToOpen(PlayerInteractEvent e) {
		if(e.getItem() != null) {
			if(e.getItem().getType() == Material.CHEST) {
				if(e.getItem().getItemMeta().getDisplayName().equals("Kit Selector")) {
					openGui(e.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void preventDrop(PlayerDropItemEvent e) {
		ItemStack stack = e.getItemDrop().getItemStack();
		if(stack.getType() == Material.CHEST && stack.getItemMeta().getDisplayName().equals("Kit Selector")) {
			e.setCancelled(true);
		}
	}
}
