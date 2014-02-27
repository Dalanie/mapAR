package de.dala;

import java.util.LinkedList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import de.dala.common.GPSPoint;
import de.dala.common.OrientationPoint;
import de.dala.listener.MapARLocationListener;
import de.dala.listener.MapARSensorListener;
import de.dala.utilities.LimitedLinkedList;
import de.dala.utilities.SystemProperties;

public class PositionManager {
	/*
	 * LocationManager, Provider and Listener
	 */
	public String locationProvider;
	private LocationManager locationManager;
	private MapARLocationListener locationListener;

	/*
	 * SensorManager and Listener
	 */
	private SensorManager sensorManager;
	private MapARSensorListener sensorListener;
	private Sensor accelerometer;
	private Sensor magField;

	/*
	 * Position data containers
	 */
	public LinkedList<OrientationPoint> orientationList = new LimitedLinkedList<OrientationPoint>(
			SystemProperties.MAX_LIST_SIZE);
	public LinkedList<Location> locationList = new LimitedLinkedList<Location>(
			SystemProperties.MAX_LIST_SIZE);

	private WakeLock wakeLock;

	private IPositionActivity positionActivity;
	private ISensorActivity sensorActivity;
	private Context context;

	private static final String TAG = PositionManager.class.getSimpleName();

	public PositionManager(IPositionActivity positionActivity,
			ISensorActivity sensorActivity) {
		this.context = positionActivity.getContext();
		this.positionActivity = positionActivity;
		this.sensorActivity = sensorActivity;
		sensorListener = new MapARSensorListener(this);
		locationListener = new MapARLocationListener(this);
	}

	/**
	 * This method should be called, if the activity, which uses the
	 * positionManager, is resumed
	 */
	@SuppressWarnings("deprecation")
	public void onResume() {
		orientationList.clear();
		locationList.clear();

		if (locationProvider != null) {
			/*
			 * Register the LocationListener and Provider
			 */
			locationManager.requestLocationUpdates(locationProvider,
					SystemProperties.LOCATION_MIN_TIME,
					SystemProperties.LOCATION_MIN_DISTANCE, locationListener);
		}
		/*
		 * Register the compass sensor listener
		 */
		sensorManager.registerListener(sensorListener, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(sensorListener, magField,
				SensorManager.SENSOR_DELAY_UI);

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				SystemProperties.SYS_WAKE_LOCK);
		wakeLock.acquire();
	}

	/**
	 * This method should be called, if the activity, which uses the
	 * positionManager, is paused
	 */
	public void onPause() {
		locationManager.removeUpdates(locationListener);
		sensorManager.unregisterListener(sensorListener);
		wakeLock.release();
	}

	public void initSensors() {
		if (locationManager == null) {
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}

		/*
		 * Assign Criteria for location Sensor
		 */
		Criteria criteria = new Criteria();

		/*
		 * Only GPS result considered here
		 */
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		/*
		 * Other Location result avoided
		 */
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);

		/*
		 * Get Best Location Provider
		 */
		locationProvider = locationManager.getBestProvider(criteria, true);

		Log.d(TAG, "locationProvider=" + locationProvider);

		/*
		 * Initialize a Location Listener
		 */
		locationListener = new MapARLocationListener(this);

		if (sensorManager == null) {
			/*
			 * Initialize the Sensor Manager
			 */
			sensorManager = (SensorManager) context
					.getSystemService(Context.SENSOR_SERVICE);
		}
		/*
		 * Initialize a SensorListener
		 */
		sensorListener = new MapARSensorListener(this);

		/*
		 * Initialize the Sensor Objects from SensorManager
		 */
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public String getLocationProvider() {
		return locationProvider;
	}

	public GPSPoint getLastLocation() {
		if (locationList == null || locationList.isEmpty()) {
			/*
			 * if no location available, use indoor position (maybe no gps
			 * reception (indoor))
			 */
			return SystemProperties.INDOOR_POSITION;
		} else {
			return new GPSPoint(locationList.getLast());
		}
	}

	public void setLocationProvider(String locationProvider) {
		this.locationProvider = locationProvider;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public MapARLocationListener getLocationListener() {
		return locationListener;
	}

	public void setLocationListener(MapARLocationListener locationListener) {
		this.locationListener = locationListener;
	}

	public SensorManager getSensorManager() {
		return sensorManager;
	}

	public void setSensorManager(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	public MapARSensorListener getSensorListener() {
		return sensorListener;
	}

	public void setSensorListener(MapARSensorListener sensorListener) {
		this.sensorListener = sensorListener;
	}

	public Sensor getAccelerometer() {
		return accelerometer;
	}

	public void setAccelerometer(Sensor accelerometer) {
		this.accelerometer = accelerometer;
	}

	public Sensor getMagField() {
		return magField;
	}

	public void setMagField(Sensor magField) {
		this.magField = magField;
	}

	public LinkedList<OrientationPoint> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(LinkedList<OrientationPoint> orientationList) {
		this.orientationList = orientationList;
	}

	public LinkedList<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(LinkedList<Location> locationList) {
		this.locationList = locationList;
	}

	public WakeLock getWakeLock() {
		return wakeLock;
	}

	public void setWakeLock(WakeLock wakeLock) {
		this.wakeLock = wakeLock;
	}

	public IPositionActivity getPositionActivity() {
		return positionActivity;
	}

	public void setPositionActivity(IPositionActivity positionActivity) {
		this.positionActivity = positionActivity;
	}

	public ISensorActivity getSensorActivity() {
		return sensorActivity;
	}

	public void setPositionActivity(ISensorActivity sensorActivity) {
		this.sensorActivity = sensorActivity;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void gpsStatusChanged(Location newLocation) {
		positionActivity.gpsStatusChanged(newLocation);
	}

	public void refreshSensorData(double bearing, double pitch,
			double rotation, int accuracy) {
		if (sensorActivity != null) {
			orientationList.add(new OrientationPoint(bearing, pitch, rotation,
					accuracy));
			sensorActivity.sensorStatusChanged(bearing, pitch, rotation,
					accuracy);
		}
	}

	public void addOrientationBean(OrientationPoint orientationPoint) {
		orientationList.add(orientationPoint);
	}

}
