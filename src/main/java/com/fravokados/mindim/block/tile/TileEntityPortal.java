package com.fravokados.mindim.block.tile;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortal extends TileEntity {

	private boolean validPortal = false;

	private int coreX;
	private int coreY;
	private int coreZ;

	public void setPortalController(int x, int y, int z) {
		this.coreX = x;
		this.coreY = y;
		this.coreZ = z;
		this.validPortal = true;
	}

	public void onEntityEnterPortal(Entity entity) {
		if(!validPortal) {
			collapseWholePortal();
			return;
		}
		TileEntity controller = this.worldObj.getTileEntity(coreX, coreY, coreZ);
		if(controller == null || !(controller instanceof TileEntityPortal)) {
			collapseWholePortal();
			return;
		}
		//((TileEntityPortalControllerEntity)controller).teleportEntity(entity);
	}

	public void collapseWholePortal() {
		//TODO: collapse whole portal
		this.worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);

	}

}
