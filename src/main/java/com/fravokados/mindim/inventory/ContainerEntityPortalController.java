package com.fravokados.mindim.inventory;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.inventory.slot.SlotControllerDestinationCard;
import com.fravokados.mindim.inventory.slot.SlotEnergyFuel;
import com.fravokados.mindim.inventory.slot.SlotOutput;
import com.fravokados.mindim.item.ItemDestinationCard;
import com.fravokados.mindim.network.IElementButtonHandler;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Nuklearwurst
 */
public class ContainerEntityPortalController extends Container implements IElementButtonHandler {

	public static final String NETWORK_ID_START = "epc_bstart";
	public static final String NETWORK_ID_STOP = "epc_bstop";

	private final TileEntityPortalControllerEntity te;

	private TileEntityPortalControllerEntity.Error lastError = TileEntityPortalControllerEntity.Error.NO_ERROR;
	private TileEntityPortalControllerEntity.State lastState = TileEntityPortalControllerEntity.State.READY;

	private int lastEnergyStored = 0;
	private int lastMaxEnergyStored = 100000;
	private int lastFlags = 0;


	public ContainerEntityPortalController(InventoryPlayer player, TileEntityPortalControllerEntity te) {
		super();
		this.te = te;

		this.addSlotToContainer(new SlotControllerDestinationCard(te, 0, 55, 80)); //destination card
		this.addSlotToContainer(new SlotEnergyFuel(te, 1, 199, 80, te.getEnergyType())); //battery
		this.addSlotToContainer(new SlotControllerDestinationCard(te, 2, 14, 10)); //input destination card
		this.addSlotToContainer(new SlotOutput(te, 3, 14, 68)); //output destination card

		//Player inventory
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + 47 + j * 18, 109 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player, i, 8 + 47 + i * 18, 167));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		//TODO update energy
		super.addCraftingToCrafters(crafter);
		crafter.sendProgressBarUpdate(this, 0, te.getId());
		crafter.sendProgressBarUpdate(this, 1, te.getState().ordinal());
		crafter.sendProgressBarUpdate(this, 2, te.getLastError().ordinal());
		crafter.sendProgressBarUpdate(this, 3, (int) te.getEnergyStored());
		crafter.sendProgressBarUpdate(this, 4, te.getMaxEnergyStored());
		crafter.sendProgressBarUpdate(this, 5, te.getUpgradeTrackerFlags());
	}

	@Override
	public void detectAndSendChanges() {
		//TODO update energy
		super.detectAndSendChanges();
		for (Object crafter : this.crafters) {
			ICrafting icrafting = (ICrafting) crafter;
			if (this.lastState != te.getState()) {
				icrafting.sendProgressBarUpdate(this, 1, te.getState().ordinal());
			}
			if (this.lastError != te.getLastError()) {
				icrafting.sendProgressBarUpdate(this, 2, te.getLastError().ordinal());
			}
			if (this.lastEnergyStored != (int) te.getEnergyStored()) {
				icrafting.sendProgressBarUpdate(this, 3, (int) te.getEnergyStored());
			}
			if (this.lastMaxEnergyStored != te.getMaxEnergyStored()) {
				icrafting.sendProgressBarUpdate(this, 4, te.getMaxEnergyStored());
			}
			if(this.lastFlags != te.getUpgradeTrackerFlags()) {
				icrafting.sendProgressBarUpdate(this, 5, te.getUpgradeTrackerFlags());
			}
		}
		this.lastState = te.getState();
		this.lastError = te.getLastError();
		this.lastEnergyStored = (int) te.getEnergyStored();
		this.lastMaxEnergyStored = te.getMaxEnergyStored();
		this.lastFlags = te.getUpgradeTrackerFlags();
	}


	@Override
	public void updateProgressBar(int index, int value) {
		super.updateProgressBar(index, value);
		switch (index) {
			case 0:
				this.te.setId(value);
				break;
			case 1:
				this.te.setState(TileEntityPortalControllerEntity.State.values()[value]);
				break;
			case 2:
				this.te.setLastError(TileEntityPortalControllerEntity.Error.values()[value]);
				break;
			case 3:
				this.te.getEnergyStorage().setEnergyStored(value);
				break;
			case 4:
				this.te.getEnergyStorage().setCapacity(value);
				break;
			case 5:
				this.te.setUpgradeTrackerFlags(value);
				break;
		}
	}


	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		ItemStack stackCopy = null;
		Slot slot = (Slot) this.inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()) {
			ItemStack stackSlot = slot.getStack();
			stackCopy = stackSlot.copy();

			if (slotId == 3) { //Destination Card Output
				if (!this.mergeItemStack(stackSlot, 4, this.inventorySlots.size(), true)) { //(inverted merge direction)
					return null;
				}
				slot.onSlotChange(stackSlot, stackCopy);
			} else if (slotId < 4) { //all other machine inventory slots
				if (!this.mergeItemStack(stackSlot, 4, this.inventorySlots.size(), false)) {
					return null;
				}
			} else { //from player:
				if (stackSlot.getItem() instanceof IElectricItem && ((IElectricItem) stackSlot.getItem()).canProvideEnergy(stackSlot)) { //TODO generalize to support other energy sources/mods
					//Electric Item
					if (!this.mergeItemStack(stackSlot, 1, 2, false)) {
						return null;
					}
				} else if (stackSlot.getItem() instanceof ItemDestinationCard) {
					//Destination Card
					if (!this.mergeItemStack(stackSlot, 0, 1, false)) {
						return null;
					}
				} else if (slotId >= 4 && slotId < 31) {
					//To Hotbar
					if (!this.mergeItemStack(stackSlot, 31, 40, false)) {
						return null;
					}
				} else if (slotId >= 31 && slotId < 40 && !this.mergeItemStack(stackSlot, 4, 31, false)) {
					//From hotbar to player main inventory
					return null;
				}
			}

			if (stackSlot.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (stackSlot.stackSize == stackCopy.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, stackSlot);
		}

		return stackCopy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.te.isUseableByPlayer(player);
	}

	@Override
	public void handleElementButtonClick(String elementName, int mouseButton) {
		if (elementName != null) {
			if (elementName.equals(NETWORK_ID_START)) {
				te.handleStartButton(this);
			} else if (elementName.equals(NETWORK_ID_STOP)) {
				te.handleStopButton(this);
			}

		}
	}
}
