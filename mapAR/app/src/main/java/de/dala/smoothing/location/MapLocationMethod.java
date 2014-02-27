package de.dala.smoothing.location;

import java.util.LinkedList;

import android.location.Location;
import de.dala.common.GPSPoint;
import de.dala.maps.MapViewFragment;

public class MapLocationMethod extends BasicSmoothingLocationMethod {

	@Override
	public String getName() {
		return "FromMap";
	}

	@Override
	public GPSPoint getSmoothingLocationData(LinkedList<Location> locationList) {
		return MapViewFragment.MY_POSITION;
	}

}
