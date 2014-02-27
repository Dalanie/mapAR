package de.dala.smoothing.sensor;

import java.util.LinkedList;

import de.dala.common.OrientationPoint;

public interface ISmoothingSensorDataMethod {

	String getName();
	
	OrientationPoint getSmoothingOrientationData(
			LinkedList<OrientationPoint> orientationPointList);

}
