package de.dala.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Daniel on 09.01.14.
 */
public class PrefUtilities {
    private SharedPreferences preferences;
    private static PrefUtilities _instance;

    private static final String SERVER_URL = "server_url";

    private PrefUtilities(Context context){
        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }



    public static void init(Context context) {
        _instance = new PrefUtilities(context);
    }

    public static PrefUtilities getInstance(){
        return _instance;
    }

    public int getObserverHeight(){
        return preferences.getInt(SystemProperties.OBSERVER_HEIGHT, 155);
    }

    public void setObserverHeight(int height){
        preferences.edit().putInt(SystemProperties.OBSERVER_HEIGHT, height).commit();
    }

    public void setServerUrl(String url){
        preferences.edit().putString(SERVER_URL, url).commit();
    }

    public String getServerURL() {
        return preferences.getString(SERVER_URL, CommonUtilities.SERVER_URL);
    }

    public int getDisplayWidth(){
        return preferences.getInt(SystemProperties.DISPLAY_WIDTH, 0);
    }

    public void setDisplayWidth(int width){
        preferences.edit().putInt(SystemProperties.DISPLAY_WIDTH, width).commit();
    }

    public int getDisplayHeight(){
        return preferences.getInt(SystemProperties.DISPLAY_HEIGHT, 0);
    }

    public void setDisplayHeight(int height){
        preferences.edit().putInt(SystemProperties.DISPLAY_HEIGHT, height).commit();
    }

    public float getCameraVerticalViewAngle(){
        return preferences.getFloat(SystemProperties.CAMERA_VERTICAL_VIEW_ANGLE, 0);
    }

    public void setCameraVerticalViewAngle(float angle){
        preferences.edit().putFloat(SystemProperties.CAMERA_VERTICAL_VIEW_ANGLE, angle).commit();
    }

    public float getCameraHorizontalViewAngle(){
        return preferences.getFloat(SystemProperties.CAMERA_HORIZONTAIL_VIEW_ANGLE, 0);
    }

    public void setCameraHorizontalViewAngle(float angle){
        preferences.edit().putFloat(SystemProperties.CAMERA_HORIZONTAIL_VIEW_ANGLE, angle).commit();
    }

    public float getCameraDistanceInPixel(){
        return preferences.getFloat(SystemProperties.CAMERA_DISTANCE_IN_PIXEL, 0);
    }

    public void setCameraDistanceInPixel(float angle){
        preferences.edit().putFloat(SystemProperties.CAMERA_DISTANCE_IN_PIXEL, angle).commit();
    }

    public float getCameraFocalLength(){
        return preferences.getFloat(SystemProperties.CAMERA_FOCAL_LENGTH, -1);
    }

    public void setCameraFocalLength(float length){
        preferences.edit().putFloat(SystemProperties.CAMERA_FOCAL_LENGTH, length).commit();
    }

}
