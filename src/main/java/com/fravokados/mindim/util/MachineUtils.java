package com.fravokados.mindim.util;

import com.fravokados.mindim.block.IFacingSix;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

/**
 * @author Nuklearwurst
 */
public class MachineUtils {

	public static void updateFacing(IFacingSix te, EntityLivingBase player, int x, int y, int z) {
		//rotate block
		if (player != null)
		{
			int rotationSegment = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			if (player.rotationPitch >= 65)
			{
				te.setFacing((byte)1);
			}
			else if (player.rotationPitch <= -65)
			{
				te.setFacing((byte)0);
			}
			else
			{
				switch (rotationSegment)
				{
					case 0: te.setFacing((byte) 2); break;
					case 1: te.setFacing((byte) 5); break;
					case 2: te.setFacing((byte) 3); break;
					case 3: te.setFacing((byte) 4); break;
					default:
						te.setFacing((byte) 0); break;
				}
			}
		}
	}
}
