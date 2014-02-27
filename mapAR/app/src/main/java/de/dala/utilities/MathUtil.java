package de.dala.utilities;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

import de.dala.common.OrientationPoint;

public class MathUtil {

	private static final int DEF_DIV_SCALE = 10;

	private MathUtil() {
	}

	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static OrientationPoint movingAverage(
			LinkedList<OrientationPoint> valueList) {
		synchronized (valueList) {
			if (valueList.isEmpty()) {
				return null;
			}
			double[] sumArray = new double[3];
			int m = 0;
			Iterator<OrientationPoint> i = valueList.iterator();
			while (i.hasNext()) {
				OrientationPoint point = i.next();
				sumArray[0] += point.bearing;
				sumArray[1] += point.pitch;
				sumArray[2] += point.rotation;
				m++;
			}

			sumArray[0] = (m != 0) ? sumArray[0] / m : sumArray[0];
			sumArray[1] = (m != 0) ? sumArray[1] / m : sumArray[1];
			sumArray[2] = (m != 0) ? sumArray[2] / m : sumArray[2];

			OrientationPoint point = new OrientationPoint(sumArray[0],
					sumArray[1], sumArray[2]);
			return point;
		}
	}

	/**
	 * same as movingAverage but deals with angles, so 1� and 359� would result
	 * in 0� instead of 180�
	 * 
	 * @param valueList
	 * @return
	 */
	public static OrientationPoint movingAverage2(
			LinkedList<OrientationPoint> valueList) {
		synchronized (valueList) {
			if (valueList.isEmpty()) {
				return null;
			}
			double[] sumArray = new double[4];
			int m = 0;
			Iterator<OrientationPoint> i = valueList.iterator();
			while (i.hasNext()) {
				OrientationPoint point = i.next();
				sumArray[0] += Math.sin(Math.toRadians(point.bearing));
				sumArray[1] += Math.cos(Math.toRadians(point.bearing));
				sumArray[2] += point.pitch;
				sumArray[3] += point.rotation;
				m++;
			}

			sumArray[2] = (m != 0) ? sumArray[2] / m : sumArray[2];
			sumArray[3] = (m != 0) ? sumArray[3] / m : sumArray[3];

			/*
			 * avoid negative bearing
			 */
			double bearing = (Math.toDegrees(Math.atan2(sumArray[0],
					sumArray[1])) + 360) % 360;

			OrientationPoint point = new OrientationPoint(bearing, sumArray[2],
					sumArray[3]);
			return point;
		}
	}

	public static OrientationPoint weightedMovingAverage(
			LinkedList<OrientationPoint> valueList) {
		synchronized (valueList) {
			if (valueList.isEmpty()) {
				return null;
			}
			double[] sumArray = new double[3];
			double m = 0;
			double size = triangularNumber(valueList.size());
			Iterator<OrientationPoint> i = valueList.iterator();
			while (i.hasNext()) {
				OrientationPoint point = i.next();
				sumArray[0] += point.bearing * ((m + 1) / size);
				sumArray[1] += point.pitch * ((m + 1) / size);
				sumArray[2] += point.rotation * ((m + 1) / size);
				m++;
			}
			OrientationPoint point = new OrientationPoint(sumArray[0],
					sumArray[1], sumArray[2]);
			return point;
		}
	}

	/**
	 * same as weightedMovingAverage but deals with angles, so 1� and 359� would
	 * result in 0� instead of 180�
	 * 
	 * @param valueList
	 * @return
	 */
	public static OrientationPoint weightedMovingAverage2(
			LinkedList<OrientationPoint> valueList) {
		synchronized (valueList) {
			if (valueList.isEmpty()) {
				return null;
			}
			double[] sumArray = new double[4];
			int m = 0;
			double size = triangularNumber(valueList.size());
			Iterator<OrientationPoint> i = valueList.iterator();
			while (i.hasNext()) {
				OrientationPoint point = i.next();
				for (int j = 0; j < m + 1; j++) {
					sumArray[0] += Math.sin(Math.toRadians(point.bearing));
					sumArray[1] += Math.cos(Math.toRadians(point.bearing));
				}
				sumArray[2] += point.pitch * ((m + 1) / size);
				sumArray[3] += point.rotation * ((m + 1) / size);
				m++;
			}

			/*
			 * avoid negative bearing
			 */
			double bearing = (Math.toDegrees(Math.atan2(sumArray[0],
					sumArray[1])) + 360) % 360;

			OrientationPoint point = new OrientationPoint(bearing, sumArray[2],
					sumArray[3]);
			return point;
		}
	}

	/**
	 * Dreiecksnummern berechnen
	 * 
	 * @param n
	 * @return
	 */
	public static int triangularNumber(int n) {
		int fact = 1;
		for (int i = 1; i <= n; i++) {
			fact += i;
		}
		return fact;
	}

}
