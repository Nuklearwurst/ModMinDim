package com.fravokados.mindim.block;

import com.fravokados.mindim.ModMiningDimension;
import com.fravokados.mindim.block.tile.TileEntityPortal;
import com.fravokados.mindim.lib.Strings;
import com.fravokados.mindim.util.LogHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

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
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
		if(!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityPortal) {
				((TileEntityPortal) te).onEntityEnterPortal(entity);
			} else {
				LogHelper.error("Invalid Portal!");
				world.setBlockToAir(x, y, z);
			}
		}
	}



	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityPortal();
	}

	public static void placePortalInWorld(World world, int x, int y, int z, int cx, int cy, int cz) {
		world.setBlock(x, y, z, ModMiningDimension.instance.portalBlock, 0, 3);
		TileEntityPortal te = (TileEntityPortal) world.getTileEntity(x, y, z);
		te.setPortalController(cx, cy, cz);
	}
}
