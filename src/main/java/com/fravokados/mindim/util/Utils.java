package com.fravokados.mindim.util;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.dimension.TeleporterMiningDimension;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * @author Nuklearwurst
 */
public class Utils {

    public static void teleportPlayerToDimension(EntityPlayerMP sender, int dim) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.getConfigurationManager().transferPlayerToDimension(sender, dim, new TeleporterMiningDimension(sender.getServerForPlayer(), -1, null));
    }
}
