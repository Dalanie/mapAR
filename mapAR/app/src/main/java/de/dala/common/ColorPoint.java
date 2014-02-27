package de.dala.common;

import android.graphics.Point;

public class ColorPoint extends Point {
	public int color;

	public ColorPoint(Point p, int color) {
		super(p);
		this.color = color;
	}
	
	public ColorPoint(int x, int y, int color) {
		super(x,y);
		this.color = color;
	}
}
