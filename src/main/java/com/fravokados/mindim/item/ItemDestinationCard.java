package com.fravokados.mindim.item;

import com.fravokados.mindim.lib.Strings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class ItemDestinationCard extends ItemMD {

	public ItemDestinationCard() {
		super(Strings.Item.destinationCard);
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		list.add(new ItemStack(item, 1, 0));
	}
}
