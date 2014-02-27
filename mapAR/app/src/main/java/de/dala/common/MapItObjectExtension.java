package de.dala.common;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.Point;

import de.dala.utilities.GeographicUtils;

/**
 * This class represents the mapit-object combined with the current distance to
 * the user
 * 
 * @author Daniel Langerenken
 * 
 */
public class MapItObjectExtension extends MapItObject implements
		Comparable<MapItObjectExtension> {

	/*
	 * 5 seconds refresh time for performance
	 */
	private static final int refresh_time = 5000;

	private double currentDistance;
	private GPSPoint nearestGPSPoint;
	private long lastUpdate;

	public MapItObjectExtension(ArrayList<GPSPoint> polygonPoints,
			String description) {
		super(polygonPoints, description);
	}

	/**
	 * Returns the nearest gps point - for performance issues caching the
	 * nearest point for some seconds
	 */
	public GPSPoint getNearestPoint(GPSPoint newLocation) {
		long newUpdate = new Date().getTime();
		double distance = 10000000;
		if (polygonPoints != null) {
			if (nearestGPSPoint == null
					|| (newUpdate - lastUpdate > refresh_time)) {
				for (int i = 0; i < polygonPoints.size(); i++) {
					GPSPoint currentPoint = polygonPoints.get(i);
					double tempDistance = currentPoint.distanceTo(newLocation);
					if (tempDistance < distance) {
						distance = tempDistance;
						nearestGPSPoint = currentPoint;
					}
				}
				lastUpdate = newUpdate;
			}
		}
		if (nearestGPSPoint != null) {
			return nearestGPSPoint;
		}
		return newLocation;
	}

	public void setDrawnValues(GPSPoint myLocation) {
		GPSPoint nearestGPSPoint = getNearestPoint(myLocation);
		if (nearestGPSPoint != null) {
			double mapItObjectAzimuth = myLocation.bearingTo(nearestGPSPoint);
			/*
			 * Distance to object
			 */
			currentDistance = myLocation.distanceTo(nearestGPSPoint);

			if (currentPoint == null) {
				currentPoint = new Point();
			}
			currentPoint
					.set((int) (GeographicUtils
							.distanceLog((double) currentDistance) * 100 * Math
							.sin(Math.toRadians(mapItObjectAzimuth))),
							(int) (GeographicUtils
									.distanceLog((double) currentDistance)
									* -100 * Math.cos(Math
									.toRadians(mapItObjectAzimuth))));
		}
	}

	public double getCurrentDistance() {
		return currentDistance;
	}

	public void setCurrentDistance(float currentDistance) {
		this.currentDistance = currentDistance;
	}

	@Override
	public int compareTo(MapItObjectExtension another) {
		if (polygonPoints == null && another.polygonPoints != null) {
			return 1;
		}
		if (another.polygonPoints == null && polygonPoints != null) {
			return -1;
		}
		return Double.compare(currentDistance, another.currentDistance);
	}
}
