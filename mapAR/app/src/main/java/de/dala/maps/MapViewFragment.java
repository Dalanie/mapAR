package de.dala.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import de.dala.MainActivity;
import de.dala.PositionManager;
import de.dala.R;
import de.dala.common.GPSPoint;
import de.dala.common.MapItObjectExtension;
import de.dala.utilities.SystemProperties;


public class MapViewFragment extends BasicMapViewFragment {

	private static View view;
	private MainActivity parentActivity;

	private GoogleMap mMap;
	private Marker myPositionMarker;
	public static GPSPoint MY_POSITION = SystemProperties.INDOOR_POSITION;

	public MapViewFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Activity activity = getActivity();
		if (activity instanceof MainActivity) {
			parentActivity = (MainActivity) activity;
		} else {
			Log.e("ProjectionViewFragment",
					"ParentActivity is not MainActivity. Big Error!");
			throw new ClassCastException();
		}
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			Log.e("InflateException", e.getMessage());
			/*
			 * map is already there, just return view as it is
			 */
		}
		return view;
	}

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_map_actionbar, menu);
    }


	private void setUpMapIfNeeded() {
		/*
		 * Do a null check to confirm that we have not already instantiated the
		 * map.
		 */
		if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
			if (mapFragment != null) {
				mMap = mapFragment.getMap();
				mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

					@Override
					public void onMarkerDragEnd(Marker marker) {
						if (marker
								.equals(MapViewFragment.this.myPositionMarker)) {
							MY_POSITION = new GPSPoint(
									marker.getPosition().latitude, marker
											.getPosition().longitude);
						}
					}

					@Override
					public void onMarkerDrag(Marker arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMarkerDragStart(Marker arg0) {
						// TODO Auto-generated method stub

					}
				});
				mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

					@Override
					public void onMapClick(LatLng myPosition) {
						MarkerOptions options = new MarkerOptions().position(
								myPosition).draggable(true);
						if (MapViewFragment.this.myPositionMarker == null) {
							Marker marker = mMap.addMarker(options);
							MapViewFragment.this.myPositionMarker = marker;
						} else {
							MapViewFragment.this.myPositionMarker
									.setPosition(myPosition);
							MY_POSITION = new GPSPoint(myPosition.latitude,
									myPosition.longitude);
						}
					}
				});
			}
			/*
			 * Check if we were successful in obtaining the map.
			 */
			if (mMap != null) {
				/*
				 * The Map is verified. It is now safe to manipulate the map.
				 */
				// mMap.setMyLocationEnabled(true);

				/*
				 * default camera on start
				 */
				boolean alreadyMoved = false;
				if (parentActivity != null) {
					PositionManager positionManager = parentActivity
							.getPositionManager();
					if (positionManager != null) {
						GPSPoint currentPosition = positionManager
								.getLastLocation();
						if (currentPosition != null) {
							CameraUpdate cameraOnStart = CameraUpdateFactory
									.newLatLngZoom(new LatLng(
                                            currentPosition.latitude,
                                            currentPosition.longitude),
                                            zoomOnStart);
							mMap.animateCamera(cameraOnStart);
							alreadyMoved = true;
						}
					}
				}
				if (!alreadyMoved) {
					CameraUpdate cameraOnStart = CameraUpdateFactory
							.newLatLngZoom(defaultCenter, zoomOnStart);
					mMap.moveCamera(cameraOnStart);
				}

				List<Polygon> polygonMarkers = new ArrayList<Polygon>();
				if (parentActivity != null) {
					List<MapItObjectExtension> mapItObjects = parentActivity
							.getMapItObjects();
					for (MapItObjectExtension mapItObject : mapItObjects) {
						if (mapItObject.polygonPoints != null) {
							List<LatLng> list = new ArrayList<LatLng>();
							for (GPSPoint point : mapItObject.polygonPoints) {
								LatLng latLngPoint = new LatLng(point.latitude,
										point.longitude);
								list.add(latLngPoint);
							}
							PolygonOptions polygonOption = new PolygonOptions();
							polygonOption.addAll(list);
							polygonOption.strokeColor(Color.BLACK);
							polygonOption.fillColor(mapItObject.getDrawColor());
							polygonOption.visible(true);
							polygonOption.strokeWidth(POLYGON_STROKE_WIDTH);
							polygonMarkers.add(mMap.addPolygon(polygonOption));
						}
					}
				}
			}
		}

		myLocationAndRotationButtonFix();
	}

	private void myLocationAndRotationButtonFix() {
		// Gets the my location button
		View myLocationButton = getView().findViewById(R.id.map)
				.findViewById(2);

		// Checks if we found the my location button
		if (myLocationButton != null) {
			int actionBarHeight = parentActivity.getActionBarHeight();

			// Sets the margin of the button
			ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(
					myLocationButton.getLayoutParams());
			marginParams.setMargins(0, actionBarHeight + 20, 20, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					marginParams);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			myLocationButton.setLayoutParams(layoutParams);
		}
	}

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_projection:
                mCallback.onProjectionClicked();
                break;
            case R.id.action_sat:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.action_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
        return true;
    }
}
