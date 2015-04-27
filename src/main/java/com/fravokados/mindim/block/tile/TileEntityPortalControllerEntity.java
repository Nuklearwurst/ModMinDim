package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.portal.BlockPositionDim;
import com.fravokados.mindim.portal.PortalMetrics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalControllerEntity extends TileEntity {
    private int dest = -1;
    private int id = -1;


    public void onActivated(World world, int x, int y, int z, EntityPlayer player, int side) {
        if(id == -1) {
            id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
        }
        if(dest == -1) {
            dest = ModMiningDimension.instance.portalManager.createPortal(id, null);
        }
        if(dest != -1 && player instanceof EntityPlayerMP) {
            teleportEntity(player);
            return;
        }
        player.addChatComponentMessage(new ChatComponentText("No Destination Found!"));
    }

    public void teleportEntity(Entity entity) {

        PortalMetrics metrics = new PortalMetrics(this.xCoord, this.yCoord, this.zCoord);
        ModMiningDimension.instance.portalManager.teleportEntityToEntityPortal(entity, dest, id, null);
    }

    public int onBlockPlaced() {
        return id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
    }

    public void setDest(int dest) {
        this.dest = dest;
    }
}
