package com.fravokados.mindim.portal;

import com.fravokados.mindim.block.BlockPortalMinDim;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * @author Nuklearwurst
 */
public class PortalMetrics {


	public enum Type {
		ENTITY_PORTAL("portal.entity");

		public String name;

		Type(String name) {
			this.name = name;
		}

		public static String getType(int i) {
			return Type.values()[i].name;
		}
	}

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

	public void placePortalsInsideFrame(World world, int x, int y, int z) {
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
	}

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

	public void calculateOrigin() {
		if(maxY - minY == 0) {
			originY = minY + 1.5;
		} else {
			originY = minY + 0.5;
		}
		originX = minX + (maxX - minX) / 2 + 0.5;
		originZ = minZ + (maxZ - minZ) / 2 + 0.5;
	}

	public boolean isEntityInsidePortal(Entity e, double padding) {
		return  e.posX >= minX - padding && e.posX <= maxX + padding + 1
				&& e.posY >= minY - padding && e.posY <= maxY + padding + 1
				&& e.posZ >= minZ - padding && e.posZ <= maxZ + padding + 1;
	}
}

