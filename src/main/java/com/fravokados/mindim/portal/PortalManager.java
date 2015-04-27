package com.fravokados.mindim.portal;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.util.TeleportUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nuklearwurst
 */
public class PortalManager extends WorldSavedData {

    private Map<Integer, BlockPositionDim> entityPortals = new HashMap<Integer, BlockPositionDim>();
    private int entityPortalCounter = 0;

    public PortalManager(String s) {
        super(s);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        entityPortals.clear();

        entityPortalCounter = nbt.getInteger("entityPortalCounter");
        int[] keys = nbt.getIntArray("entityPortalKeys");
        for(int i : keys) {
            if(nbt.hasKey("entityPortal_" + i)) {
                BlockPositionDim pos = new BlockPositionDim();
                pos.readFromNBT(nbt.getCompoundTag("entityPortal_" + i));
                entityPortals.put(i, pos);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("entityPortalCounter", entityPortalCounter);
        int[] entityPortalKeys = new int[entityPortals.size()];
        int i = 0;
        for(int j : entityPortals.keySet()) {
            entityPortalKeys[i] = j;
            NBTTagCompound tag = new NBTTagCompound();
            entityPortals.get(j).writeToNBT(tag);
            nbt.setTag("entityPortal_" + i, tag);
            i++;
        }
        nbt.setIntArray("entityPortalKeys", entityPortalKeys);
    }

    public int registerNewEntityPortal(BlockPositionDim pos) {
        entityPortals.put(++entityPortalCounter, pos);
        return entityPortalCounter;
    }

    public boolean entityPortalExists(int portal) {
        return entityPortals.containsKey(portal);
    }

    public void registerEntityPortal(int portal, BlockPositionDim pos) {
        entityPortals.put(portal, pos);
    }


	/**
	 *
	 * @param entity entity to teleport
	 * @param portalId destination portal id
	 * @param metrics used to calculate Entity Position in the destination portal
	 */
    public void teleportEntityToEntityPortal(Entity entity, int portalId, int parent, PortalMetrics metrics) {
	    if(!entityPortalExists(portalId)) {
		    return;
	    }
        if(portalId == -1) {
            return;
        }
        BlockPositionDim pos = getEntityPortalForId(portalId);
        if(pos == null) {
            return;
        }
	    double offsetX = entity.posX - metrics.originX;
	    double offsetY = entity.posY - metrics.originY;
	    double offsetZ = entity.posZ - metrics.originZ;
	    //TODO: update player rotation
		//TODO: update portal boundaries
	    double x = pos.x + offsetX;
	    double y = pos.y + offsetY;
	    double z = pos.z + offsetZ;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if(entity instanceof EntityPlayerMP) {
			if (pos.dimension == entity.dimension) {
				entity.mountEntity(null); //needed?
				((EntityPlayerMP)entity).setPositionAndUpdate(x, y, z);
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

    public int createPortal(int parent, PortalMetrics metrics) {
        BlockPositionDim pos = entityPortals.get(parent);
        if(pos == null) {
            return -1;
        }
        int dim = pos.dimension == ModMiningDimension.dimensionId ? 0 : ModMiningDimension.dimensionId;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        WorldServer worldServer = server.worldServerForDimension(dim);
	    //TODO portal generation
        worldServer.setBlock(pos.x, pos.y, pos.z, ModMiningDimension.instance.portalFrame);
        TileEntity te = worldServer.getTileEntity(pos.x, pos.y, pos.z);
        if(te != null && te instanceof TileEntityPortalControllerEntity) {
            ((TileEntityPortalControllerEntity) te).setDest(parent);
            return ((TileEntityPortalControllerEntity) te).onBlockPlaced();
        }
        return -1;
    }

    public BlockPositionDim getEntityPortalForId(int id) {
        return entityPortals.get(id);
    }

    public static PortalManager getInstance() {
	    return ModMiningDimension.instance.portalManager;
    }
}
