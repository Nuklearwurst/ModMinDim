package com.fravokados.mindim.item;

import com.fravokados.mindim.lib.Strings;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class ItemBlockPortalFrame extends ItemBlock {

	public ItemBlockPortalFrame(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		switch (stack.getItemDamage()) {
			case 0:
				return Strings.Block.portalMachineController;
			case 1:
				return Strings.Block.portalMachineFrame;
		}
		return super.getUnlocalizedName(stack);
	}
}
