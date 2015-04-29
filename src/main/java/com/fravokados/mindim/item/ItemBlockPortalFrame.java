package com.fravokados.mindim.item;

import com.fravokados.mindim.lib.Strings;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class ItemBlockPortalFrame extends ItemMDBlockMultiType {

	public ItemBlockPortalFrame(Block block) {
		super(block);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		list.add(new ItemStack(item, 1, 0));
	}


	@Override
	public String getUnlocalizedNameForItem(ItemStack stack) {
		switch (stack.getItemDamage()) {
			case 0:
				return Strings.Block.portalMachineController;
			case 1:
				return Strings.Block.portalMachineFrame;
		}
		return super.getUnlocalizedName(stack);
	}

	@Override
	public void registerIcons(IIconRegister register) {

	}
}
