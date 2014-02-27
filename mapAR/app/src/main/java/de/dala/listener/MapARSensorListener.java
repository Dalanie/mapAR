package de.dala.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.dala.PositionManager;

public class MapARSensorListener implements SensorEventListener {

	private PositionManager positionManager;

	/*
	 * accelerometer sensor values
	 */
	private float[] aValues = new float[3];
	/*
	 * magnetic field sensor values
	 */
	private float[] mValues = new float[3];
	private float[] laValues = new float[3];

	public MapARSensorListener(PositionManager positionManager) {
		this.positionManager = positionManager;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			laValues = aValues.clone();
			aValues = event.values.clone();
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mValues = event.values.clone();
		}

		if (Math.abs(Math.toDegrees(laValues[1]) - Math.toDegrees(aValues[1])) > 1) {
			float[] values = calculateOrientation();
			updateOrientation(values, event.accuracy);
		}
	}

	private void updateOrientation(float[] values, int accuracy) {
		/*
		 * Map-It values
		 */
		double bearing = values[0];
		double pitch = values[1];
		double rotation = values[2];
		/*
		 * replace negative bearing (so bearing is between 0 - 360)
		 */
		if (bearing != 0.0 && bearing != -180.0) {
			if (bearing < 0) {
				bearing = bearing + 360;
			}
			positionManager.refreshSensorData(bearing, pitch, rotation,
					accuracy);
		}
	}

	private float[] calculateOrientation() {
		float[] values = new float[3];
		float[] R = new float[9];
		float[] outR = new float[9];
		float[] i = new float[9];

		SensorManager.getRotationMatrix(R, i, aValues, mValues);
		SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X,
				SensorManager.AXIS_Z, outR);

		SensorManager.getOrientation(outR, values);

		values[0] = (float) Math.toDegrees(values[0]);
		values[1] = (float) Math.toDegrees(values[1]);
		values[2] = (float) Math.toDegrees(values[2]);

		return values;
	}

}
