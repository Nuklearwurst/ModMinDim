package com.fravokados.mindim.item;

import com.fravokados.techmobs.lib.Textures;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public abstract class ItemMDBlockMultiType extends ItemBlock {

	public ItemMDBlockMultiType(Block block) {
		super(block);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public abstract void getSubItems(Item item, CreativeTabs tabs, List list);

	protected String getUnwrappedUnlocalizedName(String unlocalizedName)
	{
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public String getUnlocalizedName()
	{
		return String.format("item.%s%s", Textures.TEXTURE_PREFIX, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack s) {
		return String.format("item.%s%s", Textures.TEXTURE_PREFIX, getUnwrappedUnlocalizedName(getUnlocalizedNameForItem(s)));
	}

	protected abstract String getUnlocalizedNameForItem(ItemStack stack);

	@Override
	public abstract void registerIcons(IIconRegister register);
}
