package de.dala.utilities;

import android.graphics.Color;

public class ColorManager {

	public static final int alpha = 120;
	private static int counter = 0;

	public static int getNewColor() {
		// return _kellysMaxContrastSet[counter++ %
		// _kellysMaxContrastSet.length];
		return _boyntonOptimized[counter++ % _boyntonOptimized.length];
	}

	/**
	 * based on
	 * http://stackoverflow.com/questions/470690/how-to-automatically-generate
	 * -n-distinct-colors
	 */
	private static int[] _kellysMaxContrastSet = new int[] {
			UIntToColor(0xFFFFB300), // Vivid Yellow
			UIntToColor(0xFF803E75), // Strong Purple
			UIntToColor(0xFFFF6800), // Vivid Orange
			UIntToColor(0xFFA6BDD7), // Very Light Blue
			UIntToColor(0xFFC10020), // Vivid Red
			UIntToColor(0xFFCEA262), // Grayish Yellow
			UIntToColor(0xFF817066), // Medium Gray

			// The following will not be good for people with defective color
			// vision
			UIntToColor(0xFF007D34), // Vivid Green
			UIntToColor(0xFFF6768E), // Strong Purplish Pink
			UIntToColor(0xFF00538A), // Strong Blue
			UIntToColor(0xFFFF7A5C), // Strong Yellowish Pink
			UIntToColor(0xFF53377A), // Strong Violet
			UIntToColor(0xFFFF8E00), // Vivid Orange Yellow
			UIntToColor(0xFFB32851), // Strong Purplish Red
			UIntToColor(0xFFF4C800), // Vivid Greenish Yellow
			UIntToColor(0xFF7F180D), // Strong Reddish Brown
			UIntToColor(0xFF93AA00), // Vivid Yellowish Green
			UIntToColor(0xFF593315), // Deep Yellowish Brown
			UIntToColor(0xFFF13A13), // Vivid Reddish Orange
			UIntToColor(0xFF232C16), // Dark Olive Green
	};

	private static int[] _boyntonOptimized = new int[] {
			Color.argb(alpha, 0, 0, 255), // Blue
			Color.argb(alpha, 255, 0, 0), // Red
			Color.argb(alpha, 0, 255, 0), // Green
			Color.argb(alpha, 255, 255, 0), // Yellow
			Color.argb(alpha, 255, 0, 255), // Magenta
			Color.argb(alpha, 255, 128, 128), // Pink
			Color.argb(alpha, 128, 128, 128), // Gray
			Color.argb(alpha, 128, 0, 0), // Brown
			Color.argb(alpha, 255, 128, 0), // Orange
	};

	public static int UIntToColor(int color) {
//		byte a = (byte) (color >> 24); // unused
		byte r = (byte) (color >> 16);
		byte g = (byte) (color >> 8);
		byte b = (byte) (color >> 0);
		return Color.argb(alpha, r, g, b);
	}
}
