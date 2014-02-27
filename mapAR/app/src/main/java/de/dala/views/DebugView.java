package de.dala.views;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import de.dala.MainActivity;
import de.dala.common.GPSPoint;

public class DebugView extends View {

	private static final int marginBetweenText = 8;
	private double bearing;
	private double roll;
	private double pitch;
	private List<GPSPoint> cornerPoints;
	private GPSPoint ownPosition;
	private Paint textPaint;
	private int textHeight;

	private int marginWidth = 30;
	private int marginTop;
	private int marginBottom;
	private DecimalFormat decimalFormat;

	public DebugView(Context context) {
		super(context);
		if (context instanceof MainActivity) {
			this.marginTop = ((MainActivity) context).getActionBarHeight() + 10;
		} else {
			this.marginTop = 110;
		}
		configurePaint();
	}

	private void configurePaint() {
		textPaint = new Paint();
		textPaint.setTextSize(30);
		textPaint.setColor(Color.WHITE);
		decimalFormat = new DecimalFormat("#000");
		getHeightOfString();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawCornerPoints(canvas);
		drawSensorAndPositionData(canvas);
	}

	private void drawSensorAndPositionData(Canvas canvas) {
		String sensorText = String.format("Bearing: %s, Pitch: %s, Roll: %s",
				decimalFormat.format(bearing) + "�",
				decimalFormat.format(pitch) + "�", decimalFormat.format(roll)
						+ "�");
		int sensorTextWidth = getWidthOfString(sensorText);
		canvas.drawText(sensorText, (canvas.getWidth() / 2) - (sensorTextWidth / 2),
				marginTop + 80, textPaint);

		if (ownPosition != null) {
			String positionText = String.format("Position Lon: %s    Lat: %s",
					ownPosition.longitude + "", ownPosition.latitude + "");
			int positionTextWidth = getWidthOfString(positionText);
			canvas.drawText(positionText,
					(canvas.getWidth() / 2) - (positionTextWidth / 2), marginTop + 80
							+ textHeight + marginBetweenText, textPaint);
		}
	}

	private void drawCornerPoints(Canvas canvas) {
		if (cornerPoints != null && cornerPoints.size() == 4) {
			String topLeftCorner = String.format("Lon: %s  Lat: %s",
					cornerPoints.get(0).longitude + "",
					cornerPoints.get(0).latitude + "");
			canvas.drawText(topLeftCorner, marginWidth, marginTop + textHeight,
					textPaint);

			String topRightCorner = String.format("Lon: %s  Lat: %s",
					cornerPoints.get(2).longitude + "",
					cornerPoints.get(2).latitude + "");
			canvas.drawText(topRightCorner, canvas.getWidth()
					- getWidthOfString(topRightCorner) - marginWidth, marginTop
					+ textHeight, textPaint);

			String bottomLeftCorner = String.format("Lon: %s  Lat: %s",
					cornerPoints.get(1).longitude + "",
					cornerPoints.get(1).latitude + "");
			canvas.drawText(bottomLeftCorner, marginWidth, canvas.getHeight()
					- textHeight - marginBottom, textPaint);

			String bottomRightCorner = String.format("Lon: %s  Lat: %s",
					cornerPoints.get(3).longitude + "",
					cornerPoints.get(3).latitude + "");
			canvas.drawText(bottomRightCorner, canvas.getWidth()
					- getWidthOfString(bottomRightCorner) - marginWidth,
					canvas.getHeight() - textHeight - marginBottom, textPaint);
		}
	}

	private void getHeightOfString() {
		String text = "DUMMY";
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		textHeight = bounds.height();
	}

	private int getWidthOfString(String text) {
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		int textWidth = bounds.width();
		return textWidth;
	}

	public void refreshGPSData(List<GPSPoint> corner, int marginBottom) {
		this.cornerPoints = corner;
		this.marginBottom = marginBottom;
		invalidate();
	}

	public void refreshOwnPosition(GPSPoint ownPosition) {
		this.ownPosition = ownPosition;
	}

	public void refreshSensorData(double bearing, double pitch, double roll,
			int marginBottom) {
		this.bearing = bearing;
		this.pitch = pitch;
		this.roll = roll;
		this.marginBottom = marginBottom;
		invalidate();
	}

}
