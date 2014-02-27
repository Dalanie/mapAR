package de.dala.smoothing.sensor;

import java.util.LinkedList;

import de.dala.common.OrientationPoint;

/**
 * based on
 * http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
 */
public class LowPassSensorSmoothingMethod extends BasicSmoothingSensorMethod {

	private float alpha;
	private OrientationPoint lastPoint;

	public LowPassSensorSmoothingMethod(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public OrientationPoint getSmoothingOrientationData(
			LinkedList<OrientationPoint> orientationPointList) {
		if (!orientationPointList.isEmpty()) {
			OrientationPoint point = orientationPointList.getLast();
			if (lastPoint == null) {
				lastPoint = point;
			} else {
				lastPoint.bearing = lowPass(point.bearing, lastPoint.bearing,
						alpha);
				lastPoint.rotation = lowPass(point.rotation,
						lastPoint.rotation, alpha);
				lastPoint.pitch = lowPass(point.pitch, lastPoint.pitch, alpha);
			}
		}
		return lastPoint;
	}

	@Override
	public String getName() {
		return "LowPass";
	}

}
