package com.fravokados.mindim.plugin;

import cpw.mods.fml.common.Loader;

public class PluginTechMobs {
	public static final String MOD_ID = "techmobs";

	public static boolean isAvailable() {
		return Loader.isModLoaded(MOD_ID);
	}
}
