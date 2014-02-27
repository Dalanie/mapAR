package de.dala.common;

import java.io.Serializable;

public class OrientationPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3693372738612942320L;

	public double pitch;
	public double bearing;
	public double rotation;
	public int accuracy;

	public OrientationPoint(double bearing, double pitch, double rotation) {
		this.rotation = rotation;
		this.bearing = bearing;
		this.pitch = pitch;
		this.accuracy = 0;
	}
	
	public OrientationPoint(double bearing, double pitch, double rotation, int accuracy) {
		this.rotation = rotation;
		this.bearing = bearing;
		this.pitch = pitch;
		this.accuracy = accuracy;;
	}
	
}
