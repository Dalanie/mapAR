package de.dala.utilities;

import android.location.Location;
import de.dala.common.GPSPoint;
import de.dala.common.GeoScreenPointContainer;
import de.dala.coordinate_systems.DistanceAlgorithm;

/**
 * GeographicUtils for handling with geo-data
 * 
 * @author Cai and Daniel based on GeographicUtils from MapIt and ArViewer
 */
public class GeographicUtils {
	public static final double MAX_LOG_DISTANCE_SHOWN = 3.5;
	/**
	 * the time tag for single filter
	 */
	public static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * http://developer.android.com/guide/topics/location/strategies.html
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	public static boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			/*
			 * A new location is always better than no location
			 */
			return true;
		}

		/*
		 * Check whether the new location fix is newer or older
		 */
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		/*
		 * If it's been more than two minutes since the current location, use
		 * the new location because the user has likely moved
		 */
		if (isSignificantlyNewer) {
			return true;
			/*
			 * If the new location is more than two minutes older, it must be
			 * worse
			 */
		} else if (isSignificantlyOlder) {
			return false;
		}

		/*
		 * Check whether the new location fix is more or less accurate
		 */
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		/*
		 * Check if the old and new location are from the same provider
		 */
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		/*
		 * Determine location quality using a combination of timeliness and
		 * accuracy
		 */
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same
	 */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Calculate the distance from Observer to Observation
	 */
	public static double calculateDistance(double height, double angle) {
		double distance = MathUtil.mul(MathUtil.div(height, 100.0),
				Math.tan(Math.toRadians(angle)));
		return distance;
	}

	public static double[] distanceInXY(GPSPoint point1, GPSPoint point2) {
		double lat1 = Math.toRadians(point1.latitude);
		double lat2 = Math.toRadians(point2.longitude);

		double dLon = Math.toRadians(point2.longitude - point1.longitude);

		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(dLon);
		return new double[] { x, y };
	}

	/**
	 * Calculate the destination gpsPoint from start point, distance and bearing
	 * angle
	 */
	public static GPSPoint destinationPoint(GPSPoint observerPoint,
			double brng, double dist) {
		dist = MathUtil.div(dist, SystemProperties.EARTH_RADIUS);
		brng = Math.toRadians(brng);

		double lat0 = Math.toRadians(observerPoint.latitude);
		double lon0 = Math.toRadians(observerPoint.longitude);

		double lat1 = Math.asin(Math.sin(lat0) * Math.cos(dist)
				+ Math.cos(lat0) * Math.sin(dist) * Math.cos(brng));
		double lon1 = lon0
				+ Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat0),
						Math.cos(dist) - Math.sin(lat0) * Math.sin(lat1));
		lon1 = (lon1 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
		return new GPSPoint(Math.toDegrees(lat1), Math.toDegrees(lon1), 0);
	}

	public static double[] getDistanceInMeters(GPSPoint positionFrom,
			GeoScreenPointContainer positionTo, double myBearing,
			DistanceAlgorithm distanceAlgorithm) {
		return getDistanceInMeters(positionFrom, positionTo.gpsPoint,
				myBearing, distanceAlgorithm);
	}

	/**
	 * returns the distance in meters (left/right , up/down)
	 * 
	 * @param position1
	 *            position from which the distance is calculated
	 * @param position2
	 *            position to which the distance is calculated
	 * @param myBearing
	 *            my own bearing (because bearing between two positions
	 *            calculates bearing to NORTH)
	 * @return [distance x, distance y]
	 *         ("you have to go x meters to the left and y meters to the top")
	 */
	public static double[] getDistanceInMeters(GPSPoint position1,
			GPSPoint position2, double myBearing,
			DistanceAlgorithm distanceAlgorithm) {
		/*
		 * distance from position1 to position2 in meters
		 */
		double hypotenuse = 0;
		switch (distanceAlgorithm) {
		case HAVERSINE:
			hypotenuse = position1.distanceTo(position2);
			break;
		case EQUIRECT:
			hypotenuse = position1.distanceTo2(position2);
			break;
		case LAW_OF_COSINES:
			hypotenuse = position1.distanceTo3(position2);
			break;
		case VINCENTY:
			hypotenuse = position1.distanceTo4(position2);
			break;
		}

		double alpha = position1.bearingTo2(position2) - myBearing;
		double opposite = Math.sin(Math.toRadians(alpha)) * hypotenuse;
		double adjacent = Math.cos(Math.toRadians(alpha)) * hypotenuse;
		return new double[] { opposite, adjacent };
	}

	public static double distanceLog(double dist) {
		double log = 1;
		if (dist == 0)
			return 0;
		log = Math.max(1, Math.min(MAX_LOG_DISTANCE_SHOWN, Math.log10(dist)))
				/ MAX_LOG_DISTANCE_SHOWN;

		return log;
	}
}
