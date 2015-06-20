package com.fravokados.mindim.lib;

import com.fravokados.mindim.util.GeneralUtils;

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
		public static final String destinationCardMinDim = "destinationCardMinDim";
		public static final String upgrade = "upgrade";

		public static final String upgradeReverse = "upgrade_reverse";
		public static final String upgradeDisconnect = "upgrade_disconnect";
	}

	public static final class TileEntity {

		public static final String TILE_ENTITY_PORTAL_CONTROLLER_ENTITY = "TileEntityPortalControllerEntity";
		public static final String TILE_ENTITY_PORTAL = "TileEntityPortal";
		public static final String TILE_ENTITY_PORTAL_FRAME = "TileEntityPortalFrame";
	}

	public static final class Chat {
		public static final String BLOCK_PLACING_CANCELLED = "chat.mindim.cancelBlockPlacing";
	}

	public static final class Gui {
		public static final String DESTINATION_CARD_PORTAL_BLOCKS_STORED = "gui.mindim.destinationCard.portalBlocksStored";

		public static final String CONTROLLER_ID = "gui.mindim.controller.id";
		public static final String CONTROLLER_DESTINATION = "gui.mindim.controller.destination";
		public static final String CONTROLLER_STATE = "gui.mindim.controller.state";
		public static final String CONTROLLER_ERROR = "gui.mindim.controller.error";

		public static final String GUI_ADD = "gui.add";
		public static final String GUI_START = "gui.start";
		public static final String GUI_STOP = "gui.stop";
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

	public static String translate(String key) {
		return GeneralUtils.translate(key);
	}

	public static String translateWithFormat(String key, Object... values) {
		return GeneralUtils.translateWithFormat(key, values);
	}
}
