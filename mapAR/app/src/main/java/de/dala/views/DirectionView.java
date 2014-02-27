package de.dala.views;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import de.dala.R;

public class DirectionView extends View {

	private float bearing;
	private String description;
	private double distanceInMeter;

	private float arrow_width;
	private float arrow_height;
	private Bitmap direction_arrow = null;
	private Paint textPaint;
	private int textHeight;
	private int distanceStringWidth;
	private int descriptionStringWidth;
	private int currentViewWidth;

	public static final int marginBetweenElements = 15;

	public DirectionView(Context context) {
		super(context);

		direction_arrow = BitmapFactory.decodeResource(getResources(),
				R.drawable.arrow_blue);
		arrow_width = direction_arrow.getWidth();
		arrow_height = direction_arrow.getHeight();

		bearing = 0;
		description = "";
		distanceInMeter = 0;
		configurePaint();
	}

	private void configurePaint() {
		textPaint = new Paint();
		textPaint.setTextSize(40);
		textPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*
		 * Draws shadow of the radar
		 */
		String distanceString = getDistanceString();
		String description = getDescriptionString();

		if (currentViewWidth < distanceStringWidth
				|| currentViewWidth < descriptionStringWidth) {
			requestLayout();
		}
		if (distanceStringWidth > arrow_width
				|| descriptionStringWidth > arrow_width) {
			canvas.drawText(distanceString, 0, arrow_height + textHeight
					+ marginBetweenElements, textPaint);
			canvas.drawText(description, 0, arrow_height
					+ (textHeight + marginBetweenElements) * 2, textPaint);
			canvas.rotate(-bearing, distanceStringWidth / 2, arrow_height / 2);
			canvas.drawBitmap(direction_arrow, distanceStringWidth / 2
					- arrow_width / 2, 0, null);
		} else {
			canvas.drawText(distanceString, arrow_width / 2
					- distanceStringWidth / 2, arrow_height + textHeight
					+ marginBetweenElements, textPaint);
			canvas.drawText(description, arrow_width / 2
					- descriptionStringWidth / 2, arrow_height
					+ (textHeight + marginBetweenElements) * 2, textPaint);

			canvas.rotate(-bearing, arrow_width / 2, arrow_height / 2);
			canvas.drawBitmap(direction_arrow, 0, 0, null);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		getDistanceString();
		if (arrow_width > distanceStringWidth
				&& arrow_width > descriptionStringWidth) {
			currentViewWidth = (int) arrow_width;
		} else {
			currentViewWidth = distanceStringWidth > descriptionStringWidth ? distanceStringWidth
					: descriptionStringWidth;
		}
		float pixelHeight = arrow_height + (textHeight + marginBetweenElements)
				* 2;
		this.setMeasuredDimension(currentViewWidth + 5, (int) pixelHeight + 5);
	}

	private String getDistanceString() {
		DecimalFormat f = new DecimalFormat("#0.00");
		String text = String.format("Distance: %s meter",
				f.format(distanceInMeter));
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		textHeight = bounds.height();
		distanceStringWidth = bounds.width();
		return text;
	}

	private String getDescriptionString() {
		String text = description;
		Rect bounds = new Rect();
		if (text == null || text.equals("")) {
			text = "No description";
		}
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		descriptionStringWidth = bounds.width();
		return text;
	}

	/**
	 * If new data is available, this method redraws the arrow with the correct
	 * direction
	 * 
	 * @param bearing
	 *            - direction to point
	 * @param description
	 *            - description of gps point
	 * @param distanceInMeter
	 *            - distance in meters
	 */
	public void refreshArrowData(float bearing, String description,
			double distanceInMeter) {
		this.bearing = bearing;
		if (bearing > 360) {
			bearing -= 360;
		} else if (bearing < 0) {
			bearing += 360;
		}
		this.description = description;
		this.distanceInMeter = distanceInMeter;
		invalidate();
	}

}
