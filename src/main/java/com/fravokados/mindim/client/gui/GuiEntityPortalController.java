package com.fravokados.mindim.client.gui;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.inventory.ContainerEntityPortalController;
import com.fravokados.mindim.lib.Textures;
import com.fravokados.mindim.network.NetworkManager;
import com.fravokados.mindim.network.network.MessageGuiElementClicked;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nuklearwurst
 */
public class GuiEntityPortalController extends GuiContainer {

	private static final int BUTTON_ID_START = 0;
	private static final int BUTTON_ID_STOP = 1;

	private TileEntityPortalControllerEntity te;

	private GuiButton btnStop;
	private GuiButton btnStart;

	public GuiEntityPortalController(InventoryPlayer inv, TileEntityPortalControllerEntity te) {
		super(new ContainerEntityPortalController(inv, te));
		this.xSize = 176 + 47;
		this.ySize = 191;
		this.te = te;
	}

	@SuppressWarnings(value = {"unchecked"})
	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - 176) / 2 - 47;

		btnStart = new GuiButton(BUTTON_ID_START, guiLeft + 75, guiTop + 78, 56, 20, "Start");
		this.buttonList.add(btnStart);
		btnStop = new GuiButton(BUTTON_ID_STOP, guiLeft + 133, guiTop + 78, 56, 20, "Stop");
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
	public void drawScreen(int x, int y, float f) {
		btnStart.enabled = te.getState() == TileEntityPortalControllerEntity.State.READY || te.getState() == TileEntityPortalControllerEntity.State.INCOMING_CONNECTION;
		btnStop.enabled = te.getState() == TileEntityPortalControllerEntity.State.CONNECTING || te.getState() == TileEntityPortalControllerEntity.State.OUTGOING_PORTAL || te.getState() == TileEntityPortalControllerEntity.State.INCOMING_PORTAL;
		super.drawScreen(x, y, f);
		drawTooltips(x, y);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(Textures.GUI_ENTITY_PORTAL_CONTROLLER);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 199, guiTop + 15 + 55 - getEnergyScaled(), 223, 36 + 55 - getEnergyScaled(), 16, getEnergyScaled());
	}

	private int getEnergyScaled() {
		return (int) (te.getEnergyStored() * 55 / te.getMaxEnergyStored());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		drawString(this.fontRendererObj, "ID: " + te.getId(), 58, 18, 4210752);
		drawString(this.fontRendererObj, "Destination: " + te.getDestination(), 58, 30, 4210752);
		drawString(this.fontRendererObj, "State: " + te.getState(), 58, 40, 4210752);
		drawString(this.fontRendererObj, "Last Error: " + te.getLastError().name, 58, 50, 4210752);
	}

	/**
	 * Draws Tooltips
	 *
	 * @param x mouse position x
	 * @param y mouse position y
	 */
	private void drawTooltips(int x, int y) {
		if(x >= guiLeft + 199 && y >= guiTop + 15 && x < guiLeft + 199 + 16 && y < guiTop + 15 + 55) {
			List<String> list = new ArrayList<String>();
			list.add(EnumChatFormatting.GRAY + "" + (int) te.getEnergyStored() + " EU" + EnumChatFormatting.RESET);
			drawHoveringText(list, x, y, fontRendererObj);
		}
	}


	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
