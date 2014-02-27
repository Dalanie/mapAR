package de.dala.views;

import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import de.dala.common.MapItObject;
import de.dala.common.MapItObjectExtension;

public class ObjectOverlay extends View {

	private List<MapItObjectExtension> mapItObjects;
	private Paint paint;
	private Semaphore semaphore;

	public Handler mHandler = new InvalidateHandler(this);

	private static class InvalidateHandler extends Handler {
		private View view;

		public InvalidateHandler(View view) {
			this.view = view;
		}

		@Override
		public void handleMessage(Message msg) {
			view.postInvalidate();
		}
	}

	public ObjectOverlay(Context context) {
		super(context);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setAlpha(75);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);

		semaphore = new Semaphore(1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mapItObjects != null) {
			for (int i = 0; i < mapItObjects.size(); i++) {
				MapItObject currentObject = mapItObjects.get(i);
				currentObject.draw(canvas, paint);
			}
		}
		super.onDraw(canvas);
	}

	public List<MapItObjectExtension> getMapItObjects() {
		return mapItObjects;
	}

	public void setMapItObjects(List<MapItObjectExtension> objects) {
		try {
			semaphore.acquire();
			this.mapItObjects = objects;
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
