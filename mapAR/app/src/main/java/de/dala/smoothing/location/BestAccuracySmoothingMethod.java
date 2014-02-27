package de.dala.smoothing.location;

import java.util.Iterator;
import java.util.LinkedList;

import android.location.Location;
import de.dala.common.GPSPoint;
import de.dala.utilities.GeographicUtils;

/**
 * This method compares every location inside of the list it uses the function,
 * implemented in GeographicUtils, which looks for the timestamp, accuracy and
 * other stuff
 * 
 * @author Daniel Langerenken
 * 
 */
public class BestAccuracySmoothingMethod extends BasicSmoothingLocationMethod {

	@Override
	public String getName() {
		return "BestAccuracy";
	}

	@Override
	public GPSPoint getSmoothingLocationData(LinkedList<Location> locationList) {
		synchronized (locationList) {
			if (locationList == null || locationList.isEmpty()) {
				return super.getSmoothingLocationData(locationList);
			}

			Location bestLocation = null;
			Iterator<Location> i = locationList.iterator();
			while (i.hasNext()) {
				Location location = i.next();
				if (GeographicUtils.isBetterLocation(location, bestLocation)) {
					bestLocation = location;
				}
			}
			return new GPSPoint(bestLocation);
		}
	}
}
