package de.dala.smoothing.sensor;

import java.util.LinkedList;

import de.dala.common.OrientationPoint;
import de.dala.utilities.MathUtil;

/**
 * based on http://fxtrade.oanda.com/lang/de/learn/forex-indicators/weighted-moving-average
 * @author Daniel Langerenken
 *
 */
public class WeightedMovingAverageMethod extends BasicSmoothingSensorMethod {

	@Override
	public String getName() {
		return "WMA";
	}

	@Override
	public OrientationPoint getSmoothingOrientationData(
			LinkedList<OrientationPoint> orientationPointList) {
//		return MathUtil.weightedMovingAverage(orientationPointList);
		return MathUtil.weightedMovingAverage2(orientationPointList);
	}

}
