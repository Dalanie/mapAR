package de.dala.common;

import java.util.ArrayList;

public class PolygonNetworkWrapper {
	public ArrayList<GPSPoint> polygon;
	public String description;

	public PolygonNetworkWrapper(ArrayList<GPSPoint> polygon, String description) {
		this.polygon = polygon;
		this.description = description;
	}

	public PolygonNetworkWrapper(MapItObjectExtension mapItObject) {
		this.polygon = mapItObject.polygonPoints;
		this.description = mapItObject.description;
	}
}
