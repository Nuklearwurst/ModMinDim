package com.fravokados.mindim.event;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.util.LogHelper;
import com.fravokados.mindim.portal.PortalManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

/**
 * @author Nuklearwurst
 */
public class ModEventHandler {

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent evt) {

    }

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load evt) {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer() && evt.world.provider.dimensionId == 0)
        {
            WorldServer world = (WorldServer)evt.world;
            PortalManager saveData = (PortalManager)world.perWorldStorage.loadData(PortalManager.class, "PortalManager");

            if(saveData == null)
            {
                saveData = new PortalManager("PortalManager");
                world.perWorldStorage.setData("PortalManager", saveData);
            }

            if(ModMiningDimension.instance.portalManager != null) {
                LogHelper.error("PortalManager already loaded! Using live version... (This is a programming Error)");
                return;
            }
            ModMiningDimension.instance.portalManager = saveData;
        }
    }
}
