package me.groot_23.skywars.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
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

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.SkywarsKit;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.util.Util;

public class KitEvents implements Listener {

	Main plugin;

	public static final String SELECTOR = "skywars_kit_selector";

	public KitEvents(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	public static String getSelectedSuffix(Player player) {
		return ChatColor.RESET + "    " + ChatColor.GRAY + "(" + ChatColor.GREEN
				+ Main.game.getTranslation(player, LanguageKeys.KIT_SELECTED) + ChatColor.GRAY + ")";
	}

	public static void openGui(Player player) {
		Inventory inv = Bukkit.createInventory(player, 45,
				Util.chat(Main.game.getTranslation(player, LanguageKeys.KIT_SELECTOR)));

		for (int y = 0; y <= 4; y += 4) {
			for (int x = 0; x < 9; x++) {
				inv.setItem(9 * y + x, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
			}
		}
		for (int y = 1; y < 4; y++) {
			for (int x = 0; x <= 8; x += 8) {
				inv.setItem(9 * y + x, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
			}
		}

		ItemStack leaveItem = new ItemStack(Material.BARRIER);
		ItemMeta leaveItemMeta = leaveItem.getItemMeta();
		leaveItemMeta.setDisplayName("Kit Auswahl verlassen");
		leaveItem.setItemMeta(leaveItemMeta);
		inv.setItem(9 * 4 + 4, leaveItem);

		int i = 0;
		for (int y = 1; y < 4; y++) {
			for (int x = 1; x < 9; x++) {
				
				if(i < Main.game.getKits().size()) {
					inv.setItem(9 * y + x, Main.game.getKits().get(i).getDisplayItem(player));
					i++;
				
//				if (i < Main.kits.size()) {
//					ItemStack stack = Main.kits.get(i).getDisplayItem(player);
//					// set selected
//					if (player.hasMetadata("skywarsKit")) {
//						String selected = player.getMetadata("skywarsKit").get(0).asString();
//						NBTItem nbt = new NBTItem(stack);
//						if (nbt.hasKey("skywars_kit")) {
//							String kitName = nbt.getString("skywars_kit");
//							if (kitName.equals(selected)) {
//								ItemMeta meta = stack.getItemMeta();
//								meta.setDisplayName(meta.getDisplayName() + getSelectedSuffix(player));
//								stack.setItemMeta(meta);
//							}
//						}
//					}
//
//					inv.setItem(9 * y + x, stack);
//					i++;
				} else
					break;
			}
		}

		player.openInventory(inv);
	}

//	@EventHandler
//	public void onClickEvent(InventoryClickEvent e) {
//		Player player = (Player) e.getWhoClicked();
//		if (e.getView().getTitle()
//				.equals(Util.chat(plugin.langManager.getTranslation(player, LanguageKeys.KIT_SELECTOR)))
//				&& e.getCurrentItem() != null) {
//			if (e.getCurrentItem().getType() == Material.BARRIER) {
//				e.getWhoClicked().closeInventory();
//			} else {
//				NBTItem nbt = new NBTItem(e.getCurrentItem());
//				if (nbt.hasKey("skywars_kit")) {
//					String kitName = nbt.getString("skywars_kit");
//					SkywarsKit kit = plugin.kitByName.get(kitName);
//					if (kit != null) {
//						for (ItemStack item : e.getInventory().getContents()) {
//							if (item != null) {
//								ItemMeta meta = item.getItemMeta();
//								String n = meta.getDisplayName();
//								if (n.contains(getSelectedSuffix(player))) {
//									meta.setDisplayName(
//											n.substring(0, n.indexOf(getSelectedSuffix(player))));
//									item.setItemMeta(meta);
//									break; // Only one kit can be selected!
//								}
//							}
//						}
//						ItemMeta meta = e.getCurrentItem().getItemMeta();
//						if (!meta.getDisplayName().contains(getSelectedSuffix(player))) {
//							meta.setDisplayName(meta.getDisplayName() + getSelectedSuffix(player));
//							e.getCurrentItem().setItemMeta(meta);
//						}
//						e.getWhoClicked().setMetadata("skywarsKit", new FixedMetadataValue(plugin, kit.getName()));
//						// kit.applyToPlayer((Player)e.getWhoClicked());
//					}
//				}
//			}
//			e.setCancelled(true);
//		}
//	}

//	@EventHandler
//	public void clickToOpen(PlayerInteractEvent e) {
//		if (e.getItem() != null) {
//			if (new NBTItem(e.getItem()).hasKey(SELECTOR)) {
//				openGui(e.getPlayer());
//			}
//		}
//	}
//
//	@EventHandler
//	public void preventDrop(PlayerDropItemEvent e) {
//		ItemStack stack = e.getItemDrop().getItemStack();
//		if (new NBTItem(stack).hasKey(SELECTOR)) {
//			e.setCancelled(true);
//		}
//	}
}
