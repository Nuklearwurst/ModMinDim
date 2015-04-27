package com.fravokados.mindim.block.tile;

import net.minecraft.tileentity.TileEntity;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalFrame extends TileEntity {

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

}
