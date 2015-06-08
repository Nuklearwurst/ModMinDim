package com.fravokados.mindim;

import com.fravokados.mindim.block.BlockMD;
import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.block.BlockPortalMinDim;
import com.fravokados.mindim.block.tile.TileEntityPortal;
import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.block.tile.TileEntityPortalFrame;
import com.fravokados.mindim.command.CommandEnterDimension;
import com.fravokados.mindim.common.CommonProxy;
import com.fravokados.mindim.common.GuiHandler;
import com.fravokados.mindim.configuration.ConfigHandler;
import com.fravokados.mindim.dimension.WorldProviderMiningDimension;
import com.fravokados.mindim.event.ModEventHandler;
import com.fravokados.mindim.item.ItemBlockPortalFrame;
import com.fravokados.mindim.item.ItemDestinationCard;
import com.fravokados.mindim.item.ItemMD;
import com.fravokados.mindim.item.ItemMindDimUpgrade;
import com.fravokados.mindim.lib.Reference;
import com.fravokados.mindim.lib.Strings;
import com.fravokados.mindim.network.ModNetworkManager;
import com.fravokados.mindim.portal.PortalManager;
import com.fravokados.mindim.recipes.RecipeManager;
import com.fravokados.mindim.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;


@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, guiFactory = Reference.GUI_FACTORY, dependencies = Reference.MOD_DEPENDENCIES, canBeDeactivated=false)
public class ModMiningDimension {

    public BlockMD blockPortalFrame;
    public BlockMD blockPortalBlock;

	public ItemMD itemDestinationCard;
    public ItemMD itemUpgrade;

    public static int dimensionId = 20;

    public PortalManager portalManager;

	public static ConfigHandler config;

	@SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_SERVER, modId = Reference.MOD_ID)
	public static CommonProxy proxy;

    @Mod.Instance(value = Reference.MOD_ID)
    public static ModMiningDimension instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
	    //load config
	    config = new ConfigHandler(evt.getSuggestedConfigurationFile());
	    config.load(true);

        blockPortalFrame = new BlockPortalFrame();
        GameRegistry.registerBlock(blockPortalFrame, ItemBlockPortalFrame.class, Strings.Block.portalMachineBase);
        blockPortalBlock = new BlockPortalMinDim();
	    GameRegistry.registerBlock(blockPortalBlock, Strings.Block.portal);

	    itemDestinationCard = new ItemDestinationCard();
	    GameRegistry.registerItem(itemDestinationCard, Strings.Item.destinationCard);
        itemUpgrade = new ItemMindDimUpgrade();
        GameRegistry.registerItem(itemUpgrade, Strings.Item.upgrade);

        GameRegistry.registerTileEntity(TileEntityPortalControllerEntity.class, Strings.TileEntity.TILE_ENTITY_PORTAL_CONTROLLER_ENTITY);
	    GameRegistry.registerTileEntity(TileEntityPortal.class, Strings.TileEntity.TILE_ENTITY_PORTAL);
	    GameRegistry.registerTileEntity(TileEntityPortalFrame.class, Strings.TileEntity.TILE_ENTITY_PORTAL_FRAME);

        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
	    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        DimensionManager.registerProviderType(dimensionId, WorldProviderMiningDimension.class, false);
        DimensionManager.registerDimension(dimensionId, dimensionId);

	    ModNetworkManager.init();

        RecipeManager.initRecipes();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        LogHelper.info("Mod " + Reference.MOD_NAME + " version: " + Reference.MOD_VERSION + " has successfully loaded.");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandEnterDimension());
    }
}
