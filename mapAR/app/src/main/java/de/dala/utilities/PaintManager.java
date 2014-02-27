package de.dala.utilities;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintManager {

	public static Paint fetchLinePaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xFFFFFF00);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(4);
		return paint;
	}

	public static Paint fetchVertexPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(3);
		return paint;
	}

	public static Paint fetchPolygonPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setAlpha(75);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		return paint;
	}

	public static Paint fetchPolygonPaint2() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setAlpha(2);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		return paint;
	}

	public static Paint fetchMapPathPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xFFFFFF00);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(6);
		return paint;
	}

	public static Paint fetchMapPathFillPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setAlpha(75);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		return paint;
	}
}
