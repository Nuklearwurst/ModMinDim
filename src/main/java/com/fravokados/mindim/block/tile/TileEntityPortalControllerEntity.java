package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.inventory.ContainerEntityPortalController;
import com.fravokados.mindim.item.ItemDestinationCard;
import com.fravokados.mindim.portal.BlockPositionDim;
import com.fravokados.mindim.portal.PortalMetrics;
import com.fravokados.mindim.util.ItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalControllerEntity extends TileEntity implements IInventory {

	private int id = -1;

	private String name = null;
	private ItemStack[] inventory = new ItemStack[getSizeInventory()];


	public void createPortal() {
		if (id == -1) {
			id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
		}
		if (getDestination() == -1) {
			int dest = ModMiningDimension.instance.portalManager.createPortal(id, null);
			if(dest >= 0)
				setDest(dest);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDestination() {
		if(inventory[0] == null) {
			return -1;
		}
		if(inventory[0].getItem() instanceof ItemDestinationCard) {
			if(inventory[0].stackTagCompound != null && inventory[0].stackTagCompound.hasKey("destinationPortalType") && inventory[0].stackTagCompound.hasKey("destinationPortal")) {
				if(inventory[0].stackTagCompound.getInteger("destinationPortalType") == PortalMetrics.Type.ENTITY_PORTAL.ordinal()) {
					return inventory[0].stackTagCompound.getInteger("destinationPortal");
				} else {
					return -3;
				}
			}
		}
		return -2;
	}

	public void teleportPlayerToPortal(EntityPlayer player) {
		if (getDestination() != -1 && player instanceof EntityPlayerMP) {
			teleportEntity(player);
			return;
		}
		player.addChatComponentMessage(new ChatComponentText("No Destination Found!"));
	}

	public void teleportEntity(Entity entity) {

		PortalMetrics metrics = new PortalMetrics(this.xCoord, this.yCoord, this.zCoord);
		ModMiningDimension.instance.portalManager.teleportEntityToEntityPortal(entity, getDestination(), id, metrics);
	}

	public int onBlockPlaced() {
		return id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
	}

	public void setDest(int dest) {
		ItemStack card = new ItemStack(ModMiningDimension.instance.itemDestinationCard);
		NBTTagCompound nbt = ItemUtils.getNBTTagCompound(card);
		nbt.setInteger("destinationPortalType", PortalMetrics.Type.ENTITY_PORTAL.ordinal());
		nbt.setInteger("destinationPortal", dest);
		inventory[0] = card;
		markDirty();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(id != -1 && inventory[2] != null && inventory[3] == null) {
			inventory[3] = inventory[2];
			inventory[2] = null;
			NBTTagCompound nbt = ItemUtils.getNBTTagCompound(inventory[3]);
			nbt.setInteger("destinationPortalType", PortalMetrics.Type.ENTITY_PORTAL.ordinal());
			nbt.setInteger("destinationPortal", id);
			markDirty();
		}
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
		if (this.inventory[slot] != null) {
			ItemStack itemstack = this.inventory[slot];
			this.inventory[slot] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		this.inventory[slot] = itemStack;

		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
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
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
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

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		NBTTagList nbttaglist = nbt.getTagList("Items", 10); //10 is compound type
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inventory.length) {
				this.inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		if(nbt.hasKey("name")) {
			name = nbt.getString("name");
		}
		id = nbt.getInteger("PortalID");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(tag);
				nbttaglist.appendTag(tag);
			}
		}
		nbt.setTag("Items", nbttaglist);
		if(hasCustomInventoryName()) {
			nbt.setString("name", name);
		}
		nbt.setInteger("PortalID", id);
	}

	@SuppressWarnings(value = "unchecked")
	public void handleStartButton(ContainerEntityPortalController containerEntityPortalController) {
		createPortal();
		List<EntityPlayerMP> list = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(xCoord - 8, yCoord - 4, zCoord - 8, xCoord + 8, yCoord + 4, zCoord + 8));
		for (EntityPlayerMP player : list) {
			player.closeScreen();
			teleportPlayerToPortal(player);
		}
	}

	public void handleStopButton(ContainerEntityPortalController containerEntityPortalController) {

	}
}
