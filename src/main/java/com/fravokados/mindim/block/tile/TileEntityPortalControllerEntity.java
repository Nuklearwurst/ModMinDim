package com.fravokados.mindim.block.tile;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.IBlockPlacedListener;
import com.fravokados.mindim.block.IFacingSix;
import com.fravokados.mindim.configuration.Settings;
import com.fravokados.mindim.inventory.ContainerEntityPortalController;
import com.fravokados.mindim.item.ItemDestinationCard;
import com.fravokados.mindim.portal.BlockPositionDim;
import com.fravokados.mindim.portal.PortalContructor;
import com.fravokados.mindim.portal.PortalManager;
import com.fravokados.mindim.portal.PortalMetrics;
import com.fravokados.mindim.util.ItemUtils;
import com.fravokados.mindim.util.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
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

/**
 * @author Nuklearwurst
 */
public class TileEntityPortalControllerEntity extends TileEntity implements IInventory, IBlockPlacedListener, IEntityPortalOptionalComponent, IFacingSix {

	/**
	 * possible states of the controller
	 */
	public enum State {
		NO_MULTIBLOCK, READY, CONNECTING, OUTGOING_PORTAL, INCOMING_CONNECTION, INCOMING_PORTAL;
	}

	/**
	 * posible errors when connecting to a portal
	 */
	public enum Error {
		//TODO: translation
		NO_ERROR("No Error"), INVALID_DESTINATION("Invalid Destination"),
		INVALID_PORTAL_STRUCTURE("Portal Structure is not intact!"), CONNECTION_INTERRUPED("Connection Interrupted!");

		/** unlocalized name */
		public final String name;

		private Error(String s) {
			this.name = s;
		}
	}

	/** Portal id */
	private int id = PortalManager.PORTAL_NOT_CONNECTED;

	/** Controller name */
	private String name = null; //TODO: Controller naming

	/** Controller main inventory */
	private ItemStack[] inventory = new ItemStack[getSizeInventory()];

	/** last portal metrics */
	private PortalMetrics metrics;
	/** current destination (if open portal) */
	private int portalDestination = PortalManager.PORTAL_NOT_CONNECTED;

	/** block facing */
	private byte facing = 0;

	/** controller state */
	private State state = State.READY;
	private Error lastError = Error.NO_ERROR;

	/** current progress */
	private int tick = 0;



	public void createPortal() {
		if (id == PortalManager.PORTAL_NOT_CONNECTED) {
			id = ModMiningDimension.instance.portalManager.registerNewEntityPortal(new BlockPositionDim(this));
		}
		if (getDestination() == PortalManager.PORTAL_MINING_DIMENSION) {
			int dest = ModMiningDimension.instance.portalManager.createPortal(id, null);
			if(dest >= 0)
				setDest(dest);
		}
	}

