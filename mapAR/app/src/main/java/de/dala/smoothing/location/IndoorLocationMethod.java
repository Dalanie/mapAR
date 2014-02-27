package de.dala.smoothing.location;

import java.util.LinkedList;

import android.location.Location;
import de.dala.common.GPSPoint;
import de.dala.utilities.SystemProperties;

public class IndoorLocationMethod extends BasicSmoothingLocationMethod {

	@Override
	public String getName() {
		return "Indoor";
	}

	@Override
	public GPSPoint getSmoothingLocationData(LinkedList<Location> locationList) {
		return SystemProperties.INDOOR_POSITION;
	}

}
