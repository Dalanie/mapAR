package de.dala.coordinate_systems;

import de.dala.common.GPSPoint;

public class LongLatAlt3DPoint extends Point3D {
	private static final double METERS_IN_A_DEGREE = 111000;
	public LongLatAlt3DPoint(double latitude, double longitude, double altitude) {
		super(latitude, longitude, altitude);
	}

	public LongLatAlt3DPoint(GPSPoint point) {
		super(point);
	}

	@Override
	protected void setXYZ() {
		y = latitude * METERS_IN_A_DEGREE;
		x = longitude * Math.cos(latitude) * METERS_IN_A_DEGREE;
		z = altitude;
	}
}
