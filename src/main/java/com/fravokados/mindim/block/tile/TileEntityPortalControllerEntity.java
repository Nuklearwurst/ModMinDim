package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.portal.BlockPositionDim;
import com.fravokados.mindim.portal.PortalMetrics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalControllerEntity extends TileEntity implements IInventory {

	private int dest = -1;
	private int id = -1;

	private ItemStack[] inventory = new ItemStack[getSizeInventory()];


	public void onActivated(World world, int x, int y, int z, EntityPlayer player, int side) {
		if (id == -1) {
			id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
		}
		if (dest == -1) {
			dest = ModMiningDimension.instance.portalManager.createPortal(id, null);
		}
		if (dest != -1 && player instanceof EntityPlayerMP) {
			teleportEntity(player);
			return;
		}
		player.addChatComponentMessage(new ChatComponentText("No Destination Found!"));
	}

	public void teleportEntity(Entity entity) {

		PortalMetrics metrics = new PortalMetrics(this.xCoord, this.yCoord, this.zCoord);
		ModMiningDimension.instance.portalManager.teleportEntityToEntityPortal(entity, dest, id, metrics);
	}

	public int onBlockPlaced() {
		return id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < inventory.length) {
			return inventory[slot];
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (this.inventory[slot] != null) {
			ItemStack itemstack;

			if (this.inventory[slot].stackSize <= amount) {
				itemstack = this.inventory[slot];
				this.inventory[slot] = null;
				return itemstack;
			} else {
				itemstack = this.inventory[slot].splitStack(amount);

				if (this.inventory[slot].stackSize == 0) {
					this.inventory[slot] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.inventory[slot] != null)
		{
			ItemStack itemstack = this.inventory[slot];
			this.inventory[slot] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		this.inventory[slot] = itemStack;

		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
		{
			itemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "Portal Controller";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack p_94041_2_) {
		//TODO improve slot validation
		return slot != 3;
	}
}
