package de.dala.smoothing.sensor;

public abstract class BasicSmoothingSensorMethod implements
		ISmoothingSensorDataMethod {

	@Override
	public String toString() {
		return getName();
	}

	private static final float SMOOTH_THRESHOLD = 60;

	/**
	 * Applies a lowpass filter to the change in the lecture of the sensor
	 * http:/
	 * /stackoverflow.com/questions/4699417/android-compass-orientation-on-
	 * unreliable-low-pass-filter appunta.com
	 * 
	 * @param newValue
	 *            the new sensor value
	 * @param lowValue
	 *            the old sensor value
	 * @return and intermediate value
	 */
	public double lowPass(double newValue, double lowValue, double smoothFactor) {
		double resultValue = 0;
		if (Math.abs(newValue - lowValue) < 180) {
			if (Math.abs(newValue - lowValue) > SMOOTH_THRESHOLD) {
				resultValue = newValue;
			} else {
				resultValue = lowValue + smoothFactor * (newValue - lowValue);
			}
		} else {
			if (360 - Math.abs(newValue - lowValue) > SMOOTH_THRESHOLD) {
				resultValue = newValue;
			} else {
				if (lowValue > newValue) {
					resultValue = (lowValue + smoothFactor
							* ((360 + newValue - lowValue) % 360) + 360) % 360;
				} else {
					resultValue = (lowValue - smoothFactor
							* ((360 - newValue + lowValue) % 360) + 360) % 360;
				}
			}
		}
		return resultValue;
	}
}
