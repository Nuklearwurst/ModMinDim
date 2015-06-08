package com.fravokados.mindim.plugin;

import cpw.mods.fml.common.Loader;
import ic2.api.item.IC2Items;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class PluginIC2 {

	public static boolean isItemWrench(ItemStack stack) {
		return Loader.isModLoaded("IC2") && stack != null && (stack.getItem() == IC2Items.getItem("wrench").getItem() || stack.getItem() == IC2Items.getItem("electricWrench").getItem());
	}
}
