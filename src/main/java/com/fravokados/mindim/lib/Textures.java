package com.fravokados.mindim.lib;

import net.minecraft.util.ResourceLocation;

/**
 * @author Nuklearwurst
 */
public class Textures {

	public static final String GUI_LOCATION = "textures/gui/";

	public static final ResourceLocation GUI_ENTITY_PORTAL_CONTROLLER = getResourceLocation(GUI_LOCATION + "GuiEntityPortalController.png");
	public static final ResourceLocation GUI_DESTINATION_CARD_MIN_DIM = getResourceLocation(GUI_LOCATION + "GuiDestinationCardMinDim.png");

	public static final String TEXTURE_PREFIX = Reference.ASSET_DIR + ":";

	public static final String BLOCK_PORTAL_FRAME = TEXTURE_PREFIX + Strings.Block.portalMachineFrame;
	public static final String BLOCK_PORTAL_CONTROLLER = TEXTURE_PREFIX + Strings.Block.portalMachineController;

	public static final String ITEM_DESTINATION_CARD = TEXTURE_PREFIX + Strings.Item.destinationCard;

	public static final String ITEM_UPGRADE_REVERSE_DIRECTION = TEXTURE_PREFIX + Strings.Item.upgradeReverse;
	public static final String ITEM_UPGRADE_DISCONNECT = TEXTURE_PREFIX + Strings.Item.upgradeDisconnect;


	public static ResourceLocation getResourceLocation(String path) {
		return new ResourceLocation(Reference.ASSET_DIR, path);
	}
}
