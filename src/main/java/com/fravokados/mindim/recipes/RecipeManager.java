package com.fravokados.mindim.recipes;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.item.ItemDestinationCard;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class RecipeManager {

	public static void initRecipes() {
		ItemStack electronicCircuit = IC2Items.getItem("electronicCircuit");
		ItemStack copperWire = IC2Items.getItem("copperCableItem");
		ItemStack machineCase = IC2Items.getItem("machine");
		ItemStack mfe = IC2Items.getItem("mfsUnit");
		ItemStack advancedCircuit = IC2Items.getItem("advancedCircuit");

		GameRegistry.addRecipe(new ItemStack(ModMiningDimension.instance.itemDestinationCard, 1, ItemDestinationCard.META_NORMAL),
				" p ",
				"wcw",
				"ppp",
				Character.valueOf('c'), electronicCircuit,
				Character.valueOf('w'), copperWire,
				Character.valueOf('p'), Items.paper
		);

		GameRegistry.addRecipe(new ItemStack(ModMiningDimension.instance.portalFrame, 1, BlockPortalFrame.META_FRAME_ENTITY),
				"oeo",
				"eme",
				"oeo",
				Character.valueOf('m'), machineCase,
				Character.valueOf('o'), Blocks.obsidian,
				Character.valueOf('e'), Items.ender_pearl
		);

		GameRegistry.addRecipe(new ItemStack(ModMiningDimension.instance.portalFrame, 1, BlockPortalFrame.META_CONTROLLER_ENTITY),
				"ded",
				"ama",
				"sfs",
				Character.valueOf('f'), new ItemStack(ModMiningDimension.instance.portalFrame, 1, BlockPortalFrame.META_FRAME_ENTITY),
				Character.valueOf('m'), mfe,
				Character.valueOf('a'), advancedCircuit,
				Character.valueOf('d'), Items.diamond,
				Character.valueOf('e'), Items.ender_eye,
				Character.valueOf('s'), Blocks.stone
		);

		GameRegistry.addRecipe(new ItemStack(ModMiningDimension.instance.itemDestinationCard, 1, ItemDestinationCard.META_MIN_DIM),
				"ede",
				"gcg",
				"ege",
				Character.valueOf('c'), new ItemStack(ModMiningDimension.instance.portalFrame, 1, BlockPortalFrame.META_CONTROLLER_ENTITY),
				Character.valueOf('d'), new ItemStack(ModMiningDimension.instance.itemDestinationCard, 1, ItemDestinationCard.META_NORMAL),
				Character.valueOf('e'), Items.ender_eye,
				Character.valueOf('g'), Items.diamond
		);
	}
}
