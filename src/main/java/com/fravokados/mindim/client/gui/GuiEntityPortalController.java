package com.fravokados.mindim.client.gui;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.inventory.ContainerEntityPortalController;
import com.fravokados.mindim.lib.Textures;
import com.fravokados.mindim.network.NetworkManager;
import com.fravokados.mindim.network.network.MessageGuiElementClicked;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

/**
 * @author Nuklearwurst
 */
public class GuiEntityPortalController extends GuiContainer {

	private static final int BUTTON_ID_START = 0;
	private static final int BUTTON_ID_STOP = 1;

	private TileEntityPortalControllerEntity te;

	private GuiButton btnStop;

	public GuiEntityPortalController(InventoryPlayer inv, TileEntityPortalControllerEntity te) {
		super(new ContainerEntityPortalController(inv, te));
		this.xSize = 176 + 47;
		this.ySize = 191;
		this.te = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - 176) / 2 - 47;
		this.buttonList.add(new GuiButton(BUTTON_ID_START, guiLeft + 75, guiTop + 78, 56, 20, "Start"));
		btnStop = new GuiButton(BUTTON_ID_STOP, guiLeft + 133, guiTop + 78, 56, 20, "Stop");
		btnStop.enabled = false;
		this.buttonList.add(btnStop);
	}


	@Override
	protected void actionPerformed(GuiButton btn) {
		switch (btn.id) {
			case BUTTON_ID_START:
				NetworkManager.INSTANCE.sendToServer(new MessageGuiElementClicked(ContainerEntityPortalController.NETWORK_ID_START, 0));
				return;
			case BUTTON_ID_STOP:
				NetworkManager.INSTANCE.sendToServer(new MessageGuiElementClicked(ContainerEntityPortalController.NETWORK_ID_STOP, 0));
				return;
		}
		super.actionPerformed(btn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(Textures.GUI_ENTITY_PORTAL_CONTROLLER);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		drawString(this.fontRendererObj, "ID: " + te.getId(), 58, 18, 4210752);
		drawString(this.fontRendererObj, "Destination: " + te.getDestination(), 58, 40, 4210752);
	}


	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
