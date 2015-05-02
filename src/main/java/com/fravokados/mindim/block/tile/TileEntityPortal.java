package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.util.LogHelper;
import net.minecraft.entity.Entity;
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
		TileEntityPortalControllerEntity controller = getController();
		if(controller != null) {
			controller.teleportEntity(entity);
		}
	}

	public void collapseWholePortal() {
		TileEntityPortalControllerEntity controller = getController();
		if(controller != null) {
			controller.collapseWholePortal();
		}
	}

	public TileEntityPortalControllerEntity getController() {
		TileEntity controller = this.worldObj.getTileEntity(coreX, coreY, coreZ);
		if(controller == null || !(controller instanceof TileEntityPortalControllerEntity)) {
			LogHelper.warn("Invalid Controller Found!");
			this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return null;
		}
		return (TileEntityPortalControllerEntity) controller;
	}

}
