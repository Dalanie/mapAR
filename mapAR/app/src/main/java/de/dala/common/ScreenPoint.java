package de.dala.common;

import java.io.Serializable;

/**
 * This class provides the information of a pixel of the display
 * 
 * @author Daniel
 * 
 */
public class ScreenPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3716849831807519815L;
	private double x;
	private double y;

	public ScreenPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
