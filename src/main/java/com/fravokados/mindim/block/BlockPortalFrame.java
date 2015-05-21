package com.fravokados.mindim.block;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import com.fravokados.mindim.block.tile.TileEntityPortalFrame;
import com.fravokados.mindim.lib.GUIIDs;
import com.fravokados.mindim.lib.Textures;
import com.fravokados.mindim.portal.PortalManager;
import com.fravokados.mindim.util.RotationUtils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Nuklearwurst
 */
public class BlockPortalFrame extends BlockMD implements ITileEntityProvider {

	public static final int META_FRAME_ENTITY = 0;
	public static final int META_CONTROLLER_ENTITY = 1;

	private IIcon iconFrame;
	private IIcon iconController;

	public BlockPortalFrame() {
		super(Material.rock);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float j, float k, float l) {
		switch (world.getBlockMetadata(x, y, z)) {
			case META_CONTROLLER_ENTITY:
				player.openGui(ModMiningDimension.instance, GUIIDs.ENTITY_PORTAL_CONTROLLER, world, x, y, z);
				return true;
		}
		return false;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		super.onBlockPreDestroy(world, x, y, z, meta);
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityPortalControllerEntity) {
			PortalManager.getInstance().removeEntityPortal(((TileEntityPortalControllerEntity) te).getId());
		}
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null) {
				if (te instanceof IBlockPlacedListener) {
					((IBlockPlacedListener) te).onBlockPostPlaced(world, x, y, z, meta);
				}
			}
		}
		super.onPostBlockPlaced(world, x, y, z, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta) {
			case META_CONTROLLER_ENTITY:
				return new TileEntityPortalControllerEntity();
			case META_FRAME_ENTITY:
				return new TileEntityPortalFrame();
		}
		return null;
	}

	@SuppressWarnings(value = {"unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconFrame = iconRegister.registerIcon(Textures.BLOCK_PORTAL_FRAME);
		iconController = iconRegister.registerIcon(Textures.BLOCK_PORTAL_CONTROLLER);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		switch (meta) {
			case META_FRAME_ENTITY:
				return iconFrame;
			case META_CONTROLLER_ENTITY:
				return iconController;
		}
		return iconFrame;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityPortalControllerEntity) {
			short facing = ((IFacingSix) te).getFacing();
			if(side == facing) {
				return iconController;
			} else {
				return iconFrame;
			}
		}
		return super.getIcon(world, x, y, z, side);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityLivingBase, stack);
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IFacingSix) {
			RotationUtils.updateFacing((IFacingSix) te, entityLivingBase, x, y, z);
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
	}
}
