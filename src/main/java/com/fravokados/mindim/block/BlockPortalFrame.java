package com.fravokados.mindim.block;

import com.fravokados.mindim.block.tile.TileEntityPortalControllerEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Nuklearwurst
 */
public class BlockPortalFrame extends BlockContainer {

    public BlockPortalFrame() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float j, float k, float l) {
        if(!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if(tile != null && tile instanceof TileEntityPortalControllerEntity) {
                ((TileEntityPortalControllerEntity)tile).onActivated(world, x, y, z, player, side);
            }
        }
        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te != null && te instanceof TileEntityPortalControllerEntity) {
            ((TileEntityPortalControllerEntity) te).onBlockPlaced();
        }
        return super.onBlockPlaced(world, x, y, z, p_149660_5_, p_149660_6_, p_149660_7_, p_149660_8_, p_149660_9_);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityPortalControllerEntity();
    }
}
