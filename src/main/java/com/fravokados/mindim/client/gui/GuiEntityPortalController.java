package com.fravokados.mindim.client.gui;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.lib.Textures;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

/**
 * @author Nuklearwurst
 */
public class GuiEntityPortalController extends GuiContainer {
	private TileEntityPortalControllerEntity te;

	public GuiEntityPortalController(TileEntityPortalControllerEntity te, Container container) {
		super(container);
		this.xSize = 176;
		this.ySize = 191;
		this.te = te;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(Textures.GUI_ENTITY_PORTAL_CONTROLLER);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
