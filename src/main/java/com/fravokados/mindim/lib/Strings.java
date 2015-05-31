package com.fravokados.mindim.lib;

/**
 * @author Nuklearwurst
 */
public class Strings {

	public static final String CREATIVE_TAB = "creativeTabMindim";

	public static final class Block {
		public static final String portalMachineBase = "portalComponent";
		public static final String portalMachineController = "portalController";
		public static final String portalMachineFrame = "portalFrame";

		public static final String portal = "mindimPortal";
	}

	public static final class Item {
		public static final String destinationCard = "destinationCard";
		public static final String upgrade = "upgrade";

		public static final String upgradeReverse = "upgrade_reverse";
		public static final String upgradeDisconnect = "upgrade_disconnect";
	}

	public static final class TileEntity {

		public static final String TILE_ENTITY_PORTAL_CONTROLLER_ENTITY = "TileEntityPortalControllerEntity";
		public static final String TILE_ENTITY_PORTAL = "TileEntityPortal";
		public static final String TILE_ENTITY_PORTAL_FRAME = "TileEntityPortalFrame";
	}

	/**
	 * Configuration keys
	 */
	public static final class Keys {
		public static final class Debug {
			public static final String DEBUG = "debug_deobf";
			public static final String DEBUG_TESTING = "debug_testing";
		}
		public static final class General {
			public static final String PORTAL_SPAWN_WITH_CARD = "portal_spawns_with_card";
		}
	}
}
