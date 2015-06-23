package com.fravokados.mindim.configuration;

import net.minecraft.world.World;

public class Settings {

	/** simple test to determine wether we are in a dev environment */
	public static final boolean IS_OBFUSCATED = !World.class.getSimpleName().equals("World");

	/** general testing features */
	public static boolean DEBUG_TESTING = false;
	/** dev env only testing features */
	public static boolean DEBUG = false;

	/** maximum size of the portal frame in any direction */
	public static int MAX_PORTAL_SIZE = 20;
	/** minimum size inside the frame */
	public static int MIN_PORTAL_SIZE = 2;

	/** time in ticks the portal takes to open a connection */
	public static int PORTAL_CONNECTION_TIME = 100;
	/** maximum time in ticks a portal can be held open */
	public static int MAX_PORTAL_CONNECTION_LENGTH = 20 * 20;

	public static int ENERGY_USAGE_INIT = 10000;
	public static int ENERGY_USAGE = 100;

	/** should a mindim portal spawn with a card leading back */
	public static boolean PORTAL_SPAWN_WITH_CARD = false;
}
