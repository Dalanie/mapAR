package de.dala.smoothing.sensor;

import java.util.LinkedList;

import de.dala.common.OrientationPoint;
import de.dala.utilities.MathUtil;

/**
 * based on
 * http://fxtrade.oanda.com/lang/de/learn/forex-indicators/weighted-moving
 * -average
 * 
 * @author Daniel Langerenken
 * 
 */
public class LowPassWeightedAverageMethod extends BasicSmoothingSensorMethod {

	private float alpha;
	private OrientationPoint lastPoint;

	public LowPassWeightedAverageMethod(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public String getName() {
		return "LP+WMA";
	}

	@Override
	public OrientationPoint getSmoothingOrientationData(
			LinkedList<OrientationPoint> orientationPointList) {
		OrientationPoint point = MathUtil
				.weightedMovingAverage2(orientationPointList);

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

}
