package de.dala.coordinate_systems;

import de.dala.common.GPSPoint;
import de.dala.utilities.GeographicUtils;

public class DistancePoint3D extends Point3D {

	public DistancePoint3D(GPSPoint camera, GPSPoint point, boolean isCamera,
			double bearing, DistanceAlgorithm algorithm) {
		super(point);
		if (isCamera) {
			x = camera.altitude;
			y = 0;
			z = 0;
		} else {
			double[] translationXY = GeographicUtils.getDistanceInMeters(point,
                    camera, bearing, algorithm);
			x = 0;
			y = -translationXY[0];
			z = -translationXY[1];
		}
	}

	@Override
	protected void setXYZ() {
		/*
		 * ignore this
		 */
	}

	@Override
	public double getDistanceInMeter(Point3D otherPoint) {
		if (otherPoint instanceof DistancePoint3D) {
			return super.getDistanceInMeter(otherPoint);
		} else {
			/*
			 * wrong coordinate system - return distance of gps positions
			 */
			return getGpsPoint().distanceTo(otherPoint.getGpsPoint());
		}
	}

}
