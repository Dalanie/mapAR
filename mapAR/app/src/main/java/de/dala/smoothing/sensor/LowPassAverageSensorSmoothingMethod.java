package de.dala.smoothing.sensor;

import java.util.LinkedList;

import de.dala.common.OrientationPoint;
import de.dala.utilities.MathUtil;

public class LowPassAverageSensorSmoothingMethod extends
		BasicSmoothingSensorMethod {

	private float alpha;
	private OrientationPoint lastPoint;

	public LowPassAverageSensorSmoothingMethod(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public OrientationPoint getSmoothingOrientationData(
			LinkedList<OrientationPoint> orientationPointList) {
		OrientationPoint point = MathUtil.movingAverage2(orientationPointList);
		if (lastPoint == null) {
			lastPoint = point;
		} else {
			lastPoint.bearing = lowPass(point.bearing, lastPoint.bearing, alpha);
			lastPoint.rotation = lowPass(point.rotation, lastPoint.rotation,
					alpha);
			lastPoint.pitch = lowPass(point.pitch, lastPoint.pitch, alpha);
		}
		return lastPoint;
	}

	@Override
	public String getName() {
		return "LP+AV";
	}

}
