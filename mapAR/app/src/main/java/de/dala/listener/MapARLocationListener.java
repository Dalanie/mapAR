package de.dala.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import de.dala.PositionManager;
import de.dala.utilities.SystemProperties;

public class MapARLocationListener implements LocationListener {

	private PositionManager positionManager;

	private static final String TAG = MapARLocationListener.class
			.getSimpleName();

	public MapARLocationListener(PositionManager positionManager) {
		this.positionManager = positionManager;
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		positionManager.getLocationList().add(newLocation);
		Log.d(TAG, String.format(
				"Latitude: %s Longitude: %s Altitude: %s Accuracy: %s",
				newLocation.getLatitude() + "",
				newLocation.getLongitude() + "",
				newLocation.getAltitude() + "", newLocation.getAccuracy() + ""));
		positionManager.gpsStatusChanged(newLocation);
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (positionManager.locationProvider.equals(provider)) {
			positionManager.getLocationManager().removeUpdates(this);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (positionManager.getLocationProvider().equals(provider)) {
			positionManager.getLocationManager().requestLocationUpdates(
					positionManager.locationProvider,
					SystemProperties.LOCATION_MIN_TIME,
					SystemProperties.LOCATION_MIN_DISTANCE, this);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
