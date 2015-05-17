package com.fravokados.mindim.item;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.lib.GUIIDs;
import com.fravokados.mindim.lib.Strings;
import com.fravokados.mindim.portal.PortalMetrics;
import com.fravokados.mindim.util.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class ItemDestinationCard extends ItemMDMultiType {

	public static final int META_NORMAL = 0;
	public static final int META_MIN_DIM = 1; //this might get moved to another item

	public ItemDestinationCard() {
		super(Strings.Item.destinationCard);
	}

	@SuppressWarnings(value = {"unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public String getUnlocalizedNameForItem(ItemStack s) {
		if(s.getItemDamage() == META_MIN_DIM) {
			return "destinationCardMindim";
		}
		return "destinationCard";
	}

	@SuppressWarnings(value = {"unchecked"})
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
		super.addInformation(stack, player, info, b);
		if(stack.getItemDamage() != META_MIN_DIM) {
			if (stack.stackTagCompound != null) {
				if (stack.stackTagCompound.hasKey("destinationPortalType") && stack.stackTagCompound.hasKey("destinationPortal")) {
					int type = stack.stackTagCompound.getInteger("destinationPortalType");
					int dest = stack.stackTagCompound.getInteger("destinationPortal");
					//TODO translation
					info.add("Portal Type: " + PortalMetrics.Type.getType(type));
					info.add("Portal Destination: " + dest);
				}
			}
		} else {
			if (stack.stackTagCompound != null) {
				if (stack.stackTagCompound.hasKey("TEST")) {
					info.add("Hi, DASFLHL");
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if(!world.isRemote && itemStack.getItemDamage() == META_MIN_DIM && player.isSneaking()) {
			player.openGui(ModMiningDimension.instance, GUIIDs.DESTINATION_CARD_MIN_DIM, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		return itemStack;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	public static ItemStack fromDestination(int id) {
		return writeDestination(new ItemStack(ModMiningDimension.instance.itemDestinationCard, 1, 0), id);
	}

	public static ItemStack writeDestination(ItemStack stack, int id) {
		NBTTagCompound nbt = ItemUtils.getNBTTagCompound(stack);
		nbt.setInteger("destinationPortalType", PortalMetrics.Type.ENTITY_PORTAL.ordinal());
		nbt.setInteger("destinationPortal", id);
		return stack;
	}
}