	public boolean openPortal() {
		if(metrics != null) {
			return metrics.placePortalsInsideFrame(worldObj, xCoord, yCoord, zCoord);
		}
		return false;
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

	/**
	 * reads the destination card
	 * @return
	 */
	public int getDestination() {
		if(inventory[0] == null) {
			return PortalManager.PORTAL_NOT_CONNECTED;
		}
		if(inventory[0].getItem() instanceof ItemDestinationCard) {
			if(inventory[0].stackTagCompound != null && inventory[0].stackTagCompound.hasKey("destinationPortalType") && inventory[0].stackTagCompound.hasKey("destinationPortal")) {
				if(inventory[0].stackTagCompound.getInteger("destinationPortalType") == PortalMetrics.Type.ENTITY_PORTAL.ordinal()) {
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
	 * @param entity
	 */
	public void teleportEntity(Entity entity) {
		if(state == State.OUTGOING_PORTAL && metrics != null && metrics.isEntityInsidePortal(entity, 0)) {
			if(!ModMiningDimension.instance.portalManager.teleportEntityToEntityPortal(entity, getDestination(), id, metrics)) {
				state = State.READY;
				lastError = Error.CONNECTION_INTERRUPED;
				collapseWholePortal();
			}
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
	 * @param dest
	 */
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
		if(worldObj.isRemote) {
			return;
		}
		if(id > -1 && inventory[2] != null && inventory[3] == null) {
			inventory[3] = inventory[2];
			inventory[2] = null;
			NBTTagCompound nbt = ItemUtils.getNBTTagCompound(inventory[3]);
			nbt.setInteger("destinationPortalType", PortalMetrics.Type.ENTITY_PORTAL.ordinal());
			nbt.setInteger("destinationPortal", id);
			markDirty();
		}
		if(state == State.CONNECTING) {
			if(tick >= Settings.PORTAL_CONNECTION_TIME) {
				tick = 0;
				portalDestination = getDestination();
				if(portalDestination == PortalManager.PORTAL_MINING_DIMENSION) {
					portalDestination = PortalManager.getInstance().createPortal(id, metrics);
				}
				//TODO: better portal connection
				if(portalDestination >= 0) {
					BlockPositionDim pos = PortalManager.getInstance().getEntityPortalForId(portalDestination);
					if(pos == null) {
						state = State.READY;
						lastError = Error.INVALID_DESTINATION;
					} else {
						if(!openPortal()) {
							state = State.READY;
							lastError = Error.INVALID_PORTAL_STRUCTURE;
						} else {
							MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
							WorldServer world = server.worldServerForDimension(pos.dimension);
							TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
							if(te != null && te instanceof TileEntityPortalControllerEntity && ((TileEntityPortalControllerEntity) te).openPortal()) {
								((TileEntityPortalControllerEntity) te).setState(State.INCOMING_PORTAL);
								state = State.OUTGOING_PORTAL;
								lastError = Error.NO_ERROR;
							} else {
								state = State.READY;
								lastError = Error.CONNECTION_INTERRUPED;
							}
						}
					}
				} else {
					//connection failed
					state = State.READY;
					lastError = Error.INVALID_DESTINATION;
				}
			} else {
				tick++;
			}
		}
	}

	public boolean canConnectTo(TileEntityPortalControllerEntity e) {
		return  true;
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
	public boolean isItemValidForSlot(int slot, ItemStack p_94041_2_) {
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
		if(nbt.hasKey("name")) {
			name = nbt.getString("name");
		}
		id = nbt.getInteger("PortalID");
		facing = nbt.getByte("facing");
		if(nbt.hasKey("metrics")) {
			metrics = PortalMetrics.getMetricsFromNBT(nbt.getCompoundTag("metrics"));
		}
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
		if(hasCustomInventoryName()) {
			nbt.setString("name", name);
		}
		nbt.setInteger("PortalID", id);
		nbt.setByte("facing", facing);
		if(metrics != null) {
			NBTTagCompound metricsTag = new NBTTagCompound();
			metrics.writeToNBT(metricsTag);
			nbt.setTag("metrics", metricsTag);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	/**
	 * connects portal with destination
	 */
	public void initializeConnection() {
		//update state and tick
		state = State.CONNECTING;
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
				BlockPositionDim pos = PortalManager.getInstance().getEntityPortalForId(portalDestination);
				if(pos != null) {
					MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
					WorldServer world = server.worldServerForDimension(pos.dimension);
					TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
					if(te != null && te instanceof TileEntityPortalControllerEntity) {
						((TileEntityPortalControllerEntity) te).setState(State.READY);
						((TileEntityPortalControllerEntity) te).collapseWholePortal();
					}
				}
				state = State.READY;
				collapseWholePortal();
				break;
			case CONNECTING:
				state = State.READY;
				break;

		}
	}

	public void collapseWholePortal() {
		if(metrics != null) {
			metrics.removePortalsInsideFrame(worldObj);
		}
	}

	@Override
	public void setFacing(byte b) {
		facing = b;
	}

	@Override
	public byte getFacing() {
		return facing;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("facing", facing);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();
		if(nbt != null && nbt.hasKey("facing")) {
			int old = facing;
			facing = nbt.getByte("facing");
			if(old != facing) {
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}
}
