package de.dala.projection_algorithm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import de.dala.MainActivity;
import de.dala.utilities.TransformCoordinatesUtilities;
import de.dala.common.GPSPoint;
import de.dala.common.GeoScreenPointContainer;
import de.dala.common.ScreenPoint;
import de.dala.common.TransformationParameter;

public class MapItGridWorldToScreenProjector extends BasicProjector {

	private int gridSizeX = 10;
	private int gridSizeY = 10;

	private long lastUpdateOfParameter = 0;
	private List<GeoScreenPointContainer> lastGeoScreenPointContainer;

	private MainActivity activity;

	public MapItGridWorldToScreenProjector(Context context) {
		if (context instanceof MainActivity) {
			this.activity = (MainActivity) context;
		}
	}

	@Override
	public synchronized List<ScreenPoint> transformWorldToScreen(
			TransformationParameter parameter, List<GPSPoint> gpsPoints) {
		lastGeoScreenPointContainer = getDisplayGPSPoints(parameter);

		List<ScreenPoint> drawPoints = new ArrayList<ScreenPoint>();
		if (gpsPoints != null) {
			for (int i = 0; i < gpsPoints.size(); i++) {
				GPSPoint currentPoint = gpsPoints.get(i);
				drawPoints.add(getNearestElement(currentPoint,
						lastGeoScreenPointContainer));
			}
		}
		return drawPoints;
	}

	@Override
	public String getProjectionName() {
		return "MapItGrid";
	}

	/**
	 * Erstellt ein Gitternetz von GPS-Koordinaten, an denen man sich später
	 * orientieren kann
	 * 
	 * @param parameter
	 *
	 * @return Gitter von der Größe X*Y in GPS Punkten
	 */
	public List<GeoScreenPointContainer> getDisplayGPSPoints(
			TransformationParameter parameter) {
		if (parameter == null) {
			return new ArrayList<GeoScreenPointContainer>();
		}
		/*
		 * create grid only once
		 */
		if (lastUpdateOfParameter == parameter.lastUpdate
				&& lastGeoScreenPointContainer != null) {
			return lastGeoScreenPointContainer;
		}
		int yStep = parameter.canvasHeight / gridSizeY;
		int xStep = parameter.canvasWidth / gridSizeX;

		List<ScreenPoint> screenPointList = new ArrayList<ScreenPoint>();
		for (int x = 0; x < gridSizeX; x++) {
			for (int y = 0; y < gridSizeY; y++) {
				screenPointList.add(new ScreenPoint(x * xStep, y * yStep));
			}
		}
		List<GPSPoint> transformedGPSCoords = transformDisplayCoordsToGPSCoords(
				screenPointList, parameter);
		List<GeoScreenPointContainer> geoInScreenPoints = new ArrayList<GeoScreenPointContainer>();

		for (int i = 0; i < screenPointList.size(); i++) {
			ScreenPoint point = screenPointList.get(i);
			GPSPoint gps = transformedGPSCoords.get(i);
			geoInScreenPoints.add(new GeoScreenPointContainer(point, gps));
		}
		lastUpdateOfParameter = parameter.lastUpdate;
		return geoInScreenPoints;
	}

	public List<GPSPoint> transformDisplayCoordsToGPSCoords(
			List<ScreenPoint> screenPoints, TransformationParameter parameter) {
		return TransformCoordinatesUtilities.transformGeoCoords(parameter,
                screenPoints);
	}

	public ScreenPoint getNearestElement(GPSPoint gpsPoint,
			List<GeoScreenPointContainer> points) {
		ScreenPoint point = null;
		double distance = Integer.MAX_VALUE;
		for (GeoScreenPointContainer geoInScreenPoint : points) {
			double tempDistance = gpsPoint
					.distanceTo(geoInScreenPoint.gpsPoint);
			if (tempDistance < distance) {
				point = geoInScreenPoint.screenPoint;
				distance = tempDistance;
			}
		}
		return point;
	}

	public void keyCodeDown() {
		if (gridSizeX > 1) {
			gridSizeX--;
		}
		if (gridSizeY > 1) {
			gridSizeY--;
		}
		activity.toast(String.format("Grid: %s x %s", gridSizeX + "", gridSizeY
				+ ""));
	}

	public void keyCodeUp() {
		gridSizeX++;
		gridSizeY++;
		activity.toast(String.format("Grid: %s x %s", gridSizeX + "", gridSizeY
				+ ""));
	}
}
