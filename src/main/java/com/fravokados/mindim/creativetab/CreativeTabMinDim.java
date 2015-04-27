package com.fravokados.mindim.creativetab;

import com.fravokados.mindim.lib.Strings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabMinDim {

	public static final CreativeTabs TAB_MD = new CreativeTabs(Strings.CREATIVE_TAB) {
		@Override
		public Item getTabIconItem() {
			return Items.boat;
		}
	};

}
