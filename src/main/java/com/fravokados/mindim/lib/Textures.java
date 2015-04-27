package com.fravokados.mindim.lib;

import net.minecraft.util.ResourceLocation;

/**
 * @author Nuklearwurst
 */
public class Textures {

	public static final String GUI_LOCATION = "textures/gui/";

	public static final ResourceLocation GUI_ENTITY_PORTAL_CONTROLLER = getResourceLocation(GUI_LOCATION + "GuiEntityPortalController.png");

	public static final String TEXTURE_PREFIX = Reference.ASSET_DIR + ":";

	public static ResourceLocation getResourceLocation(String path) {
		return new ResourceLocation(Reference.ASSET_DIR, path);
	}
}