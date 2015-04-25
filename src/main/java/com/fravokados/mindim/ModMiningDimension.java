package com.fravokados.mindim;

import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.command.CommandEnterDimension;
import com.fravokados.mindim.dimension.WorldProviderMiningDimension;
import com.fravokados.mindim.event.ModEventHandler;
import com.fravokados.mindim.lib.Reference;
import com.fravokados.mindim.portal.PortalManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class ModMiningDimension {

    public Block portalFrame;
    public Block portalBlock;

    public static int dimensionId = 20;

    public PortalManager portalManager;

    @Mod.Instance(value = Reference.MOD_ID)
    public static ModMiningDimension instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        portalFrame = new BlockPortalFrame();
        GameRegistry.registerBlock(portalFrame, "portalFrame");

        GameRegistry.registerTileEntity(TileEntityPortalControllerEntity.class, "TileEntityPortalControllerEntity");

        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        DimensionManager.registerProviderType(dimensionId, WorldProviderMiningDimension.class, false);
        DimensionManager.registerDimension(dimensionId, dimensionId);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandEnterDimension());
    }
}
