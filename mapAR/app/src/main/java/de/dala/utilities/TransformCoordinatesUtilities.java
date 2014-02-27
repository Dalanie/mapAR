package de.dala.utilities;

import java.util.ArrayList;
import java.util.List;

import toxi.geom.Plane;
import toxi.geom.Ray3D;
import toxi.geom.Vec3D;
import de.dala.common.GPSPoint;
import de.dala.common.ScreenPoint;
import de.dala.common.TransformationParameter;

public class TransformCoordinatesUtilities {

	public static List<GPSPoint> transformGeoCoords(
			TransformationParameter transformationParameter,
			List<ScreenPoint> pointList) {

		List<GPSPoint> resultList = new ArrayList<GPSPoint>();
		if (transformationParameter != null) {
			double screenHeight = transformationParameter.canvasHeight;
			double screenWidth = transformationParameter.canvasWidth;
			double horizontalViewAngle = transformationParameter.cameraHorizontalAngle;
			double verticalViewAngle = transformationParameter.cameraVerticalAngle;

			double c = (screenWidth / 2.0)
					/ (Math.atan(Math.toRadians(horizontalViewAngle / 2)));

			double pitch = 90 - Math.abs(transformationParameter.pitch);
			double minAngle = pitch - verticalViewAngle / 2.0;

			float a = (float) (Math.sin(Math.toRadians(minAngle)) * c);
			float b = (float) (Math.cos(Math.toRadians(minAngle)) * c);

			Vec3D cameraPosition = new Vec3D(0.0f, 0.0f, b);

			for (ScreenPoint screenPoint : pointList) {
				float pointPositionX = (float) (screenPoint.getX() - screenWidth / 2);
				float pointPositionY = (float) ((screenHeight - screenPoint
						.getY()) * Math.cos(Math.toRadians(pitch)) + a);
				float pointPositionZ = (float) ((screenHeight - screenPoint
						.getY()) * Math.sin(Math.toRadians(pitch)));

				Vec3D pointVec = new Vec3D(pointPositionX, pointPositionY,
						pointPositionZ);
				Vec3D normal = new Vec3D(cameraPosition.x - pointVec.x,
						cameraPosition.y - pointVec.y, cameraPosition.z
								- pointVec.z);

				Plane xyPlane = Plane.XY;
				Ray3D mLine = new Ray3D(cameraPosition, normal);
				Vec3D intersectionPoint = (Vec3D) xyPlane
						.getIntersectionWithRay(mLine);

				double pointDeviationBearing = 0.0;
				if (intersectionPoint != null) {
					pointDeviationBearing = Math.toDegrees(Math
							.atan(intersectionPoint.x / intersectionPoint.y));
				}

				double pointBearing = transformationParameter.bearing
						+ pointDeviationBearing;
				double distance = 0;
				if (intersectionPoint != null) {
					distance = Math.abs(((transformationParameter.height / 100)
							* intersectionPoint.y / b)
							/ Math.cos(Math.toRadians(Math
									.abs(pointDeviationBearing))));
				}
				GPSPoint transformedPoint = GeographicUtils.destinationPoint(
						transformationParameter.observerPosition, pointBearing,
						distance);

				resultList.add(transformedPoint);
			}
		}
		return resultList;
	}
}
