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


public class AccuracyView extends View {
	private String accuracyString = "Accuracy: ???";
	private int accuracyStringWidth;
	private int textHeight;
	private int currentViewWidth;

	private float accuracy_width;
	private float accuracy_height;

	private Bitmap accuracyImageFine = null;
	private Bitmap accuracyImageMedium = null;
	private Bitmap accuracyImageBad = null;

	private Paint textPaint;

	private Bitmap currentAccuracyImage;

	public static final int marginBetweenElements = 15;

	private static final double FINE_ACCURACY = 3;
	private static final double MEDIUM_ACCURACY = 12;

	public AccuracyView(Context context) {
		super(context);
		init();
		configurePaint();
	}

	private void init() {
		accuracyImageBad = BitmapFactory.decodeResource(getResources(),
				 R.drawable.ic_gps_bad);
		accuracyImageMedium = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_gps_medium);
		accuracyImageFine = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_gps_fine);
		currentAccuracyImage = accuracyImageBad;
		accuracy_width = currentAccuracyImage.getWidth();
		accuracy_height = currentAccuracyImage.getHeight();
		accuracyString = "";
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
		if (currentViewWidth < accuracyStringWidth) {
			requestLayout();
		}
		if (accuracyStringWidth > accuracy_width) {
			canvas.drawText(accuracyString, 0, accuracy_height + textHeight
					+ marginBetweenElements, textPaint);
			canvas.drawBitmap(currentAccuracyImage, accuracyStringWidth / 2
					- accuracy_width / 2, 0, null);
		} else {
			canvas.drawText(accuracyString, accuracy_width / 2
					- accuracyStringWidth / 2, accuracy_height + textHeight
					+ marginBetweenElements, textPaint);
			canvas.drawBitmap(currentAccuracyImage, 0, 0, null);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (accuracy_width > accuracyStringWidth) {
			currentViewWidth = (int) accuracy_width;
		} else {
			currentViewWidth = accuracyStringWidth;
		}
		float pixelHeight = accuracy_height
				+ (textHeight + marginBetweenElements) * 2;
		this.setMeasuredDimension(currentViewWidth + 5, (int) pixelHeight + 5);
	}

	public void onAccuracyChanged(double newAccuracy) {
		checkAccuracy(newAccuracy);
		DecimalFormat f = new DecimalFormat("02");
		this.accuracyString = String.format("Accuracy: %s m",
				f.format(newAccuracy));
		setStringDimensions(accuracyString);
		this.invalidate();
	}

	private void setStringDimensions(String text) {
		Rect bounds = new Rect();
		if (text == null || text.equals("")) {
			text = "No description";
		}
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		accuracyStringWidth = bounds.width();
		textHeight = bounds.height();
	}

	private void checkAccuracy(double newAccuracy) {
		if (newAccuracy < FINE_ACCURACY) {
			currentAccuracyImage = accuracyImageFine;
		} else if (newAccuracy < MEDIUM_ACCURACY) {
			currentAccuracyImage = accuracyImageMedium;
		} else {
			currentAccuracyImage = accuracyImageBad;
		}
	}
}
