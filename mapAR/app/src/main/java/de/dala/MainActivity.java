package de.dala;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.dala.common.EvaluationPicture;
import de.dala.common.MapItObject;
import de.dala.common.MapItObjectExtension;
import de.dala.common.PolygonNetworkWrapper;
import de.dala.database.DatabaseHandler;
import de.dala.database.IDatabaseHandler;
import de.dala.maps.BasicMapViewFragment;
import de.dala.maps.MapViewFragment;
import de.dala.maps.OSMapViewFragment;
import de.dala.network.INetworkCommunication;
import de.dala.network.NetworkCommunication;
import de.dala.utilities.DialogUtilities;
import de.dala.utilities.PrefUtilities;
import de.dala.utilities.SystemProperties;

@SuppressLint("NewApi")
public class MainActivity extends SherlockFragmentActivity implements IPositionActivity,
        ISensorActivity, ProjectionViewFragment.OnMapClickListener,
        MapViewFragment.OnProjectionClickListener {

    private static final int OPEN_STREET_MAPS = 1;
    private static final int GOOGLE_MAPS = 0;
    private ProjectionViewFragment projectionViewFragment;
    private BasicMapViewFragment mapViewFragment;

    private boolean positionTrackingEnabled = true;
    private PositionManager positionManager;

    private Toast currentToast;

    private List<MapItObjectExtension> mapItObjects;
    private IDatabaseHandler database;
    private INetworkCommunication networkCommunication;

    private int actionBarHeight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
        hideStatusbar();
        setupActionBar();

        database = DatabaseHandler.getInstance();
        networkCommunication = new NetworkCommunication();
        // networkCommunication = new MockNetworkCommunication();
        reloadMapItObjects();
        positionManager = new PositionManager(this, this);
        positionManager.initSensors();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    getResources().getDisplayMetrics());
        }
        onProjectionClicked();
    }

    private void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_main_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load_polygons_url:
                final EditText editText = new EditText(this);
                editText.setText("http://daniellangerenken.de/mapittest");
                DialogUtilities.showDialog(this, "Load Polygons", "Select url",
                        editText, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        networkCommunication.getObjectsFromURL(
                                new Response.Listener<ArrayList<PolygonNetworkWrapper>>(){
                                    @Override
                                    public void onResponse(ArrayList<PolygonNetworkWrapper> polygonNetworkWrappers) {
                                        dropMapItObjects();
                                        if (polygonNetworkWrappers != null){
                                            for (PolygonNetworkWrapper polygon : polygonNetworkWrappers) {
                                                database.addMapItObject(
                                                        new MapItObject(polygon));
                                            }
                                            reloadMapItObjects();
                                        }
                                        toast("Polygons downloaded.");
                                    }
                                }, new Response.ErrorListener(){
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        if (volleyError != null) {
                                            toast(volleyError.toString());
                                        } else {
                                            toast("Sorry. An error occured");
                                        }
                                    }

                                }, editText
                                .getText().toString());
                    }
                });
                break;
            case R.id.action_set_height:
                final EditText heightEditText = new EditText(this);
                heightEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                int observerHeight = PrefUtilities.getInstance().getObserverHeight();
                heightEditText.setText("" + observerHeight);
                DialogUtilities.showDialog(this, "Adjust projection",
                        "Camera height in cm :", heightEditText,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int newHeight = Integer.parseInt(heightEditText
                                            .getText().toString());
                                    if (newHeight > 0) {
                                        PrefUtilities.getInstance().setObserverHeight(newHeight);
                                    }
                                } catch (ParseException e) {
                                    toast("Please enter valid camera height in cm (e.g.: 140)");
                                }
                            }
                        });
            case R.id.last_polygon_debug:
                List<EvaluationPicture> pictures = database.getAllPictures();
                if (pictures != null && pictures.size() > 0){
                    toast(pictures.get(0).toString());
                }
        }
        return false;
    }



    /**
     * based on
     * http://stackoverflow.com/questions/16384866/hide-navigation-bar-but
     * -show-actionbar-in-android-3-0
     *
     * Hides the statusbar, but keeps the actionbar
     */
    private void hideStatusbar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void gpsStatusChanged(Location newLocation) {
        if (projectionViewFragment != null) {
            projectionViewFragment.gpsStatusChanged(newLocation);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (positionTrackingEnabled) {
            if (positionManager != null) {
                positionManager.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (positionTrackingEnabled) {
            if (positionManager != null) {
                positionManager.onPause();
            }
        }
    }

    /**
     * Toasts a message, cancels the previous one
     *
     * @param text
     *            - Text which should appear as a toast
     */
    public void toast(String text) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (projectionViewFragment != null) {
                    projectionViewFragment.keyCodeDown();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (projectionViewFragment != null) {
                    projectionViewFragment.keyCodeUp();
                }
                return true;
        }
        return false;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    public PositionManager getPositionManager() {
        return positionManager;
    }


    public List<MapItObjectExtension> getMapItObjects() {
        return mapItObjects;
    }

    @Override
    public void sensorStatusChanged(double bearing, double pitch,
                                    double rotation, int accuracy) {
        if (projectionViewFragment != null) {
            projectionViewFragment.sensorStatusChanged(bearing, pitch,
                    rotation, accuracy);
        }
    }

    private void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }

    public int getActionBarHeight() {
        return actionBarHeight;
    }

    @Override
    public void onMapClicked() {
        new AlertDialog.Builder(this).setItems(R.array.map_provider,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case GOOGLE_MAPS:
                                mapViewFragment = new MapViewFragment();
                                break;
                            case OPEN_STREET_MAPS:
                                mapViewFragment = new OSMapViewFragment();
                                break;
                        }
                        changeFragment(mapViewFragment);
                    }
                }).show();
    }

    @Override
    public void onProjectionClicked() {
        if (projectionViewFragment == null) {
            projectionViewFragment = new ProjectionViewFragment();
        }
        changeFragment(projectionViewFragment);
    }

    public void reloadMapItObjects() {
        List<MapItObjectExtension> tempList = database.getAllMapItObjects();
        if (mapItObjects == null) {
            mapItObjects = tempList;
        } else {
			/*
			 * if list exists, keep references
			 */
            mapItObjects.clear();
            mapItObjects.addAll(tempList);
        }
        if (projectionViewFragment != null) {
            projectionViewFragment.mapItObjectsReloaded();
        }
    }

    public void dropMapItObjects() {
        database.removeAllMapItObjects();
        reloadMapItObjects();
    }

}
