package me.groot_23.skywars.language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;

import me.groot_23.skywars.util.Utf8Config;

public class LanguageManager {
	
	private Map<String, Map<String, String>> langValues;
	
	private String defaultLanguage;
	
	public LanguageManager(String defaultLanguage) {
		langValues = new HashMap<String, Map<String,String>>();
		this.defaultLanguage = defaultLanguage;
	}
	
	public void loadLanguages(File langFolder) {
		if(!langFolder.isDirectory()) {
			throw new IllegalArgumentException("The given file is not a directory!");
		}
		for(File f : langFolder.listFiles()) {
			Utf8Config config = new Utf8Config();
			try {
				config.load(f);
				loadLang(FilenameUtils.removeExtension(f.getName()), config);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void loadLang(String lang, Utf8Config cfg) {
		Map<String, String> values = new HashMap<String, String>();
		for(String key : cfg.getKeys(true)) {
			if(!cfg.isConfigurationSection(key)) {
				String val = cfg.getString(key);
				if(val != null) {
					values.put(key, val);
				}
			}
		}
		langValues.put(lang, values);
	}
	
	public String getTranslation(String language, String key) {
		String translated = null;
		Map<String, String> values = langValues.get(language);
		// check if languages is registered
		if(values != null) {
			translated = values.get(key);
		}
		// fallback to default language
		if(translated == null) {
			values = langValues.get(defaultLanguage);
			if(values != null) {
				translated = values.get(key);
			}
		}
		// use key if no value could be found
		if(translated == null) {
			translated = key;
		}
		return translated;
	}
	
	public String getDefault(String key) {
		String translated = null;
		Map<String, String> values = langValues.get(defaultLanguage);
		if(values != null) {
			translated = values.get(key);
		}
		// use key if no value could be found
		if(translated == null) {
			translated = key;
		}
		return translated;
	}
	
	public String getTranslation(Player player, String key) {
		return getTranslation(player.getLocale(), key);
	}
}
