package com.fravokados.mindim.configuration;

import net.minecraft.world.World;

public class Settings {

	public static final boolean IS_OBFUSCATED = !World.class.getSimpleName().equals("World");

	public static boolean DEBUG_TESTING = false;
	public static boolean DEBUG = false;

	public static int MAX_PORTAL_SIZE = 20;
	public static int MIN_PORTAL_SIZE = 4;

}
