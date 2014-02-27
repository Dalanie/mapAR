package de.dala;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import de.dala.database.DatabaseHandler;
import de.dala.network.VolleySingleton;
import de.dala.utilities.PrefUtilities;
import de.dala.utilities.SystemProperties;

public class MapARApplication extends Application {
	public static final String APP_ID = "de.uni_bremen.mapAR";

	public void onCreate() {
        VolleySingleton.init(this);
        PrefUtilities.init(this);
        DatabaseHandler.init(this);

        int measuredWidth;
        int measuredHeight;

		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = wm.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight();
        }

		/*
		 * Open SharePreference file
		 */
		SharedPreferences settings = getSharedPreferences(
				SystemProperties.SYSTEM_PREFERENCE_FILE, Context.MODE_PRIVATE);

		/*
		 * Get the editor for preference file
		 */
		SharedPreferences.Editor editor = settings.edit();
		/*
		 * Assign the value for dimension of devices
		 */
		editor.putInt(SystemProperties.DISPLAY_WIDTH, measuredWidth);
		editor.putInt(SystemProperties.DISPLAY_HEIGHT, measuredHeight);
		editor.putInt(SystemProperties.OBSERVER_HEIGHT, 155);

		Camera mCamera = Camera.open();
		Camera.Parameters params = mCamera.getParameters();
		editor.putFloat(SystemProperties.CAMERA_VERTICAL_VIEW_ANGLE,
				params.getVerticalViewAngle());
		editor.putFloat(SystemProperties.CAMERA_HORIZONTAIL_VIEW_ANGLE,
				params.getHorizontalViewAngle());
		editor.putFloat(SystemProperties.CAMERA_FOCAL_LENGTH, mCamera
				.getParameters().getFocalLength());
		mCamera.release();

		/*
		 * commit editor to save into file
		 */
		editor.commit();
	};

}
