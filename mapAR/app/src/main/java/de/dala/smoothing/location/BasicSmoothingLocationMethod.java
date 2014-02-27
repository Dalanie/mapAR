package de.dala.smoothing.location;

import java.util.LinkedList;

import android.location.Location;
import de.dala.common.GPSPoint;
import de.dala.utilities.SystemProperties;

public abstract class BasicSmoothingLocationMethod implements
		ISmoothingLocationDataMethod {
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public GPSPoint getSmoothingLocationData(LinkedList<Location> locationList) {
		/*
		 * return indoor-position (fixed gps-value), only called, if child-class explicitely called super
		 */
		return SystemProperties.INDOOR_POSITION;
	}
}
