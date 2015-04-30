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


    public void onActivated(World world, int x, int y, int z, EntityPlayer player, int side) {
        if(id == -1) {
            id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
        }
        if(dest == -1) {
            dest = ModMiningDimension.instance.portalManager.createPortal(id, null);
        }
        if(dest != -1 && player instanceof EntityPlayerMP) {
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
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {

	}

	@Override
	public String getInventoryName() {
		return "asdasd";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}
}
