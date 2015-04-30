package com.fravokados.mindim.item;

import com.fravokados.mindim.lib.Strings;
import com.fravokados.mindim.portal.PortalMetrics;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class ItemDestinationCard extends ItemMDMultiType {

	public ItemDestinationCard() {
		super(Strings.Item.destinationCard);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		list.add(new ItemStack(item, 1, 0));
	}

	@Override
	public String getUnlocalizedNameForItem(ItemStack s) {
		return "destinationCard";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
		super.addInformation(stack, player, info, b);
		if(stack.stackTagCompound != null) {
			if(stack.stackTagCompound.hasKey("destinationPortalType") && stack.stackTagCompound.hasKey("destinationPortal")) {
				int type = stack.stackTagCompound.getInteger("destinationPortalType");
				int dest = stack.stackTagCompound.getInteger("destinationPortal");
				//TODO translation
				info.add("Portal Type: " + PortalMetrics.Type.getType(type));
				info.add("Portal Destination: " + dest);
			}
		}
	}
}
