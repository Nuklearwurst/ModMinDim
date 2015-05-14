package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.block.IFacingSix;
import com.fravokados.mindim.util.LogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * helper tileentity that stores data used to determine the portals destination
 * will not save or load from disk
 * TODO: find way to destroy portal when attempted to be saved
 *
 * @author Nuklearwurst
 */
public class TileEntityPortal extends TileEntity implements IFacingSix {

	private boolean validPortal = false;

	private int coreX;
	private int coreY;
	private int coreZ;

	private short facing = 0;

	public void setPortalController(int x, int y, int z) {
		this.coreX = x;
		this.coreY = y;
		this.coreZ = z;
		this.validPortal = true;
	}

	public void onEntityEnterPortal(Entity entity) {
		if(!validPortal) {
			this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return;
		}
		TileEntityPortalControllerEntity controller = getController();
		if(controller != null) {
			controller.teleportEntity(entity);
		}
	}

	//TODO: remove obsolete code
//	public void collapseWholePortal() {
//		TileEntityPortalControllerEntity controller = getController();
//		if(controller != null) {
//			controller.collapseWholePortal();
//		}
//	}

	public TileEntityPortalControllerEntity getController() {
		TileEntity controller = this.worldObj.getTileEntity(coreX, coreY, coreZ);
		if(controller == null || !(controller instanceof TileEntityPortalControllerEntity)) {
			LogHelper.warn("Invalid Controller Found! portal: [" + xCoord + "; " + yCoord + "; " + zCoord + "], controller: [" + coreX + "; " + coreY + "; " + coreZ + "]");
			this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return null;
		}
		return (TileEntityPortalControllerEntity) controller;
	}

	@Override
	public void setFacing(short b) {
		facing = b;
	}

	@Override
	public short getFacing() {
		return facing;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setShort("facing", facing);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();
		if(nbt != null && nbt.hasKey("facing")) {
			int old = facing;
			facing = nbt.getByte("facing");
			if(old != facing) {
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}
}
