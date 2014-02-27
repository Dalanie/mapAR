package de.dala.projection_algorithm;

import java.util.ArrayList;
import java.util.List;

import de.dala.common.GPSPoint;
import de.dala.common.ScreenPoint;
import de.dala.common.TransformationParameter;
import de.dala.coordinate_systems.DistanceAlgorithm;
import de.dala.coordinate_systems.DistancePoint3D;
import de.dala.coordinate_systems.Point3D;

public class PerspectiveScreenProjector extends BasicProjector {

	/**
	 * based on
	 * http://en.wikipedia.org/wiki/3D_projection#Perspective_projection
	 * 
	 */
	@Override
	public List<ScreenPoint> transformWorldToScreen(
			TransformationParameter parameter, List<GPSPoint> gpsPoints) {
		List<ScreenPoint> resultList = new ArrayList<ScreenPoint>();

		if (parameter != null) {
			double bearing = Math.toRadians(parameter.bearing);
			double pitch = Math.toRadians(parameter.pitch);
			double rotation = Math.toRadians(parameter.rotation);

			// Log.d("Sensordata", bearing + "  |  " + pitch + "   |  " +
			// rotation);
			/*
			 * H�he des Bildschirms
			 */
			double screenSizeY = parameter.canvasHeight;

			/*
			 * Breite des Bildschirms
			 */
			double screenSizeX = parameter.canvasWidth;

			double cameraRotationX = -pitch;
			double cameraRotationY = bearing;
			double cameraRotationZ = -rotation;

			GPSPoint cameraPosition = parameter.observerPosition;
			/*
			 * height from cm to m
			 */
			cameraPosition.altitude = parameter.height / 100;
			for (int i = 0; i < gpsPoints.size(); i++) {
				GPSPoint p = gpsPoints.get(i);

				/*
				 * Relative translation in meters
				 */

				/*
				 * Variante 1
				 */
				Point3D p3D = new DistancePoint3D(cameraPosition, p, false,
						bearing, DistanceAlgorithm.HAVERSINE);
				Point3D c3D = new DistancePoint3D(cameraPosition, p, true,
						bearing, DistanceAlgorithm.HAVERSINE);

				/*
				 * Variante 2: Umwandeln der GPS-Punkte in Meter via Meter pro
				 * Grad (~111000m/Grad)
				 */
				// Point3D p3D = new LongLatAlt3DPoint(p);
				// Point3D c3D = new LongLatAlt3DPoint(cameraPosition);

				/*
				 * Variante 3 Umwandeln der GPS-Punkte nach dem Earth-Centered,
				 * Earth-Fixed Schema
				 */
				// Point3D p3D = new EcefPoint3D(p);
				// Point3D c3D = new EcefPoint3D(cameraPosition);

				double relativeTranslationX = (p3D.x - c3D.x);
				double relativeTranslationY = (p3D.y - c3D.y);
				double relativeTranslationZ = (p3D.z - c3D.z);

				/*
				 * http://en.wikipedia.org/wiki/3D_projection#Perspective_projection
				 */
				double relativePositionX = Math.cos(cameraRotationY)
						* (Math.sin(cameraRotationZ) * relativeTranslationY + Math
								.cos(cameraRotationZ) * relativeTranslationX)
						- Math.sin(cameraRotationY) * relativeTranslationZ;
				double relativePositionY = Math.sin(cameraRotationX)
						* (Math.cos(cameraRotationY) * relativeTranslationZ + Math
								.sin(cameraRotationY)
								* (Math.sin(cameraRotationZ)
										* relativeTranslationY + Math
										.cos(cameraRotationZ)
										* relativeTranslationX))
						+ Math.cos(cameraRotationX)
						* (Math.cos(cameraRotationZ) * relativeTranslationY - Math
								.sin(cameraRotationZ) * relativeTranslationX);
				double relativePositionZ = Math.cos(cameraRotationX)
						* (Math.cos(cameraRotationY) * relativeTranslationZ + Math
								.sin(cameraRotationY)
								* (Math.sin(cameraRotationZ)
										* relativeTranslationY + Math
										.cos(cameraRotationZ)
										* relativeTranslationX))
						- Math.sin(cameraRotationX)
						* (Math.cos(cameraRotationZ) * relativeTranslationY - Math
								.sin(cameraRotationZ) * relativeTranslationX);

				/*
				 * Wenn relativePositionZ < 0 ist, dann ist der Punkt hinter der
				 * Kamera und nicht sichtbar
				 */
				// Log.d("RelativePositionZ", "Z: " + relativePositionZ);
				if (relativePositionZ > 0) {
					double x = (relativePositionX / relativePositionZ)
							* ((screenSizeX / 2) / Math
									.tan(Math
											.toRadians(parameter.cameraHorizontalAngle) / 2))
							+ (screenSizeX / 2);
					double y = (relativePositionY / relativePositionZ)
							* ((screenSizeY / 2) / Math
									.tan(Math
											.toRadians(parameter.cameraVerticalAngle) / 2))
							+ (screenSizeY / 2);
					ScreenPoint screenPoint = new ScreenPoint((int) x, (int) y);
					resultList.add(screenPoint);
				} else {
					// Log.d("Coord-Transformation", "Point not visible");
				}
			}
		}
		return resultList;
	}

	@Override
	public String getProjectionName() {
		return "Perspective";
	}
}
