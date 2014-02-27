package de.dala.smoothing.sensor;

import java.util.LinkedList;

import de.dala.common.OrientationPoint;
import de.dala.utilities.MathUtil;

public class AverageSensorSmoothingMethod extends BasicSmoothingSensorMethod {

	@Override
	public OrientationPoint getSmoothingOrientationData(
			LinkedList<OrientationPoint> orientationPointList) {
		return MathUtil.movingAverage2(orientationPointList);
	}

	@Override
	public String getName() {
		return "Average";
	}

}
