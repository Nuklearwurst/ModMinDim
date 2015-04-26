package com.fravokados.mindim.command;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.util.TeleportUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
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
            ((EntityPlayer) sender).travelToDimension(ModMiningDimension.dimensionId);

            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            if(((EntityPlayerMP) sender).dimension == ModMiningDimension.dimensionId) {
	            TeleportUtils.transferPlayerToDimension((EntityPlayerMP) sender, 0);
            } else {
	            TeleportUtils.transferPlayerToDimension((EntityPlayerMP) sender, ModMiningDimension.dimensionId);
            }
        }
    }
}
