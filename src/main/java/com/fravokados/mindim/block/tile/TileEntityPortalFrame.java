package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.block.IBlockPlacedListener;
import com.fravokados.mindim.block.IFacingSix;
import com.fravokados.mindim.lib.Strings;
import com.fravokados.mindim.portal.PortalContructor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalFrame extends TileEntity implements IBlockPlacedListener, IEntityPortalMandatoryComponent, IFacingSix {

	private boolean validPortal = false;

	private int coreX;
	private int coreY;
	private int coreZ;

	private short facing = 1;

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

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		coreX = nbt.getInteger("coreX");
		coreY = nbt.getInteger("coreY");
		coreZ = nbt.getInteger("coreZ");
		facing = nbt.getShort("facing");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("coreX", coreX);
		nbt.setInteger("coreY", coreY);
		nbt.setInteger("coreZ", coreZ);
		nbt.setShort("facing", facing);
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
			facing = nbt.getShort("facing");
			if(old != facing) {
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	public void onDestroy() {
		if(validPortal) {
			TileEntity te = worldObj.getTileEntity(coreX, coreY, coreZ);
			if(te != null && te instanceof TileEntityPortalControllerEntity) {
				if(((TileEntityPortalControllerEntity) te).isActive()) {
					worldObj.createExplosion(null, xCoord, yCoord, zCoord, 2.0F, false);
					((TileEntityPortalControllerEntity) te).closePortal();
				}
//				((TileEntityPortalControllerEntity) te).updateMetrics();
			}
		}
	}

	public void setFacing(short facing) {
		this.facing = facing;
	}

	public short getFacing() {
		return facing;
	}
}
