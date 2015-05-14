package com.fravokados.mindim.block.tile;

/**
 * @author Nuklearwurst
 */
public interface IEntityPortalMandatoryComponent extends IEntityPortalComponent {
	void setPortalController(int xCoord, int yCoord, int zCoord);

	short getFacing();
}
