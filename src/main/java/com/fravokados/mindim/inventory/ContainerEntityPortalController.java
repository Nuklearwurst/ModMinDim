package com.fravokados.mindim.inventory;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class ContainerEntityPortalController extends Container {

	private TileEntityPortalControllerEntity te;

	public ContainerEntityPortalController(InventoryPlayer player, TileEntityPortalControllerEntity te) {
		super();
		this.te = te;

		this.addSlotToContainer(new Slot(te, 0, 55, 80)); //destination card
		this.addSlotToContainer(new Slot(te, 1, 199, 80)); //battery
		this.addSlotToContainer(new Slot(te, 2, 14, 10)); //input destination card
		this.addSlotToContainer(new Slot(te, 3, 14, 68)); //output destination card

		//Player inventory
		int i;
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + 47 + j * 18, 109 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(player, i, 8 + 47 + i * 18, 167));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting p_75132_1_) {
		//TODO update energy
		super.addCraftingToCrafters(p_75132_1_);
	}

	@Override
	public void detectAndSendChanges() {
		//TODO update energy
		super.detectAndSendChanges();
	}

	@Override
	public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
		//TODO update energy
		super.updateProgressBar(p_75137_1_, p_75137_2_);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		//TODO mnanage shift clicking
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.te.isUseableByPlayer(player);
	}
}
