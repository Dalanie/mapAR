package de.dala.utilities;

import de.dala.common.GPSPoint;

public class SystemProperties {
	// ######## String tag for preference properties #######
	// preference file name
	public static final String SYSTEM_PREFERENCE_FILE = "mapit_reverse_prefs";

	// the dimension of phone display
	public static final String DISPLAY_WIDTH = "display_width";
	public static final String DISPLAY_HEIGHT = "display_height";

	// the observer height
	public static final String OBSERVER_HEIGHT = "observer_height";

	// camera view angles (Float)
	public static final String CAMERA_VERTICAL_VIEW_ANGLE = "camera_vertical_view_angle";
	public static final String CAMERA_HORIZONTAIL_VIEW_ANGLE = "camera_horizontal_view_angle";
	public static final String CAMERA_PREVIEW_WIDTH = "camera_preview_width";
	public static final String CAMERA_PREVIEW_HEIGHT = "camera_preview_height";
	public static final String CAMERA_FOCAL_LENGTH = "camera_focal_length";

	// the dimension of photo
	public static final String PHOTO_WIDTH = "photo_width";
	public static final String PHOTO_HEIGHT = "photo_height";

	// distance from camera to center point in virtual cs (Float)
	public static final String CAMERA_DISTANCE_IN_PIXEL = "camera_distance_in_pixel";

	// the properties for location provider
	// every 10 ms being forced to get new location
	public static final int LOCATION_MIN_TIME = 10;
	// regardless of location changing
	public static final int LOCATION_MIN_DISTANCE = 0;

	public static final String SYS_WAKE_LOCK = "sysWakeLock";

	public static final int MAX_LIST_SIZE = 15;

	/*
	 * geographic properties
	 */
	public static final double EARTH_RADIUS = 6371004.00;

	// default location for location provider
	public static final double BREMEN_LAT = 53.0884572;
	public static final double BREMEN_LON = 8.8556671;

	//Parking Spot Unimelb
	public static final GPSPoint INDOOR_POSITION = new GPSPoint(-37.812198,
			145.012490, 155);

	// orientation types
	public static final String ORIENTATION_BEARING_TYPE = "bearing";
	public static final String ORIENTATION_PITCH_TYPE = "pitch";
	public static final String ORIENTATION_ROTATE_TYPE = "rotate";

	// distance tolerance for Douglas generalization
	public static final float DISTANCE_TOLERANCE = 30.0f;

	// the dimension of canvas for touching pick, according to the display
	// size(integer)
	public static final String PICK_CANVAS_WIDTH = "pick_canvas_width";
	public static final String PICK_CANVAS_HEIGHT = "pick_canvas_height";

}
