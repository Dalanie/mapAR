package de.dala.coordinate_systems;

import java.math.BigDecimal;

import de.dala.common.GPSPoint;

public abstract class Point3D {

	protected double latitude;
	protected double longitude;
	protected double altitude;

	public double x;
	public double y;
	public double z;
	
	public Point3D(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		setXYZ();
	}

	public Point3D(double[] latLngAlt) {
		this.latitude = latLngAlt[0];
		this.longitude = latLngAlt[1];
		if (latLngAlt.length > 2) {
			this.altitude = latLngAlt[2];
		}
		setXYZ();
	}

	public Point3D(GPSPoint point) {
		this.latitude = point.latitude;
		this.longitude = point.longitude;
		this.altitude = point.altitude;
		setXYZ();
	}

	protected abstract void setXYZ();

	public double[] getXYZ() {
		return new double[] { x, y, z };
	}

	public GPSPoint getGpsPoint() {
		return new GPSPoint(latitude, longitude, altitude);
	}

	public double getDistanceInMeter(Point3D otherPoint) {
		BigDecimal xA = new BigDecimal(x);
		BigDecimal yA = new BigDecimal(y);
		BigDecimal zA = new BigDecimal(z);

		BigDecimal xB = new BigDecimal(otherPoint.x);
		BigDecimal yB = new BigDecimal(otherPoint.y);
		BigDecimal zB = new BigDecimal(otherPoint.z);

		BigDecimal x = xB.subtract(xA);
		BigDecimal y = yB.subtract(yA);
		BigDecimal z = zB.subtract(zA);
		BigDecimal x2 = x.pow(2);
		BigDecimal y2 = y.pow(2);
		BigDecimal z2 = z.pow(2);
		BigDecimal result = x2.add(y2).add(z2);
		return Math.sqrt(result.doubleValue());
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		setXYZ();
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		setXYZ();
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
		setXYZ();
	}

}
