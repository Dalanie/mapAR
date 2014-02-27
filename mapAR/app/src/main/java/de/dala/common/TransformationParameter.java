package de.dala.common;

import java.io.Serializable;
import java.util.Date;

public class TransformationParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6083586652381360838L;
	public float height;
	public double bearing;
	public double pitch;
	public double rotation;
	public float focalLength;

	public float cameraVerticalAngle;
	public float cameraHorizontalAngle;
	public float cameraDistanceInPixel;
	public int canvasWidth;
	public int canvasHeight;
	public GPSPoint observerPosition;

	public long lastUpdate;

	public TransformationParameter(float height, double bearing, double pitch,
			double rotation, float cameraVerticalAngle,
			float cameraHorizontalAngle, float cameraDistanceInPixel,
			int canvasWidth, int canvasHeight, GPSPoint observerPosition,
			float focalLength) {
		this.height = height;
		this.bearing = bearing;
		this.pitch = pitch;
		this.rotation = rotation;
		this.cameraVerticalAngle = cameraVerticalAngle;
		this.cameraHorizontalAngle = cameraHorizontalAngle;
		this.canvasHeight = canvasHeight;
		this.canvasWidth = canvasWidth;
		this.observerPosition = observerPosition;
		this.cameraDistanceInPixel = cameraDistanceInPixel;
		this.focalLength = focalLength;
		lastUpdate = (new Date()).getTime();
	}

	@Override
	public String toString() {
		return String.format(
				"height: %s,  bearing: %s, pitch: %s, rotation: %s", height
						+ "", bearing + "", pitch + "", rotation + "");
	}
}
