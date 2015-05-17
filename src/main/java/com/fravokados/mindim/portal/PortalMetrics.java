package com.fravokados.mindim.portal;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.block.BlockPortalMinDim;
import com.fravokados.mindim.block.tile.IEntityPortalMandatoryComponent;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class PortalMetrics {


	public enum Type {
		ENTITY_PORTAL("portal.entity");

		public final String name;

		Type(String name) {
			this.name = name;
		}

		public static String getType(int i) {
			return Type.values()[i].name;
		}
	}

	public ForgeDirection top = ForgeDirection.UP;
	public ForgeDirection front = ForgeDirection.EAST;

	public double originX;
	public double originY;
	public double originZ;


	public int minX;
	public int minY;
	public int minZ;

	public int maxX;
	public int maxY;
	public int maxZ;

	private boolean init = false;

	public PortalMetrics() {

	}

	public PortalMetrics(double xCoord, double yCoord, double zCoord) {
		this.originX = xCoord;
		this.originY = yCoord;
		this.originZ = zCoord;
		minX = maxX = (int) originX;
		minY = maxY = (int) originY;
		minZ = maxZ = (int) originZ;
		init = true;
	}

	public boolean isBlockInsideFrame(int x, int y, int z) {
		return x >= minX + 1 && x <= maxX - 1 && y >= minY + 1 && y <= maxY - 1 && z >= minZ + 1 && z <= maxZ - 1;
	}

	public boolean isBlockInside(int x, int y, int z) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
	}

	public boolean isHorizontal() {
		return minY == maxY;
	}

	public void addCoord(int x, int y, int z) {
		if(init) {
			minX = Math.min(x, minX);
			minY = Math.min(y, minY);
			minZ = Math.min(z, minZ);
			maxX = Math.max(x, maxX);
			maxY = Math.max(y, maxY);
			maxZ = Math.max(z, maxZ);
		} else {
			minX = maxX = x;
			minY = maxY = y;
			minZ = maxZ = z;
			init = true;
		}
	}

	public int biggestDimension() {
		return Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ);
	}

	public int smallestDimension() {
		int x = maxX - minX;
		int y = maxY - minY;
		int z = maxZ - minZ;
		if(x == 0) {
			return Math.min(y, z);
		}
		if(y == 0) {
			return Math.min(x, z);
		}
		if(z == 0) {
			return Math.min(x, y);
		}
		return 0;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean placePortalsInsideFrame(World world, int x, int y, int z) {
		for(int j = minX; j <= maxX; j++) { //X
			for(int k = minY; k <= maxY; k++) { //Y
				for(int l = minZ; l <= maxZ; l++) { //Z
					if(minX - maxX != 0 && (j == minX || j == maxX)) {
						continue;
					}
					if(minY - maxY != 0 && (k == minY || k == maxY)) {
						continue;
					}
					if(minZ - maxZ != 0 && (l == minZ || l == maxZ)) {
						continue;
					}
					BlockPortalMinDim.placePortalInWorld(world, j, k, l, x, y, z);
				}
			}
		}
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	public void removePortalsInsideFrame(World world) {
		for(int j = minX; j <= maxX; j++) { //X
			for(int k = minY; k <= maxY; k++) { //Y
				for(int l = minZ; l <= maxZ; l++) { //Z
					if(minX - maxX != 0 && (j == minX || j == maxX)) {
						continue;
					}
					if(minY - maxY != 0 && (k == minY || k == maxY)) {
						continue;
					}
					if(minZ - maxZ != 0 && (l == minZ || l == maxZ)) {
						continue;
					}
					if(world.getBlock(j, k, l) instanceof BlockPortalMinDim) {
						world.setBlockToAir(j, k, l);
					}
				}
			}
		}
	}

	public void setOrigin(double x, double y, double z) {
		originX = x;
		originY = y;
		originZ = z;
	}

	public void calculateOrigin(List<IEntityPortalMandatoryComponent> list) {
		if(maxY - minY == 0) {
			originY = minY + 0.5; //center in horizontal portals
		} else {
			originY = minY + 1; //ground om vertical portals
		}
		originX = minX + ((maxX - minX) / 2 + 0.5);
		originZ = minZ + ((maxZ - minZ) / 2 + 0.5);
		//Orientation
		int[] orientation = {0, 0, 0, 0, 0, 0};
		for(IEntityPortalMandatoryComponent frame : list) {
			orientation[frame.getFacing()]++;
		}
		int facing = 0;
		int max = 0;
		for(int i = 0; i < 6; i++) {
			if(orientation[i] > max) {
				facing = i;
				max = orientation[i];
			}
		}
		front = ForgeDirection.getOrientation(facing);
		if(maxX - minX == 0) {
			top = ForgeDirection.UP;
			if(front != ForgeDirection.EAST && front != ForgeDirection.WEST) {
				front = ForgeDirection.EAST;
			}
		} else if(maxY - minY == 0) {
			if(front != ForgeDirection.DOWN && front != ForgeDirection.UP) {
				front = ForgeDirection.UP;
			}
			top = front == ForgeDirection.UP ? ForgeDirection.EAST : ForgeDirection.WEST;
		} else if(maxZ - minZ == 0) {
			top = ForgeDirection.UP;
			if(front != ForgeDirection.NORTH && front != ForgeDirection.SOUTH) {
				front = ForgeDirection.NORTH;
			}
		}
	}

	public boolean isEntityInsidePortal(Entity e, double padding) {
		return  e.posX >= minX - padding && e.posX <= maxX + padding + 1
				&& e.posY >= minY - padding && e.posY <= maxY + padding + 1
				&& e.posZ >= minZ - padding && e.posZ <= maxZ + padding + 1;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setDouble("originX", originX);
		nbt.setDouble("originY", originY);
		nbt.setDouble("originZ", originZ);
		nbt.setInteger("minX", minX);
		nbt.setInteger("minY", minY);
		nbt.setInteger("minZ", minZ);
		nbt.setInteger("maxX", maxX);
		nbt.setInteger("maxY", maxY);
		nbt.setInteger("maxZ", maxZ);
		nbt.setInteger("top", top.ordinal());
		nbt.setInteger("front", front.ordinal());
	}

	public void readFromNBT(NBTTagCompound nbt) {
		originX = nbt.getDouble("originX");
		originY = nbt.getDouble("originY");
		originZ = nbt.getDouble("originZ");
		minX = nbt.getInteger("minX");
		minY = nbt.getInteger("minY");
		minZ = nbt.getInteger("minZ");
		maxX = nbt.getInteger("maxX");
		maxY = nbt.getInteger("maxY");
		maxZ = nbt.getInteger("maxZ");
		top = ForgeDirection.getOrientation(nbt.getInteger("top"));
		front = ForgeDirection.getOrientation(nbt.getInteger("front"));
	}

	public static PortalMetrics getMetricsFromNBT(NBTTagCompound nbt) {
		if(nbt == null) {
			return null;
		}
		PortalMetrics metrics = new PortalMetrics();
		metrics.readFromNBT(nbt);
		return metrics;
	}

	public double getMaxUp() {
		if(top.offsetX != 0) {
			return maxX - originX;
		} else if(top.offsetY != 0) {
			return  maxY - originY;
		} else if(top.offsetZ != 0) {
			return maxZ - originZ;
		}
		return 0;
	}

	public double getMinUp() {
		if(top.offsetX != 0) {
			return minX - originX;
		} else if(top.offsetY != 0) {
			return  minY - originY;
		} else if(top.offsetZ != 0) {
			return minZ - originZ;
		}
		return 0;
	}

	public double getMaxSide() {
		ForgeDirection side = top.getRotation(front);
		if(side.offsetX != 0) {
			return maxX - originX;
		} else if(side.offsetY != 0) {
			return  maxY - originY;
		} else if(side.offsetZ != 0) {
			return maxZ - originZ;
		}
		return 0;
	}

	public double getMinSide() {
		ForgeDirection side = top.getRotation(front);
		if(side.offsetX != 0) {
			return minX - originX;
		} else if(side.offsetY != 0) {
			return  minY - originY;
		} else if(side.offsetZ != 0) {
			return minZ - originZ;
		}
		return 0;
	}

	/**
	 *
	 * @return returns how many blocks are needed to form the frame of this portal
	 */
	public int getFrameBlockCount() {
		return (maxX - minX) * 2 + (maxY - minY) * 2 + (maxZ - minZ) *2;
	}

	/**
	 * creates a portal with x, y, z as its origin
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	@SuppressWarnings("ConstantConditions")
	public boolean placePortalFrame(World world, int x, int y, int z, boolean clearInside) {
		int minX = this.minX - (int) (originX);
		int minY = this.minY - (int) (originY);
		int minZ = this.minZ - (int) (originZ);
		int maxX = this.maxX - (int) (originX);
		int maxY = this.maxY - (int) (originY);
		int maxZ = this.maxZ - (int) (originZ);
		boolean flagX;
		boolean flagY;
		boolean flagZ;
		for(int i = minX; i <= maxX; i++) {
			flagX = i == minX || i == maxX;
			for(int j = minY; j <= maxY; j++) {
				flagY = j == minY || j == maxY;
				for(int k = minZ; k <= maxZ; k++) {
					flagZ = k == minZ || k == maxZ;
					if(flagX == flagY ? flagX : flagZ) {
						world.setBlock(x + i, y + j, z + k, ModMiningDimension.instance.portalFrame, BlockPortalFrame.META_FRAME_ENTITY, 0);
					} else {
						world.setBlock(x + i, y + j, z + k, Blocks.air, 0, 0);
					}
				}
			}
		}
		return true;
	}
}

