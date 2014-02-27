package de.dala.smoothing.location;

import java.util.LinkedList;

import android.location.Location;
import de.dala.common.GPSPoint;

public class LastPositionSmoothingMethod extends BasicSmoothingLocationMethod {

	@Override
	public String getName() {
		return "LastPosition";
	}

	@Override
	public GPSPoint getSmoothingLocationData(LinkedList<Location> locationList) {
		/*
		 * if no location available -> take value from parent
		 */
		if (locationList == null || locationList.isEmpty()) {
			return super.getSmoothingLocationData(locationList);
		}
		/*
		 * return last location
		 */
		return new GPSPoint(locationList.getLast());
	}

}
