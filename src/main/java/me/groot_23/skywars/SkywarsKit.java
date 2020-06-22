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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.groot_23.ming.config.ItemSerializer;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.language.LanguageManager;
import me.groot_23.skywars.util.Util;

public class SkywarsKit {
	
	private String name;
	private Material displayMaterial;
	private List<ItemStack> startItems;
	private List<PotionEffect> startEffects;
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName(Player player) {
		LanguageManager lang = Main.getInstance().langManager;
		return Util.chat(lang.getTranslation(player, "kits." + name + ".name"));
	}
	
	public SkywarsKit(String name, Material material, List<ItemStack> items, List<PotionEffect> effects) {
		this.name = name;
		displayMaterial = material;
		startItems = items;
		startEffects = effects;
	}
	
	public void applyToPlayer(Player player) {
		for(ItemStack item : startItems) {
			player.getInventory().addItem(item);
		}
		for(PotionEffect effect : startEffects) {
			player.addPotionEffect(effect);
		}
	}
	
	public ItemStack getDisplayItem(Player player) {
		LanguageManager lang = Main.getInstance().langManager;
		ItemStack item = new ItemStack(displayMaterial);
		NBTItem nbt = new NBTItem(item);
		nbt.setString("skywars_kit", name);
		item = nbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + Util.chat(lang.getTranslation(player,"kits." + name + ".name")));
		meta.setLore(getLore(player));
		item.setItemMeta(meta);
		return item;
	}
	
	public List<String> getLore(Player player) {
		LanguageManager lang = Main.getInstance().langManager;
		List<String> lore = new ArrayList<String>();
		String description = lang.getTranslation(player, "kits." + name + ".description");
		String[] descriptions = description.split("\\n");
		lore.add(ChatColor.YELLOW + lang.getTranslation(player, LanguageKeys.KIT_DESCRIPTION) + ":");
		for(String s : descriptions) {
			lore.add(ChatColor.RESET + " - " + Util.chat(s));
		}
		lore.add(ChatColor.YELLOW + lang.getTranslation(player, LanguageKeys.KIT_ITEMS) + ":");
		for(ItemStack stack : startItems) {
			stack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
			lore.add(ChatColor.RESET + " - " + ItemSerializer.asString(stack));
			
			Main.getInstance().getConfig().set("test_item_stack", stack);
			System.out.println(Main.getInstance().getConfig().getItemStack("test_item_stack").toString());
			Main.getInstance().saveConfig();
			
//			String itemString = stack.getType().toString().toLowerCase().replace("_", " ");
//			if(stack.getAmount() > 1) {
//				itemString += " x" + stack.getAmount();
//			}
//			if(stack.getItemMeta() instanceof PotionMeta) {
//				itemString += " Effekt: " + ((PotionMeta)stack.getItemMeta()).getBasePotionData().getType().toString().toLowerCase();
//			}
//			lore.add(ChatColor.RESET + " - " + itemString);
		}
		lore.add(ChatColor.YELLOW + lang.getTranslation(player, LanguageKeys.KIT_EFFECTS) + ":");
		for(PotionEffect effect : startEffects) {
			String effectString = effect.getType().getName().toLowerCase();
			effectString += " x" + effect.getAmplifier();
			effectString += " " + Util.minuteSeconds(effect.getDuration() / 20);
			lore.add(ChatColor.RESET + " - " + effectString);
		}
		return lore;
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
				System.err.println("Für Item'" + type + "' kann kein Potion angegeben werden! Bei: " + section.getCurrentPath());
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
				
//				String displayName = ChatColor.RESET + Util.chat(section.getString("name"));
//				List<String> description = section.getStringList("description");
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
				
				kits.add(new SkywarsKit(kit, material, startItems, startEffects));
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
