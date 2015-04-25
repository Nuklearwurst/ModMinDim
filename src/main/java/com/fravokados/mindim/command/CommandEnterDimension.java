package com.fravokados.mindim.command;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.dimension.TeleporterMiningDimension;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * @author Nuklearwurst
 */
public class CommandEnterDimension extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpw";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "tpw";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] params) {
        if(sender != null && sender instanceof EntityPlayerMP) {
//            ((EntityPlayer) sender).travelToDimension(ModMiningDimension.dimensionId);
// //((EntityPlayerMP) sender).getServerForPlayer()
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            if(((EntityPlayerMP) sender).dimension == ModMiningDimension.dimensionId) {
                server.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) sender, 0, new TeleporterMiningDimension(server.worldServerForDimension(0), -1, null));
            } else {
                server.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP) sender, ModMiningDimension.dimensionId, new TeleporterMiningDimension(server.worldServerForDimension(ModMiningDimension.dimensionId), -1, null));
            }
        }
    }
}
