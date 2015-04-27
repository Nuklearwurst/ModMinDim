package com.fravokados.mindim.configuration.gui;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.lib.Reference;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

public class GuiModConfigurataion extends GuiConfig {

	public GuiModConfigurataion(GuiScreen parent) {
		super(parent,
				null,
				Reference.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ModMiningDimension.config.config.toString()));
	}


}
