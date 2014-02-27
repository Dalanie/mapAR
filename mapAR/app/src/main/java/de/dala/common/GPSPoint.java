package de.dala.common;

import java.io.Serializable;


import android.location.Location;

import de.dala.utilities.SystemProperties;

/**
 * This class stores the longitude/latitude value, together with the altitude
 * and the accuracy of the point
 * 
 * @author Daniel Langerenken
 * 
 */
public class GPSPoint implements Serializable {
	/**
	 * Earth's equatorial radius
	 */
	private static final float EQUATORIAL_RADIUS_EARTH = 6378137;

	/**
	 * Earth's polar radius
	 */
	private static final float POLAR_RADIUS_EARTH = 6356752.3f;

	/**
	 * The equivalent in meters for a distance of one latitude second
	 */
	private static final float LATITUDE_DEGREE = 30.82f;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3522061193440066398L;

	/**
	 * Longitude value of the gps-point
	 */
	public double longitude;

	/**
	 * Latitude value of the gps-point
	 */
	public double latitude;

	public double altitude;

	/**
	 * Accuracy of the point (returned value of the location listener)
	 */
	public float accuracy;

	public GPSPoint(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}

	public GPSPoint(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.altitude = location.getAltitude();
		this.accuracy = location.getAccuracy();
	}

	public GPSPoint(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * Calculate the bearing in angle between given two points http://
	 * www.smokycogs.com/blog/finding-the-bearing-between-two- gps-coordinates/
	 */
	public double bearingTo(GPSPoint point2) {
		return bearingTo(this, point2);
	}

	/**
	 * This function calculates the azimuth of a resource from the user's
	 * position
	 *
	 * @return This returns the azimuth in degrees
	 */
	public double bearingTo2(GPSPoint point2) {
		return bearingTo2(this, point2);
	}

	/**
	 * Calculate the bearing in angle between given two points http://
	 * www.smokycogs.com/blog/finding-the-bearing-between-two- gps-coordinates/
	 */
	public static double bearingTo2(GPSPoint point1, GPSPoint point2) {
		double lat1 = Math.toRadians(point1.latitude);
		double lat2 = Math.toRadians(point2.latitude);
		double lon1 = Math.toRadians(point1.longitude);
		double lon2 = Math.toRadians(point2.longitude);

		double deltaLong = lon2 - lon1;

		double y = Math.sin(deltaLong) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(deltaLong);
		double bearing = Math.atan2(y, x);

		return (Math.toDegrees(bearing) + 360) % 360;
	}

	/**
	 * based on arviewer This function calculates the azimuth of a resource from
	 * the user's position
	 *
	 *            more accurate then bearingTo2
	 * @return This returns the azimuth in degrees
	 */
	public static double bearingTo(GPSPoint point1, GPSPoint point2) {
		double azimuth;
		double dist_lat;
		double dist_lng;

		double lat_degree = LATITUDE_DEGREE * 3600;
		double long_degree = ((Math.PI / 180)
				* Math.cos(Math.toRadians(point1.latitude)) * Math
				.sqrt((Math.pow(EQUATORIAL_RADIUS_EARTH, 4)
						* Math.pow(Math.cos(Math.toRadians(point1.latitude)), 2) + Math
						.pow(POLAR_RADIUS_EARTH, 4)
						* Math.pow(Math.sin(Math.toRadians(point1.latitude)), 2))
						/ (Math.pow(EQUATORIAL_RADIUS_EARTH, 2)
								* Math.pow(Math.cos(Math
										.toRadians(point1.latitude)), 2) + Math
								.pow(POLAR_RADIUS_EARTH, 2)
								* Math.pow(Math.sin(Math
										.toRadians(point1.latitude)), 2))));

		dist_lat = (point2.latitude - point1.latitude) * lat_degree;
		dist_lng = (point2.longitude - point1.longitude) * long_degree;

		azimuth = Math.toDegrees(Math.atan2(dist_lng, dist_lat));

		if (azimuth < 0)
			azimuth += 360;

		return azimuth;
	}

	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public double distanceTo(GPSPoint point2) {
		return distanceTo(this, point2);
	}

	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html Haversine-Formula
	 */
	public static double distanceTo(GPSPoint point1, GPSPoint point2) {
		double lat1 = Math.toRadians(point1.latitude);
		double lon1 = Math.toRadians(point1.longitude);

		double lat2 = Math.toRadians(point2.latitude);
		double lon2 = Math.toRadians(point2.longitude);

		double dLat = lat2 - lat1;
		double dLon = lon2 - lon1;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = SystemProperties.EARTH_RADIUS * c;

		return distance;
	}

	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html Equirectangular
	 * approximation
	 */
	public static double distanceTo2(GPSPoint point1, GPSPoint point2) {
		double lat1 = Math.toRadians(point1.latitude);
		double lon1 = Math.toRadians(point1.longitude);

		double lat2 = Math.toRadians(point2.latitude);
		double lon2 = Math.toRadians(point2.longitude);

		double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
		double y = lat2 - lat1;
		double distance = Math.sqrt(x * x + y * y)
				* (SystemProperties.EARTH_RADIUS);
		return distance;
	}

	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public double distanceTo2(GPSPoint point2) {
		return distanceTo2(this, point2);
	}
	
	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html Spherical Law of
	 * Cosines
	 */
	public static double distanceTo3(GPSPoint point1, GPSPoint point2) {
		double lat1 = Math.toRadians(point1.latitude);
		double lon1 = Math.toRadians(point1.longitude);

		double lat2 = Math.toRadians(point2.latitude);
		double lon2 = Math.toRadians(point2.longitude);
		double distance = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
				* SystemProperties.EARTH_RADIUS;
		return distance;
	}

	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public double distanceTo3(GPSPoint point2) {
		return distanceTo3(this, point2);
	}
	
	/**
	 * http://stackoverflow.com/questions/120283/working-with-latitude-longitude
	 * -values-in-java http://www.movable-type.co.uk/scripts/latlong.html
	 * Vincenty formula
	 */
	public static double distanceTo4(GPSPoint point1, GPSPoint point2) {
		double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84
																		// ellipsoid
																		// params
		double L = Math.toRadians(point2.longitude - point1.longitude);
		double U1 = Math.atan((1 - f)
				* Math.tan(Math.toRadians(point1.latitude)));
		double U2 = Math.atan((1 - f)
				* Math.tan(Math.toRadians(point2.latitude)));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

		double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
		double lambda = L, lambdaP, iterLimit = 100;
		do {
			sinLambda = Math.sin(lambda);
			cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
					+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
					* (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
			if (sinSigma == 0)
				return 0; // co-incident points
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
			if (Double.isNaN(cos2SigmaM))
				cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (�6)
			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L
					+ (1 - C)
					* f
					* sinAlpha
					* (sigma + C
							* sinSigma
							* (cos2SigmaM + C * cosSigma
									* (-1 + 2 * cos2SigmaM * cos2SigmaM)));
		} while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

		if (iterLimit == 0)
			return Double.NaN; // formula failed to converge

		double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSq / 16384
				* (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B
								/ 6 * cos2SigmaM
								* (-3 + 4 * sinSigma * sinSigma)
								* (-3 + 4 * cos2SigmaM * cos2SigmaM)));
		double dist = b * A * (sigma - deltaSigma);

		return dist;
	}
	/**
	 * http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public double distanceTo4(GPSPoint point2) {
		return distanceTo4(this, point2);
	}

}
