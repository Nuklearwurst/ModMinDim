package com.fravokados.mindim.event;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.portal.PortalManager;
import com.fravokados.mindim.util.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

/**
 * @author Nuklearwurst
 */
public class ModEventHandler {

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent evt) {
        if(evt.player.dimension != ModMiningDimension.dimensionId) {
//	        evt.block.getUnlocalizedName()
	        GameRegistry.UniqueIdentifier block = GameRegistry.findUniqueIdentifierFor(evt.block);
	        if(block != null && block.modId.contains("BuildCraft")) {
		        if(block.name.equals("machineBlock") || block.name.equals("miningWellBlock")) {
			        evt.player.addChatComponentMessage(new ChatComponentText("Mmh, that didn't work..."));
			        evt.setCanceled(true);
		        }
	        }
        }
    }

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load evt) {
        if(!evt.world.isRemote && evt.world.provider.dimensionId == 0)
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
