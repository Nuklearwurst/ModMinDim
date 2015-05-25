package com.fravokados.mindim.portal;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.util.LogHelper;
import com.fravokados.mindim.util.RotationUtils;
import com.fravokados.mindim.util.TeleportUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nuklearwurst
 */
public class PortalManager extends WorldSavedData {

	//Destination numbers
	public static final int PORTAL_NOT_CONNECTED = -1;
	public static final int PORTAL_MINING_DIMENSION = -2;
	public static final int PORTAL_INVALID_ITEM = -3;
	public static final int PORTAL_WRONG_TYPE = -4;


	private final Map<Integer, BlockPositionDim> entityPortals = new HashMap<Integer, BlockPositionDim>();
	private int entityPortalCounter = 0;

	public PortalManager(String s) {
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		entityPortals.clear();

		entityPortalCounter = nbt.getInteger("entityPortalCounter");
		int[] keys = nbt.getIntArray("entityPortalKeys");
		for (int portalId : keys) {
			if (nbt.hasKey("entityPortal_" + portalId)) {
				BlockPositionDim pos = new BlockPositionDim();
				pos.readFromNBT(nbt.getCompoundTag("entityPortal_" + portalId));
				entityPortals.put(portalId, pos);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("entityPortalCounter", entityPortalCounter);
		int[] entityPortalKeys = new int[entityPortals.size()];
		int i = 0;
		for (int portalId : entityPortals.keySet()) {
			entityPortalKeys[i] = portalId;
			NBTTagCompound tag = new NBTTagCompound();
			entityPortals.get(portalId).writeToNBT(tag);
			nbt.setTag("entityPortal_" + portalId, tag);
			i++;
		}
		nbt.setIntArray("entityPortalKeys", entityPortalKeys);
	}

	/**
	 * registers a new entity portal
	 * @param pos position of the controller
	 * @return new id
	 */
	public int registerNewEntityPortal(BlockPositionDim pos) {
		registerEntityPortal(++entityPortalCounter, pos);
		return entityPortalCounter;
	}

	public boolean entityPortalExists(int portal) {
		return entityPortals.containsKey(portal);
	}

	public void registerEntityPortal(int portal, BlockPositionDim pos) {
		entityPortals.put(portal, pos);
		this.markDirty();
	}


	/**
	 * @param entity   entity to teleport
	 * @param portalId destination portal id
	 * @param originMetrics  used to calculate Entity Position in the destination portal (originMetrics of the origin portal)
	 */
	public boolean teleportEntityToEntityPortal(Entity entity, int portalId, int parent, PortalMetrics originMetrics) {
		if (!entityPortalExists(portalId)) {
			return false;
		}
		if (portalId == -1) {
			return false;
		}
		BlockPositionDim pos = getEntityPortalForId(portalId);
		if (pos == null) {
			return false;
		}

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer worldServer = server.worldServerForDimension(pos.dimension);
		TileEntity te = worldServer.getTileEntity(pos.x, pos.y, pos.z);
		if (te != null && te instanceof TileEntityPortalControllerEntity) {
			PortalMetrics targetMetrics = ((TileEntityPortalControllerEntity) te).getMetrics();
			if(targetMetrics != null) {
				//TODO: update player rotation
				//TODO: update portal boundaries
				ForgeDirection sideAxisOrigin = originMetrics.top.getRotation(originMetrics.front);
				ForgeDirection sideAxisTarget = targetMetrics.top.getRotation(targetMetrics.front);

				double posX = entity.posX - originMetrics.originX;
				double posY = entity.posY - originMetrics.originY;
				double posZ = entity.posZ - originMetrics.originZ;
//
//				//make sure player spawns inside the portal
//				if(originMetrics.front.posX != 0) {
//					posX = 0;
//				} else if(originMetrics.front.posY != 0) {
//					posY = 0;
//				} else if(originMetrics.front.posZ != 0) {
//					posZ = 0;
//				}

				double maxUp = targetMetrics.getMaxUp() - 0.5;
				double minUp = targetMetrics.getMinUp() + 0.5;
				double maxSide = targetMetrics.getMaxSide() - 0.5;
				double minSide = targetMetrics.getMinSide() + 0.5;

				////////////////
				//  position  //
				////////////////
				//relative coordinate system
				double posTop = posX * originMetrics.top.offsetX + posY * originMetrics.top.offsetY + posZ * originMetrics.top.offsetZ;
				double posSide = posX * sideAxisOrigin.offsetX + posY * sideAxisOrigin.offsetY + posZ * sideAxisOrigin.offsetZ;

				//make sure player spawns inside portal
				posTop = MathHelper.clamp_double(posTop, minUp, maxUp); //"Top" Axis
				posSide = MathHelper.clamp_double(posSide, minSide, maxSide); //"Side" Axis

				//target coordinate system
				double x = targetMetrics.originX + targetMetrics.top.offsetX * posTop + sideAxisTarget.offsetX * posSide;
				double y = targetMetrics.originY + targetMetrics.top.offsetY * posTop + sideAxisTarget.offsetY * posSide;
				double z = targetMetrics.originZ + targetMetrics.top.offsetZ * posTop + sideAxisTarget.offsetZ * posSide;

				if(targetMetrics.front == ForgeDirection.DOWN) {
					//spawn slighly lower to compensate entity/player height on down facing portals
					y -= entity.height;
				}

				////////////////
				//  momentum  //
				////////////////
				//relative coordinate system
				double speed_rel_x = entity.motionX * originMetrics.front.offsetX + entity.motionY * originMetrics.front.offsetY + entity.motionZ * originMetrics.front.offsetZ;
				double speed_rel_y = entity.motionX * originMetrics.top.offsetX + entity.motionY * originMetrics.top.offsetY + entity.motionZ * originMetrics.top.offsetZ;
				double speed_rel_z = entity.motionX * sideAxisOrigin.offsetX + entity.motionY * sideAxisOrigin.offsetY + entity.motionZ * sideAxisOrigin.offsetZ;

				//target coordinate system
				double speed_x = targetMetrics.front.offsetX * (-1) * speed_rel_x + targetMetrics.top.offsetX * speed_rel_y + sideAxisTarget.offsetX * speed_rel_z;
				double speed_y = targetMetrics.front.offsetY * (-1) * speed_rel_x + targetMetrics.top.offsetY * speed_rel_y + sideAxisTarget.offsetY * speed_rel_z;
				double speed_z = targetMetrics.front.offsetZ * (-1) * speed_rel_x + targetMetrics.top.offsetZ * speed_rel_y + sideAxisTarget.offsetZ * speed_rel_z;

				//update entity
				entity.setVelocity(speed_x, speed_y, speed_z);
				entity.velocityChanged = true;
				////////////////
				//  rotation  //
				////////////////
				if(originMetrics.isHorizontal()) {
					if(targetMetrics.isHorizontal()) {
						entity.rotationYaw = (entity.rotationYaw + RotationUtils.get2DRotation(originMetrics.top, targetMetrics.top)) % 360;
					} else {
						entity.rotationPitch = (entity.rotationPitch + originMetrics.front.offsetY * (-90)) % 180;
						entity.rotationYaw = (entity.rotationYaw + RotationUtils.get2DRotation(originMetrics.top, targetMetrics.front)) % 360;
					}
				} else {
					if(targetMetrics.isHorizontal()) {
						entity.rotationPitch = (entity.rotationPitch + targetMetrics.front.offsetY * 90) % 180;
						entity.rotationYaw = (entity.rotationYaw + RotationUtils.get2DRotation(originMetrics.front, targetMetrics.top)) % 360;
					} else {
						entity.rotationYaw = (entity.rotationYaw + RotationUtils.get2DRotation(originMetrics.front, targetMetrics.front)) % 360;
					}
				}


				////////////////
				//  transfer  //
				////////////////
				if (entity instanceof EntityPlayerMP) {
					if (pos.dimension == entity.dimension) {
						entity.mountEntity(null); //needed?
						((EntityPlayerMP) entity).setPositionAndUpdate(x, y, z);
					} else {
						TeleportUtils.transferPlayerToDimension((EntityPlayerMP) entity, pos.dimension, x, y, z, entity.rotationYaw, entity.rotationPitch);
					}
				} else {
					if (pos.dimension == entity.dimension) {
						entity.mountEntity(null); //needed?
						entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
					} else {
						TeleportUtils.transferEntityToDimension(entity, pos.dimension, x, y, z, entity.rotationYaw);
					}
				}
			}
			return true;
		}
		return false;
	}

	public int createPortal(int parent, PortalMetrics metrics, TileEntity parentTile) {
		BlockPositionDim pos = entityPortals.get(parent);
		if (pos == null) {
			return PORTAL_NOT_CONNECTED;
		}
		//FIXME: For now it is possible creating portals both ways
		int dim = pos.dimension == ModMiningDimension.dimensionId ? 0 : ModMiningDimension.dimensionId;

		//get the destination world server
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer worldServer = server.worldServerForDimension(dim);

		//create the portal frame
		PortalContructor.createPortalFromMetrics(worldServer, metrics, true);
		//create the controller
		worldServer.setBlock(parentTile.xCoord, parentTile.yCoord, parentTile.zCoord, ModMiningDimension.instance.blockPortalFrame, BlockPortalFrame.META_CONTROLLER_ENTITY, 3);
		TileEntity te = worldServer.getTileEntity(pos.x, pos.y, pos.z);
		if (te != null && te instanceof TileEntityPortalControllerEntity) {
			((TileEntityPortalControllerEntity) te).setDest(parent);
			((TileEntityPortalControllerEntity) te).onBlockPostPlaced(te.getWorldObj(), pos.x, pos.y, pos.z, worldServer.getBlockMetadata(pos.x, pos.y, pos.z));
			return ((TileEntityPortalControllerEntity) te).getId();
		}
		LogHelper.warn("Error creating Portal Controller!");
		return PORTAL_NOT_CONNECTED;
	}

	public BlockPositionDim getEntityPortalForId(int id) {
		return entityPortals.get(id);
	}

	public boolean removeEntityPortal(int id) {
		if (entityPortals.remove(id) != null) {
			markDirty();
			return true;
		}
		return false;
	}

	public static PortalManager getInstance() {
		return ModMiningDimension.instance.portalManager;
	}
}
