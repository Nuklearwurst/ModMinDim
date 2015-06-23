package com.fravokados.mindim.block;

import com.fravokados.mindim.block.tile.TileEntityPortal;
import com.fravokados.mindim.lib.Strings;
import com.fravokados.mindim.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Portal Block
 * @author Nuklearwurst
 */
public class BlockPortalMinDim extends BlockMD implements ITileEntityProvider{


	public BlockPortalMinDim() {
		super(Material.portal, Strings.Block.portal);
		this.setCreativeTab(null);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityPortal) {
			short facing = ((TileEntityPortal) te).getFacing();
			return side == facing || ForgeDirection.getOrientation(side) == ForgeDirection.getOrientation(facing).getOpposite();
		} else {
			return true;
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
		if(!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityPortal) {
				((TileEntityPortal) te).onEntityEnterPortal(entity);
			} else {
				LogHelper.error("Invalid Portal!");
				removePortalAndSurroundingPortals(world, x, y, z);
			}
		}
	}

	public void removePortalAndSurroundingPortals(World world, int x, int y, int z) {
		world.setBlockToAir(x, y, z);
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			Block b = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if(b != null && b instanceof BlockPortalMinDim) {
				((BlockPortalMinDim) b).removePortalAndSurroundingPortals(world, x, y, z);
			}
		}
	}



	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityPortal();
	}

	public static void placePortalInWorld(World world, int x, int y, int z, int cx, int cy, int cz) {
		world.setBlock(x, y, z, ModBlocks.blockPortalBlock, 0, 3);
		TileEntityPortal te = (TileEntityPortal) world.getTileEntity(x, y, z);
		te.setPortalController(cx, cy, cz);
	}

	@Override
	public int quantityDropped(Random r) {
		return 0;
	}
}
