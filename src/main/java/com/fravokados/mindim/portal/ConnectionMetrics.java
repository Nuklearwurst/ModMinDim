package com.fravokados.mindim.portal;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * normed cooedinate system
 * @author Nuklearwurst
 */
public class ConnectionMetrics {

	private double x = 0;
	private double y = 0;

	private ForgeDirection top;
	private ForgeDirection front;

	public ConnectionMetrics(PortalMetrics metrics) {
		//TODO: working relative coordinate transformation
		top = metrics.top;
		front = metrics.front;
		if(metrics.maxX - metrics.minX == 0) {
			x = metrics.originY * top.offsetZ;
			y = metrics.originY * top.offsetY;
			x += metrics.originZ * top.getRotation(front).offsetZ;
			y += metrics.originZ * top.getRotation(front).offsetY;
		} else if(metrics.maxY - metrics.minY == 0) {
			x = metrics.originX * top.offsetX;
			y = metrics.originZ * top.offsetZ;
			x += top.getRotation(front).offsetX;
			y += top.getRotation(front).offsetZ;
		} else if(metrics.maxZ - metrics.minZ == 0) {
			x = metrics.originX * top.offsetX;
			y = metrics.originY * top.offsetY;
			x += top.getRotation(front).offsetX;
			y += top.getRotation(front).offsetY;
		}
	}
}
