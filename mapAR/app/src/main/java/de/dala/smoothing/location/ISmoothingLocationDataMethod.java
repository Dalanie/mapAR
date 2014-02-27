package de.dala.smoothing.location;

import java.util.LinkedList;

import android.location.Location;
import de.dala.common.GPSPoint;

public interface ISmoothingLocationDataMethod {
	String getName();
	GPSPoint getSmoothingLocationData(
			LinkedList<Location> locationList);
}
