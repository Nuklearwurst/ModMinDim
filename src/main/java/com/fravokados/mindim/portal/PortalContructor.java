package com.fravokados.mindim.portal;

import com.fravokados.mindim.block.tile.*;
import com.fravokados.mindim.configuration.Settings;
import com.fravokados.mindim.util.LogHelper;
import com.fravokados.mindim.util.SimpleObjectReference;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nuklearwurst
 */
public class PortalContructor {

	public enum Result {
		SUCCESS, ERROR_INVALID_STRUCTURE, ERROR_MULTIPLE_CONTROLLERS, ERROR_MISSING_CONTOLLER, ERROR_TO_BIG, ERROR_TO_SMALL, ERROR_OPEN_PORTAL, ERROR_NO_PORTAL_BLOCK, ERROR_UNKNOWN;
	}

	public static Result createPortalMultiBlock(World world, int x, int y, int z) {
		List<IEntityPortalMandatoryComponent> frames = new ArrayList<IEntityPortalMandatoryComponent>();
		return createPortalMultiBlock(world, x, y, z, frames);
	}

	public static Result createPortalMultiBlock(World world, int x, int y, int z, List<IEntityPortalMandatoryComponent> frames) {
		LogHelper.info("Searching for Portal MultiBlock");
		//settings up
		SimpleObjectReference<TileEntityPortalControllerEntity> controller = new SimpleObjectReference<TileEntityPortalControllerEntity>();
		PortalMetrics metrics = new PortalMetrics();

		Result result = createPortalMultiBlock(world, x, y, z, ForgeDirection.UNKNOWN, frames, controller, metrics, new ArrayList<ForgeDirection>());
		if (result != Result.SUCCESS) {
			LogHelper.info("MultiBlock forming failed: " + result);
			return result;
		}
		if (controller.isNull()) {
			LogHelper.info("MultiBlock is missing a controller!");
			return Result.ERROR_MISSING_CONTOLLER;
		}
		if (metrics.smallestDimension() <= Settings.MIN_PORTAL_SIZE) {
			LogHelper.info("MultiBlock to small!");
			return Result.ERROR_TO_SMALL;
		}
		//update controllers
		for (IEntityPortalMandatoryComponent frame : frames) {
			frame.setPortalController(controller.get().xCoord, controller.get().yCoord, controller.get().zCoord);
		}
		metrics.calculateOrigin();
		//update controller
		controller.get().updateMetrics(metrics);

		LogHelper.info("Successfully formed multiblock");
		return Result.SUCCESS;
	}

	private static Result createPortalMultiBlock(World world, int x, int y, int z, ForgeDirection from, List<IEntityPortalMandatoryComponent> frames, SimpleObjectReference<TileEntityPortalControllerEntity> controller, PortalMetrics metrics, List<ForgeDirection> finishedAxis) {
		boolean success = false;
		//first entry
		if (from == ForgeDirection.UNKNOWN) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof IEntityPortalComponent) {
				if (te instanceof IEntityPortalOptionalComponent) {
					//search new starting position
					for (int i = 0; i < 6; i++) {
						ForgeDirection dir = ForgeDirection.getOrientation(i);
						TileEntity st = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
						if (st != null && st instanceof IEntityPortalMandatoryComponent) {
							return createPortalMultiBlock(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, from, frames, controller, metrics, finishedAxis);
						}
					}
					return Result.ERROR_NO_PORTAL_BLOCK;
				} else if (te instanceof IEntityPortalMandatoryComponent) { //add self
					frames.add((IEntityPortalMandatoryComponent) te);
					metrics.addCoord(x, y, z);
				}
			} else {
				return Result.ERROR_UNKNOWN;
			}
		} else {

			Result result = findPortalBockAt(world, x + from.offsetX, y + from.offsetY, z + from.offsetZ, from, frames, controller, metrics, finishedAxis, false);
			if(result == Result.SUCCESS) {
				success = true;
				if(finishedAxis.contains(from)) {
					finishedAxis.add(from);
				}
			} else if (result != Result.ERROR_NO_PORTAL_BLOCK) {
				//backtrace if portal could not be completed in that direction
				return result;
			}

		}
		if (!from.equals(ForgeDirection.UNKNOWN) && !finishedAxis.contains(from)) {
			//finish direction
			finishedAxis.add(from);
		}
		//start search
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir == from || dir.getOpposite() == from) {
				continue;
			}
			Result result = findPortalBockAt(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir, frames, controller, metrics, finishedAxis, true);
			if(result == Result.SUCCESS) {
				success = true;
				if(finishedAxis.contains(from)) {
					finishedAxis.add(from);
				}
			} else if (result != Result.ERROR_NO_PORTAL_BLOCK) {
				//backtrace if portal could not be completed in that direction
				return result;
			}
		}
		return success ? Result.SUCCESS : Result.ERROR_NO_PORTAL_BLOCK;
	}

	private static Result findPortalBockAt(World world, int x, int y, int z, ForgeDirection from, List<IEntityPortalMandatoryComponent> frames, SimpleObjectReference<TileEntityPortalControllerEntity> controller, PortalMetrics metrics, List<ForgeDirection> finishedAxis, boolean isNewAxis) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof IEntityPortalComponent) {
			//check valid position
			if (metrics.isBlockInsideFrame(x, y, z)) {
				return Result.ERROR_INVALID_STRUCTURE;
			}
			//start next step
			if (te instanceof IEntityPortalMandatoryComponent) {
				if (finishedAxis.contains(from) && !metrics.isBlockInside(x, y, z)) {
					return Result.ERROR_INVALID_STRUCTURE;
				}
				if (frames.contains(te)) {
					return Result.SUCCESS;
				}
				frames.add((IEntityPortalMandatoryComponent) te);
				metrics.addCoord(x, y, z);
			} else if (te instanceof TileEntityPortalControllerEntity) {
				if (controller.isNull()) {
					controller.set((TileEntityPortalControllerEntity) te);
				} else {
					if (controller.get() == te) {
						//go on to confirm
					} else {
						return Result.ERROR_MULTIPLE_CONTROLLERS;
					}
				}
			}
			return createPortalMultiBlock(world, x, y, z, from, frames, controller, metrics, finishedAxis);
		}
		return Result.ERROR_NO_PORTAL_BLOCK;
	}

	public static boolean createPortalFromMetrics() {
		//TODO implement portal placer
		return false;
	}
}
