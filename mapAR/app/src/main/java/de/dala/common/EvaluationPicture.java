package de.dala.common;

import java.io.Serializable;
import java.util.ArrayList;

public class EvaluationPicture implements Serializable {

	public String imagePath;
	public TransformationParameter params;
	public ArrayList<ScreenPoint> screenPointsOfPolygon;
	public ArrayList<GPSPoint> gpsPointsOfPolygon;

	public EvaluationPicture(String imagePath, TransformationParameter params,
			ArrayList<ScreenPoint> screenPointList,
			ArrayList<GPSPoint> gpsPointsOfPolygon) {
		this.imagePath = imagePath;
		this.params = params;
		this.screenPointsOfPolygon = screenPointList;
		this.gpsPointsOfPolygon = gpsPointsOfPolygon;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return String.format("ImagePath: %s, Params: %s", imagePath,
				params.toString());
	}
}
