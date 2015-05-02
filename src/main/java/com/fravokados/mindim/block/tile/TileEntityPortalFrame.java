package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.block.IBlockPlacedListener;
import com.fravokados.mindim.portal.PortalContructor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalFrame extends TileEntity implements IBlockPlacedListener, IEntityPortalMandatoryComponent {

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

	@Override
	public void onBlockPostPlaced(World world, int x, int y, int z, int meta) {
		PortalContructor.createPortalMultiBlock(world, x, y, z);
	}

	public boolean isActive() {
		TileEntity te = worldObj.getTileEntity(coreX, coreY, coreZ);
		return validPortal && te != null && te instanceof TileEntityPortalControllerEntity && ((TileEntityPortalControllerEntity) te).isActive();
	}


}
