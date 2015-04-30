package com.fravokados.mindim.inventory;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author Nuklearwurst
 */
public class ContainerEntityPortalController extends Container {

	private TileEntityPortalControllerEntity te;

	public ContainerEntityPortalController(InventoryPlayer player, TileEntityPortalControllerEntity te) {
		super();
		this.te = te;

		this.addSlotToContainer(new Slot(player, 0, 55 - 47, 80));
		this.addSlotToContainer(new Slot(player, 1, 199 - 47, 80));
		this.addSlotToContainer(new Slot(player, 1, 14 - 47, 10));
		this.addSlotToContainer(new Slot(player, 1, 14 - 47, 68));

		//Player inventory
		int i;
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 109 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 167));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.te.isUseableByPlayer(player);
	}
}
