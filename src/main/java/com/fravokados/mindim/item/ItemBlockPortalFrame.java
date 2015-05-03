package com.fravokados.mindim.item;

import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.lib.Strings;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class ItemBlockPortalFrame extends ItemMDBlockMultiType {

	public ItemBlockPortalFrame(Block block) {
		super(block);
	}


	@Override
	public String getUnlocalizedNameForItem(ItemStack stack) {
		switch (stack.getItemDamage()) {
			case BlockPortalFrame.META_CONTROLLER_ENTITY:
				return Strings.Block.portalMachineController;
			case BlockPortalFrame.META_FRAME_ENTITY:
				return Strings.Block.portalMachineFrame;
		}
		return super.getUnlocalizedName(stack);
	}

//	@Override
//	public void registerIcons(IIconRegister register) {
//
//	}
}
