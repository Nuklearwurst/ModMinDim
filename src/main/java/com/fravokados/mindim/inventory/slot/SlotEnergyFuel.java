package com.fravokados.mindim.inventory.slot;

import com.fravokados.mindim.plugin.EnergyTypes;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class SlotEnergyFuel extends Slot {

	private final EnergyTypes type;

	public SlotEnergyFuel(IInventory inv, int id, int x, int y, EnergyTypes type) {
		super(inv, id, x, y);
		this.type = type;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		switch (type) {
			case IC2:
				return stack.getItem() instanceof IElectricItem && ((IElectricItem) stack.getItem()).canProvideEnergy(stack);
		}
		return true;
	}
}
