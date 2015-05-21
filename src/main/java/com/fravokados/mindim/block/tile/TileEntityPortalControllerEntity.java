package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.BlockPortalFrame;
import com.fravokados.mindim.block.IBlockPlacedListener;
import com.fravokados.mindim.block.IFacingSix;
import com.fravokados.mindim.block.tile.energy.EnergyStorage;
import com.fravokados.mindim.configuration.Settings;
import com.fravokados.mindim.inventory.ContainerEntityPortalController;
import com.fravokados.mindim.item.ItemDestinationCard;
import com.fravokados.mindim.plugin.EnergyTypes;
import com.fravokados.mindim.portal.BlockPositionDim;
import com.fravokados.mindim.portal.PortalContructor;
import com.fravokados.mindim.portal.PortalManager;
import com.fravokados.mindim.portal.PortalMetrics;
import com.fravokados.mindim.util.ItemUtils;
import com.fravokados.mindim.util.LogHelper;
import com.fravokados.techmobs.inventory.InventoryUpgrade;
import com.fravokados.techmobs.upgrade.IUpgradable;
import com.fravokados.techmobs.upgrade.IUpgradeInventory;
import com.fravokados.techmobs.upgrade.UpgradeStatCollection;
import com.fravokados.techmobs.upgrade.UpgradeTypes;
import cpw.mods.fml.common.FMLCommonHandler;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalControllerEntity extends TileEntity implements IInventory, IBlockPlacedListener, IEntityPortalOptionalComponent, IFacingSix, IEnergySink, IWrenchable, IUpgradable {

	/**
	 * possible states of the controller
	 */
	public enum State {
		NO_MULTIBLOCK, READY, CONNECTING, OUTGOING_PORTAL, INCOMING_CONNECTION, INCOMING_PORTAL
	}

	/**
	 * posible errors when connecting to a portal
	 */
	public enum Error {
		//TODO: translation
		NO_ERROR("No Error"), INVALID_DESTINATION("Invalid Destination"),
		INVALID_PORTAL_STRUCTURE("Portal Structure is not intact!"), CONNECTION_INTERRUPED("Connection Interrupted!"),
		POWER_FAILURE("Power Failure"), DESTINATION_CHANGED("Destination Changed");

		/**
		 * unlocalized name
		 */
		public final String name;

		Error(String s) {
			this.name = s;
		}
	}

	/** flag indicating that the controller is able to disconnect from incoming portals */
	public static final int FLAG_CAN_DISCONNECT_INCOMING = 1;
	/** flag indicating that the controller can reverse the portal direction */
	public static final int FLAG_CAN_REVERSE_PORTAL = 2;

	/**
	 * Portal id
	 */
	private int id = PortalManager.PORTAL_NOT_CONNECTED;

	/**
	 * Controller name
	 */
	private String name = null; //TODO: Controller naming

	/**
	 * Controller main inventory
	 */
	private ItemStack[] inventory = new ItemStack[getSizeInventory()];

	/**
	 * last portal metrics
	 */
	private PortalMetrics metrics;
	/**
	 * current destination (if open portal)
	 */
	private int portalDestination = PortalManager.PORTAL_NOT_CONNECTED;

	/**
	 * block facing
	 */
	private short facing = 0;

	/**
	 * controller state
	 */
	private State state = State.READY;
	private Error lastError = Error.NO_ERROR;

	/**
	 * current progress
	 */
	private int tick = 0;

	/**
	 * gets set to true on first world tick, used for energy initialization
	 */
	private boolean init = false;

	/**
	 * EnergyType of this block
	 */
	private EnergyTypes energyType = EnergyTypes.IC2; //TODO proper initialization of this value and support of different energy mods
	/**
	 * Energy Storage
	 */
	private EnergyStorage energy = new EnergyStorage(100000);

	/**
	 * Upgrades
	 * TODO: support different upgrade inventory sizes
	 */
	private InventoryUpgrade upgrades = new InventoryUpgrade(9);

	/**
	 * used to determine wich upgrades are installed
	 */
	private int upgradeTrackerFlags = 0;


	public void createPortal() {
		if (id == PortalManager.PORTAL_NOT_CONNECTED) {
			id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
		}
		if (getDestination() == PortalManager.PORTAL_MINING_DIMENSION) {
			int dest = ModMiningDimension.instance.portalManager.createPortal(id, null, this);
			if (dest >= 0)
				setDest(dest);
		}
	}

	public boolean openPortal() {
		return metrics != null && metrics.placePortalsInsideFrame(worldObj, xCoord, yCoord, zCoord);
	}


	@Override
	public IUpgradeInventory getUpgradeInventory() {
		return upgrades;
	}

	@Override
	public void updateUpgradeInformation() {
		UpgradeStatCollection col = UpgradeStatCollection.getUpgradeStatsFromDefinitions(upgrades.getUpgrades());
		energy.setCapacity(100000 + col.getInt(UpgradeTypes.ENERGY_STORAGE.id, 0));
		upgradeTrackerFlags = 0;
		if(col.hasKey(UpgradeTypes.DISCONNECT_INCOMING)) {
			upgradeTrackerFlags += FLAG_CAN_DISCONNECT_INCOMING;
		}
		if(col.hasKey(UpgradeTypes.REVERSE_DIRECTION)) {
			upgradeTrackerFlags += FLAG_CAN_REVERSE_PORTAL;
		}
	}

	public int getUpgradeTrackerFlags() {
		return upgradeTrackerFlags;
	}

	public void setUpgradeTrackerFlags(int upgradeTrackerFlags) {
		this.upgradeTrackerFlags = upgradeTrackerFlags;
	}

	public PortalMetrics getMetrics() {
		return metrics;
	}

	public State getState() {
		return state;
	}

	public Error getLastError() {
		return lastError;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setLastError(Error lastError) {
		this.lastError = lastError;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EnergyTypes getEnergyType() {
		return energyType;
	}

	/**
	 * reads the destination card
	 *
	 * @return
	 */
	public int getDestination() {
		if (inventory[0] == null) {
			return PortalManager.PORTAL_NOT_CONNECTED;
		}
		if (inventory[0].getItem() instanceof ItemDestinationCard) {
			if (inventory[0].getItemDamage() == ItemDestinationCard.META_MIN_DIM) {
				return PortalManager.PORTAL_MINING_DIMENSION;
			} else if (inventory[0].stackTagCompound != null && inventory[0].stackTagCompound.hasKey("destinationPortalType") && inventory[0].stackTagCompound.hasKey("destinationPortal")) {
				if (inventory[0].stackTagCompound.getInteger("destinationPortalType") == PortalMetrics.Type.ENTITY_PORTAL.ordinal()) {
					return inventory[0].stackTagCompound.getInteger("destinationPortal");
				} else {
					return PortalManager.PORTAL_WRONG_TYPE;
				}
			}
		}
		return PortalManager.PORTAL_INVALID_ITEM;
	}

	/**
	 * teleports an entity to the current destination
	 *
	 * @param entity
	 */
	public void teleportEntity(Entity entity) {
		if (state == State.OUTGOING_PORTAL && metrics != null) {
			if (metrics.isEntityInsidePortal(entity, 1)) {
				if (!ModMiningDimension.instance.portalManager.teleportEntityToEntityPortal(entity, getDestination(), id, metrics)) {
					state = State.READY;
					lastError = Error.CONNECTION_INTERRUPED;
					collapseWholePortal();
				}
			}
		} else if (state != State.INCOMING_PORTAL) {
			//invalid state, close portal and continue as usual
			collapseWholePortal();
			state = State.READY;
			lastError = Error.CONNECTION_INTERRUPED;
			LogHelper.warn("Invalid portal found, destroying... (" + id + ", dest.:" + portalDestination + ")");
		}
	}

	/**
	 * called on Block#onBlockPostPlaced()
	 * registeres portal
	 */
	@Override
	public void onBlockPostPlaced(World world, int x, int y, int z, int meta) {
		id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
		PortalContructor.createPortalMultiBlock(world, x, y, z);
	}

	/**
	 * creates a destination card!
	 * FIXME: remove, portal should not spawn with connection card
	 *
	 * @param dest
	 */
	@Deprecated
	public void setDest(int dest) {
		ItemStack card = new ItemStack(ModMiningDimension.instance.itemDestinationCard);
		NBTTagCompound nbt = ItemUtils.getNBTTagCompound(card);
		nbt.setInteger("destinationPortalType", PortalMetrics.Type.ENTITY_PORTAL.ordinal());
		nbt.setInteger("destinationPortal", dest);
		inventory[0] = card;
		markDirty();
	}

	/**
	 * @return true if portal is open
	 */
	public boolean isActive() {
		return state == State.INCOMING_PORTAL || state == State.OUTGOING_PORTAL;
	}

	public void updateMetrics(PortalMetrics metrics) {
		this.metrics = metrics;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		//Do first tick initialization
		if (!init) {
			init = true;
			if (state != State.INCOMING_PORTAL && state != State.OUTGOING_PORTAL) {
				collapseWholePortal();//reset portals
			}
			if (energyType == EnergyTypes.IC2) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}
		}
		//Write Destination Cards (Side GUI)
		if (id > -1 && inventory[2] != null && inventory[3] == null) {
			if (inventory[2].getItem() instanceof ItemDestinationCard && inventory[2].getItemDamage() != ItemDestinationCard.META_MIN_DIM) {
				inventory[3] = inventory[2];
				inventory[2] = null;
				ItemDestinationCard.writeDestination(inventory[3], id);
				markDirty();
			}
		}
		//Connect Portal
		if (state == State.CONNECTING) {
			if (tick == 0) { //update destination portal
				portalDestination = getDestination();
				if (portalDestination >= 0) {
					BlockPositionDim pos = PortalManager.getInstance().getEntityPortalForId(portalDestination);
					if (pos == null || portalDestination == id) { //invalid portal
						state = State.READY;
						lastError = Error.INVALID_DESTINATION;
					} else {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						WorldServer world = server.worldServerForDimension(pos.dimension);
						TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
						if (te != null && te instanceof TileEntityPortalControllerEntity) {
							//inform target of our connection
							((TileEntityPortalControllerEntity) te).setState(State.INCOMING_CONNECTION);
						} else { //invalid controller
							LogHelper.warn("Could not find registered controller with id: " + portalDestination);
							state = State.READY;
							lastError = Error.CONNECTION_INTERRUPED;
						}
					}
				}
				tick++;
			} else if (tick >= Settings.PORTAL_CONNECTION_TIME) { //Do connection
				tick = 0;
				int oldDestination = portalDestination;
				portalDestination = getDestination();
				//check destination
				if (oldDestination != portalDestination) {
					state = State.READY;
					lastError = Error.DESTINATION_CHANGED;
				} else {
					//create portal if necessary
					if (portalDestination == PortalManager.PORTAL_MINING_DIMENSION) {
						int count = ItemUtils.getNBTTagCompound(inventory[0]).getInteger("frame_blocks");
						if (count >= metrics.getFrameBlockCount()) {
							portalDestination = PortalManager.getInstance().createPortal(id, metrics, this);
							if (portalDestination >= 0) {
								//create destination card
								inventory[0] = ItemDestinationCard.fromDestination(portalDestination);
							}
						}
					}
					if (portalDestination >= 0) { //if destination is valid
						BlockPositionDim pos = PortalManager.getInstance().getEntityPortalForId(portalDestination);
						if (pos == null) { //invalid destination (Not found)
							state = State.READY;
							lastError = Error.INVALID_DESTINATION;
						} else {
							//open portal
							if (!openPortal()) { //invalid structure
								state = State.READY;
								lastError = Error.INVALID_PORTAL_STRUCTURE;
							} else {
								//update controllers
								MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
								WorldServer world = server.worldServerForDimension(pos.dimension);
								TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
								if (te != null && te instanceof TileEntityPortalControllerEntity && ((TileEntityPortalControllerEntity) te).openPortal()) {
									if (energy.useEnergy(10000)) { //use initial energy
										//update controller states
										((TileEntityPortalControllerEntity) te).setState(State.INCOMING_PORTAL);
										state = State.OUTGOING_PORTAL;
										lastError = Error.NO_ERROR;
									} else {
										//reset destination if power fails
										((TileEntityPortalControllerEntity) te).setState(State.READY);
										state = State.READY;
										lastError = Error.POWER_FAILURE;
									}
								} else { //invalid portal (Invalid TE or Destination has invalid structure [portal creation failed])
									LogHelper.warn("Error opening portal to: " + portalDestination + " with controller: " + te);
									state = State.READY;
									lastError = Error.CONNECTION_INTERRUPED;
								}
							}
						}
					} else {
						//connection failed (invalid destination card or failed creating portal)
						state = State.READY;
						lastError = Error.INVALID_DESTINATION;
					}
				}
			} else {
				//update progress
				tick++;
			}
		} else if(state == State.OUTGOING_PORTAL) {
			if(tick >= Settings.MAX_PORTAL_CONNECTION_LENGTH) {
				closePortal();
				tick = 0;
			} else {
				tick++;
			}
		}
		//Use Energy
		if (state == State.CONNECTING || state == State.OUTGOING_PORTAL) {
			if (!energy.useEnergy(Settings.ENERGY_USAGE)) {
				closePortal();
				lastError = Error.POWER_FAILURE;
			}
		}
		//recharge energy
		if (inventory[1] != null) {
			if (energyType == EnergyTypes.IC2) {
				if (inventory[1].getItem() instanceof IElectricItem) {
					energy.receiveEnergy(ElectricItem.manager.discharge(inventory[1], getDemandedEnergy(), getSinkTier(), false, true, false), false);
				}
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}

	/**
	 * Unloads the IC2 Energy Sink
	 */
	@Override
	public void onChunkUnload() {
		if (init && energyType == EnergyTypes.IC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			init = false;
		}
	}

	public boolean canConnectTo(TileEntityPortalControllerEntity e) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < inventory.length) {
			return inventory[slot];
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (this.inventory[slot] != null) {
			ItemStack itemstack;

			if (this.inventory[slot].stackSize <= amount) {
				itemstack = this.inventory[slot];
				this.inventory[slot] = null;
				return itemstack;
			} else {
				itemstack = this.inventory[slot].splitStack(amount);

				if (this.inventory[slot].stackSize == 0) {
					this.inventory[slot] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.inventory[slot] != null) {
			ItemStack itemstack = this.inventory[slot];
			this.inventory[slot] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		this.inventory[slot] = itemStack;

		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
			itemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "Portal Controller";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		//TODO improve slot validation
		return slot != 3;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		NBTTagList nbttaglist = nbt.getTagList("Items", 10); //10 is compound type
		this.inventory = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < this.inventory.length) {
				this.inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		if (nbt.hasKey("name")) {
			name = nbt.getString("name");
		}
		id = nbt.getInteger("PortalID");
		facing = nbt.getShort("facing");
		if (nbt.hasKey("metrics")) {
			metrics = PortalMetrics.getMetricsFromNBT(nbt.getCompoundTag("metrics"));
		}
		upgrades.readFromNBT(nbt.getCompoundTag("Upgrades"));
		energyType = EnergyTypes.readFromNBT(nbt);
		energy.readFromNBT(nbt);
		updateUpgradeInformation();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				this.inventory[i].writeToNBT(tag);
				nbttaglist.appendTag(tag);
			}
		}
		nbt.setTag("Items", nbttaglist);
		if (hasCustomInventoryName()) {
			nbt.setString("name", name);
		}
		nbt.setInteger("PortalID", id);
		nbt.setShort("facing", facing);
		if (metrics != null) {
			NBTTagCompound metricsTag = new NBTTagCompound();
			metrics.writeToNBT(metricsTag);
			nbt.setTag("metrics", metricsTag);
		}
		nbt.setTag("Upgrades", upgrades.writeToNBT(new NBTTagCompound()));
		energyType.writeToNBT(nbt);
		energy.writeToNBT(nbt);
	}

	/**
	 * connects portal with destination
	 */
	public void initializeConnection() {
		//update state and tick
		state = State.CONNECTING;
		lastError = Error.NO_ERROR;
		tick = 0;
		//register portal and log warning
		if (id == PortalManager.PORTAL_NOT_CONNECTED) {
			LogHelper.warn("Invalid Controller found!");
			LogHelper.warn((hasCustomInventoryName() ? "Unnamed Controller" : ("Controller " + name)) + " @dim: " + worldObj.provider.dimensionId + ", pos: " + xCoord + "; " + yCoord + "; " + zCoord + " has no valid id. Registering...");
			id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
		}
	}

	@SuppressWarnings(value = "unchecked")
	public void handleStartButton(ContainerEntityPortalController containerEntityPortalController) {
		switch (state) {
			case READY:
				initializeConnection();
		}
	}

	public void handleStopButton(ContainerEntityPortalController containerEntityPortalController) {
		switch (state) {
			case OUTGOING_PORTAL:
			case INCOMING_PORTAL: //For now you can disconnect manually TODO: require an upgrade in order to disconnect from incoming portal
				closePortal();
				break;
			case CONNECTING:
				state = State.READY;
				break;

		}
	}

	/**
	 * closes the portal
	 * TODO: better system
	 */
	public void closePortal() {
		//close remote portal if needed
		if(state == State.OUTGOING_PORTAL) {
			BlockPositionDim pos = PortalManager.getInstance().getEntityPortalForId(portalDestination);
			if (pos != null) {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				WorldServer world = server.worldServerForDimension(pos.dimension);
				TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
				if (te != null && te instanceof TileEntityPortalControllerEntity) {
					((TileEntityPortalControllerEntity) te).setState(State.READY);
					((TileEntityPortalControllerEntity) te).collapseWholePortal();
				}
			}
		}
		//reset state
		state = State.READY;
		//remove portal
		if (metrics != null) {
			metrics.removePortalsInsideFrame(worldObj);
		}
	}

	public void collapseWholePortal() {
		if (metrics != null) {
			metrics.removePortalsInsideFrame(worldObj);
		}
	}

	@Override
	public void setFacing(short b) {
		facing = b;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return true;
	}

	@Override
	public short getFacing() {
		return facing;
	}


	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(ModMiningDimension.instance.portalFrame, 1, BlockPortalFrame.META_CONTROLLER_ENTITY);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setShort("facing", facing);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();
		if (nbt != null && nbt.hasKey("facing")) {
			int old = facing;
			facing = nbt.getShort("facing");
			if (old != facing) {
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}


	@Override
	public double getDemandedEnergy() {
		return Math.max(0, energy.getMaxEnergyStored() - energy.getEnergyStored());
	}

	@Override
	public int getSinkTier() {
		return 3; //TODO: proper ic2 tiers
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		return amount - energy.receiveEnergy(amount, false);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return energyType == EnergyTypes.IC2;
	}

	public double getEnergyStored() {
		return energy.getEnergyStored();
	}

	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	public EnergyStorage getEnergyStorage() {
		return energy;
	}
}
