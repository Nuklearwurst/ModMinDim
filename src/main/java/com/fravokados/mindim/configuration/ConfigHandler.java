package com.fravokados.mindim.configuration;

import com.fravokados.mindim.lib.Reference;
import com.fravokados.mindim.lib.Strings.Keys;
import com.fravokados.mindim.util.LogHelper;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigHandler {

	public Configuration config; 

	public ConfigHandler(File configFile) {
		config = new Configuration(configFile);
	}

	public void load(boolean load) {
		//load config
		if(load) {
			try {
				config.load();
			} catch(Exception e) {
				LogHelper.error(Reference.MOD_NAME + " has had a problem loading its configuration!");
			}
		}
		//read config
		Property prop;

		////////////
		// DEBUG //
		///////////

		if(Settings.IS_OBFUSCATED) {
			Settings.DEBUG = config.getBoolean(Keys.Debug.DEBUG, Configuration.CATEGORY_GENERAL, DefaultSettings.Debug.DEBUG, "Enables dev debug features");
		}
		Settings.DEBUG_TESTING = config.getBoolean(Keys.Debug.DEBUG_TESTING, Configuration.CATEGORY_GENERAL, DefaultSettings.Debug.DEBUG_TESTING, "Enables debug testing features");

		//save config
		if(config.hasChanged()) {
			config.save();
		}
	}


	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equalsIgnoreCase(Reference.MOD_ID)) {
			load(false);
			LogHelper.info("Reloading config!");
		}
	}

}