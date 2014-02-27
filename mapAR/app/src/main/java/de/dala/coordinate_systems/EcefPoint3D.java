package de.dala.coordinate_systems;

import de.dala.common.GPSPoint;


/*
 * ECEF - Earth Centered Earth Fixed
 */
public class EcefPoint3D extends Point3D {
	/*
	 * WGS84 ellipsoid constants Radius
	 */
	private static final double a = 6378137;
	/*
	 * eccentricity
	 */
	private static final double e = 8.1819190842622e-2;

	private static final double asq = Math.pow(a, 2);
	private static final double esq = Math.pow(e, 2);

	public EcefPoint3D(double latitude, double longitude, double altitude) {
		super(latitude, longitude, altitude);
	}

	public EcefPoint3D(GPSPoint point) {
		super(point);
	}

	public EcefPoint3D(double[] latLngAlt) {
		super(latLngAlt);
	}

	/*
	 * ported from matlab code at https://gist.github.com/1536054 and
	 * https://gist.github.com/1536056
	 */
	@Override
	protected void setXYZ() {
		double lat = Math.toRadians(latitude);
		double lon = Math.toRadians(longitude);
		double alt = altitude;

		double N = a / Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2));

		x = (N + alt) * Math.cos(lat) * Math.cos(lon);
		y = (N + alt) * Math.cos(lat) * Math.sin(lon);
		z = ((1 - esq) * N + alt) * Math.sin(lat);
	}

	/*
	 * ported from matlab code at https://gist.github.com/1536054 and
	 * https://gist.github.com/1536056
	 */
	public double[] ecef2lla() {
		double b = Math.sqrt(asq * (1 - esq));
		double bsq = Math.pow(b, 2);
		double ep = Math.sqrt((asq - bsq) / bsq);
		double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		double th = Math.atan2(a * z, b * p);

		double lon = Math.atan2(y, x);
		double lat = Math.atan2(
				(z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq
						* a * Math.pow(Math.cos(th), 3)));
		double N = a / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
		double alt = p / Math.cos(lat) - N;

		/*
		 * mod lat to 0-2pi
		 */
		lon = lon % (2 * Math.PI);

		double[] ret = { lat, lon, alt };
		return ret;
	}

	@Override
	public double getDistanceInMeter(Point3D otherPoint) {
		if (otherPoint instanceof EcefPoint3D) {
			return super.getDistanceInMeter(otherPoint);
		} else {
			/*
			 * wrong coordinate system - return distance of gps positions
			 */
			return getGpsPoint().distanceTo(otherPoint.getGpsPoint());
		}
	}
}
