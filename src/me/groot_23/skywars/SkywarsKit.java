package me.groot_23.skywars;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.groot_23.skywars.util.Util;

public class SkywarsKit {
	
	private String displayName;
	private List<String> description;
	private List<String> lore;
	private Material displayMaterial;
	private List<ItemStack> startItems;
	private List<PotionEffect> startEffects;
	
	public String getName() {
		return displayName;
	}
	
	public SkywarsKit(String name, List<String> description, Material material, List<ItemStack> items, List<PotionEffect> effects) {
		displayName = name;
		this.description = description;
		displayMaterial = material;
		startItems = items;
		startEffects = effects;
		
		initLore();
	}
	
	public void applyToPlayer(Player player) {
		for(ItemStack item : startItems) {
			player.getInventory().addItem(item);
		}
		for(PotionEffect effect : startEffects) {
			player.addPotionEffect(effect);
		}
	}
	
	public ItemStack getDisplayItem() {
		ItemStack item = new ItemStack(displayMaterial);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private void initLore() {
		lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "Beschreibung:");
		for(String s : description) {
			lore.add(" - " + s);
		}
		lore.add(ChatColor.YELLOW + "Items:");
		for(ItemStack stack : startItems) {
			String itemString = stack.getType().toString().toLowerCase().replace("_", " ");
			if(stack.getAmount() > 1) {
				itemString += " x" + stack.getAmount();
			}
			if(stack.getItemMeta() instanceof PotionMeta) {
				itemString += " Effekt: " + ((PotionMeta)stack.getItemMeta()).getBasePotionData().getType().toString().toLowerCase();
			}
			lore.add(" - " + itemString);
		}
		lore.add(ChatColor.YELLOW + "Effekte: ");
		for(PotionEffect effect : startEffects) {
			String effectString = effect.getType().getName().toLowerCase();
			effectString += " x" + effect.getAmplifier();
			effectString += " " + Util.minuteSeconds(effect.getDuration() / 20);
			lore.add(" - " + effectString);
		}
	}
	
	private static ItemStack parseItemSection(ConfigurationSection section) {
		if(!section.contains("type")) {
			System.err.println("Item 'type' Eigenschaft fehlt in: " + section.getCurrentPath());
		}
		String type = section.getString("type");
		Material material = Material.matchMaterial(type);
		if(material == null) {
			System.err.println("Ungültiger 'type' für Item: '" + type + "' bei: " + section.getCurrentPath());
		}
		ItemStack stack = new ItemStack(material);
		if(section.contains("count")) {
			stack.setAmount(section.getInt("count"));
		}
		if(section.contains("potion")) {
			if(!(stack.getItemMeta() instanceof PotionMeta)) {
				System.err.println("Für das Item '" + type + "' kann kein Potion angegeben werden! Bei: " + section.getCurrentPath());
			} else {
				PotionMeta meta = (PotionMeta)stack.getItemMeta();
				meta.setBasePotionData(new PotionData(PotionType.valueOf(section.getString("potion").toUpperCase())));
				stack.setItemMeta(meta);
			}
		}
		return stack;
		
	}
	
	private static PotionEffect parsePotionEffect(ConfigurationSection section) {
		if(!section.contains("potion")) {
			System.err.println("Potion 'potion' Eigenschaft fehlt in: " + section.getCurrentPath());
		}
		PotionType ptype = PotionType.valueOf(section.getString("potion").toUpperCase());
		if(ptype == null) {
			System.err.println("Ungültiger 'type' für Potion: '" + section.getString("potion") + "' bei: " + section.getCurrentPath());
		}
		PotionEffectType type = ptype.getEffectType();
		if(!section.contains("duration")) {
			System.err.println("Potion 'duration' Eigenschaft fehlt in: " + section.getCurrentPath());
		}
		int duration = section.getInt("duration");
		if(!section.contains("amplifier")) {
			System.err.println("Potion 'amplifier' Eigenschaft fehlt in: " + section.getCurrentPath());
		}
		int amplifier = section.getInt("amplifier");
		return new PotionEffect(type, duration, amplifier);
	}
	
	public static List<SkywarsKit> loadKits() {
		List<SkywarsKit> kits = new ArrayList<SkywarsKit>();
		YamlConfiguration yaml = new YamlConfiguration();
		try {
			yaml.load(new File(Main.getInstance().getDataFolder().getPath() + File.separator + "kits.yml"));
			for(String kit : yaml.getKeys(false)) {
				ConfigurationSection section = yaml.getConfigurationSection(kit);
				
				String displayName = section.getString("name");
				List<String> description = section.getStringList("description");
				Material material = Material.matchMaterial(section.getString("material"));
				
				List<ItemStack> startItems = new ArrayList<ItemStack>();
				ConfigurationSection itemSec = section.getConfigurationSection("startItems");
				if(itemSec != null) {
					for(String key : itemSec.getKeys(false)) {
						startItems.add(parseItemSection(itemSec.getConfigurationSection(key)));
					}
				}
				
				List<PotionEffect> startEffects = new ArrayList<PotionEffect>();
				ConfigurationSection effectSec = section.getConfigurationSection("startEffects");
				if(effectSec != null) {
					for(String key : effectSec.getKeys(false)) {
						startEffects.add(parsePotionEffect(effectSec.getConfigurationSection(key)));
					}
				}
				
				kits.add(new SkywarsKit(displayName, description, material, startItems, startEffects));
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("[Skywars] Kits wurden nicht gefunden! (plugins/skywars/kits.yml)");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("[Skywars] Fehler beim Laden der Kits!");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			System.err.println("[Skywars] Fehler beim Laden der Kits!");
			e.printStackTrace();
		}
		return kits;
	}
}
