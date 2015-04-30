package com.fravokados.mindim.portal;

/**
 * @author Nuklearwurst
 */
public class PortalMetrics {


	public enum Type {
		ENTITY_PORTAL("portal.entity");

		public String name;

		Type(String name) {
			this.name = name;
		}

		public static String getType(int i) {
			return Type.values()[i].name;
		}
	}

	public double originX;
	public double originY;
	public double originZ;

	public PortalMetrics(double xCoord, double yCoord, double zCoord) {
		this.originX = xCoord;
		this.originY = yCoord;
		this.originZ = zCoord;
	}
}
