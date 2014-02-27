package de.dala.common;

/**
 * This class stores the information of a gps-bean and combines it with the
 * dynamically calculated estimated screenpoint
 * 
 * @author Daniel Langerenken
 * 
 */
public class GeoScreenPointContainer {

	/**
	 * Dynamically calculated screen point
	 */
	public ScreenPoint screenPoint;
	public GPSPoint gpsPoint;

	public GeoScreenPointContainer(ScreenPoint screenPoint, GPSPoint gpsPoint) {
		this.screenPoint = screenPoint;
		this.gpsPoint = gpsPoint;
	}
}
