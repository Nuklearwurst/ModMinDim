package com.fravokados.mindim.util;

import com.fravokados.techmobs.upgrade.IUpgradeInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Nuklearwurst
 */
public class ItemUtils {

	public static NBTTagCompound getNBTTagCompound(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		return stack.stackTagCompound;
	}

	public static void writeUpgradesToItemStack(IUpgradeInventory inventory, ItemStack stack) {

	}

	public static void readUpgradesFromItemStack(IUpgradeInventory inventory, ItemStack stack) {

	}
}
