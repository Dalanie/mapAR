package de.dala.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import de.dala.R;
import de.dala.common.ColorPoint;
import de.dala.common.MapItObjectExtension;

/**
 * Code optimized, added, changed to show a RadarView Most of the sourcecode is
 * based on Libresofts ARViewer (Copyright (C) 2010 GSyC/LibreSoft, Universidad
 * Rey Juan Carlos)
 * 
 * @author Daniel Langerenken, LibreSoft(Universidad, Rey Juan Carlos)
 */
public class RadarView extends View {

	private Context context;
	private Handler mHandler = new InvalidateHandler(this);
	private Semaphore sem;

	private boolean rotateCompass;
	private float bearing;
	private List<ColorPoint> colorPoints = null;

	/*
	 * vision range
	 */
	private Paint paintRange;
	private Paint defaultPaint;
	private RectF oval;
	private float compassCenterX;
	private float compassCenterY;

	private Bitmap compassAppearance = null;
	private Bitmap compassShadow = null;

	private static float RADIUS;
	private static float RADIUS_ANGLE;
	private static final float resourceRadius = 2;
	private static float maxRadiusResource;
	private static final float BORDER = 10;

	public static final float MAX_AZIMUTH_VISIBLE = 30;

	public RadarView(Context context) {
		super(context);

		this.compassAppearance = BitmapFactory.decodeResource(getResources(),
				R.drawable.compass_blue);
		this.compassShadow = BitmapFactory.decodeResource(getResources(),
				R.drawable.compass_blue_shadow);
		this.context = context;
		RADIUS = ((float) compassAppearance.getWidth()) / 2;
		RADIUS_ANGLE = RADIUS - transformPixInDip(4);
		maxRadiusResource = RADIUS_ANGLE - transformPixInDip(resourceRadius);
		bearing = 0;
		rotateCompass = true;
		sem = new Semaphore(1);
		configurePaint();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float pixelWidth = RADIUS * 2;
		float pixelHeight = RADIUS * 2;
		this.setMeasuredDimension((int) pixelWidth + 5, (int) pixelHeight + 5);
	}

	private void configurePaint() {
		compassCenterX = RADIUS + BORDER;
		compassCenterY = compassCenterX;

		defaultPaint = new Paint();
		paintRange = new Paint();
		paintRange.setStyle(Paint.Style.FILL);
		paintRange.setShader(new RadialGradient(compassCenterX,
				compassCenterY, RADIUS_ANGLE, Color.WHITE, Color.YELLOW,
				TileMode.MIRROR));
		oval = new RectF(compassCenterX - RADIUS_ANGLE, compassCenterY
				- RADIUS_ANGLE, compassCenterX + RADIUS_ANGLE,
				compassCenterY + RADIUS_ANGLE);
	}

	public void setResourcesList(final List<MapItObjectExtension> mapItObjects) {
		new Thread() {
			public void run() {
				List<ColorPoint> points = new ArrayList<ColorPoint>();
				if (mapItObjects != null) {
					for (int i = 0; i < mapItObjects.size(); i++) {
						ColorPoint currentPoint = new ColorPoint(
								mapItObjects.get(i).currentPoint, mapItObjects
										.get(i).getDrawColor());
						if (currentPoint != null) {
							points.add(currentPoint);
						}
					}
				}
				try {
					sem.acquire();
					colorPoints = points;
					sem.release();
					mHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					Log.e("DrawRadar", "", e);
				}
			}
		}.start();
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
		if (this.bearing < 0) {
			this.bearing += 360;
		} else if (this.bearing > 360) {
			this.bearing -= 360;
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		float base = 0;
		if (!rotateCompass) {
			base = bearing;
		}
		canvas.drawArc(oval, base - 90 - MAX_AZIMUTH_VISIBLE,
				MAX_AZIMUTH_VISIBLE * 2, true, paintRange);

		/*
		 * Draws shadow of the radar
		 */
		canvas.drawBitmap(compassShadow, compassCenterX - RADIUS,
				compassCenterY - RADIUS, null);

		/*
		 * rotates the compass
		 */
		if (rotateCompass) {
			canvas.rotate(-bearing, compassCenterX, compassCenterY);
		}

		/*
		 * draws the background of the compass
		 */
		canvas.drawBitmap(compassAppearance, compassCenterX - RADIUS,
				compassCenterY - RADIUS, null);

		/*
		 * draws every gps-point onto the radar
		 */
		try {
			sem.acquire();
			if (colorPoints != null) {
				defaultPaint.setStyle(Paint.Style.FILL);
				int max = colorPoints.size();
				for (int i = 0; i < max; i++) {
					ColorPoint point = colorPoints.get(i);
					defaultPaint.setColor(point.color);
					defaultPaint.setAlpha(255);
					canvas.drawCircle(compassCenterX
							+ ((float) maxRadiusResource * point.x) / 100,
							compassCenterY
									+ ((float) maxRadiusResource * point.y)
									/ 100, resourceRadius, defaultPaint);
				}
			}
			sem.release();
		} catch (InterruptedException e) {
			Log.e("DrawRadar", "", e);
		}
		super.onDraw(canvas);
	}

	/**
	 * Transformation of px in dip
	 *
	 * @param px
	 * @return
	 */
	public float transformPixInDip(float px) {
		return ((px * context.getResources().getDisplayMetrics().density) + 0.5f);
	}

	private static class InvalidateHandler extends Handler {
		private View view;

		public InvalidateHandler(View view) {
			this.view = view;
		}

		@Override
		public void handleMessage(Message msg) {
			view.invalidate();
		}
	}

}