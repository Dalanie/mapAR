package de.dala.common;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import de.dala.utilities.ColorManager;

/**
 * This class represents the mapit-object which is taken by another application
 * and can be reused inside of this application It basically contains the
 * polygons, a description and the image
 * 
 * @author Daniel Langerenken
 * 
 */
public class MapItObject {

	/**
	 * Liste der Eckpunkte des zu zeichnenden Objektes
	 */
	public ArrayList<GPSPoint> polygonPoints;
	public List<ScreenPoint> screenPoints;
	public String description;

	/**
	 * Point which depends on the current location of the smartphone (bearing,
	 * pitch, roll)
	 */
	public Point currentPoint;

	public long id;
	private int drawColor = -1;

	public MapItObject(ArrayList<GPSPoint> polygonPoints, String description) {
		this.polygonPoints = polygonPoints;
		this.description = description;
	}

	public MapItObject(PolygonNetworkWrapper wrapper) {
		this.polygonPoints = wrapper.polygon;
		this.description = wrapper.description;
	}

	public void setScreenPoints(List<ScreenPoint> screenPoints) {
		this.screenPoints = screenPoints;
	}

	public void draw(Canvas canvas, Paint paint) {
		paint.setColor(getDrawColor());
		Path drawedPath = new Path();
		int index = 0;
		if (screenPoints != null) {
			for (ScreenPoint point : screenPoints) {
				if (point == null) {
					continue;
				}
				if (index == 0) {
					drawedPath.moveTo((int) point.getX(), (int) point.getY());
				}
				if (index++ != screenPoints.size()) {
					drawedPath.lineTo((int) point.getX(), (int) point.getY());
				} else {
					drawedPath.setLastPoint((int) point.getX(),
							(int) point.getY());
				}
			}
			canvas.drawPath(drawedPath, paint);
		}
	}

	public int getDrawColor() {
		if (drawColor == -1) {
			drawColor = ColorManager.getNewColor();
		}
		return drawColor;
	}
}
