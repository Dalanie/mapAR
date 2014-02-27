package de.dala.maps;

import java.util.List;


import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import de.dala.MainActivity;
import de.dala.PositionManager;
import de.dala.R;
import de.dala.common.GPSPoint;
import de.dala.common.MapItObjectExtension;

public class OSMapViewFragment extends BasicMapViewFragment {

	private static View view;
	private MainActivity parentActivity;

	private MapView mMap;
	protected MyLocationNewOverlay mLocationOverlay;
	protected ResourceProxy mResourceProxy;
	private CompassOverlay mCompassOverlay;
	private MinimapOverlay mMinimapOverlay;
	private ScaleBarOverlay mScaleBarOverlay;

	public OSMapViewFragment() {
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
			view = inflater.inflate(R.layout.fragment_osmap, container, false);
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
		final DisplayMetrics dm = parentActivity.getResources()
				.getDisplayMetrics();
		/*
		 * Do a null check to confirm that we have not already instantiated the
		 * map.
		 */
		if (mMap == null) {
			mMap = (MapView) view.findViewById(R.id.osmap);
			/*
			 * Check if we were successful in obtaining the map.
			 */
			if (mMap != null) {
				mMap.setTileSource(TileSourceFactory.MAPNIK);
				mResourceProxy = new ResourceProxyImpl(parentActivity);
				this.mMap.setBuiltInZoomControls(true);
				this.mMap.setMultiTouchControls(true);
				mMap.getController().setZoom(1);
				mMap.scrollTo(0, 0);
				/*
				 * only do static initialisation if needed
				 */
				if (CloudmadeUtil.getCloudmadeKey().length() == 0) {
					CloudmadeUtil.retrieveCloudmadeKey(parentActivity);
				}

				this.mCompassOverlay = new CompassOverlay(parentActivity,
						new InternalCompassOrientationProvider(parentActivity),
						mMap);
				this.mLocationOverlay = new MyLocationNewOverlay(
						parentActivity, new GpsMyLocationProvider(
								parentActivity), mMap);
				mMinimapOverlay = new MinimapOverlay(getActivity(),
						mMap.getTileRequestCompleteHandler());
				mMinimapOverlay.setWidth(dm.widthPixels / 5);
				mMinimapOverlay.setHeight(dm.heightPixels / 5);

				mScaleBarOverlay = new ScaleBarOverlay(parentActivity);
				mScaleBarOverlay.setCentred(true);
				mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

				mLocationOverlay.enableMyLocation(new GpsMyLocationProvider(
						parentActivity));
				mCompassOverlay
						.enableCompass(new InternalCompassOrientationProvider(
								parentActivity));

				mMap.getOverlays().add(this.mLocationOverlay);
				mMap.getOverlays().add(this.mCompassOverlay);
				mMap.getOverlays().add(this.mMinimapOverlay);
				mMap.getOverlays().add(this.mScaleBarOverlay);
				mMap.getController().setZoom(ZOOM);

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
							mMap.getController().animateTo(
									currentPosition.latitude,
									currentPosition.longitude);
							alreadyMoved = true;
						}
					}
				}
				if (!alreadyMoved) {
					mMap.getController().animateTo(defaultCenter.latitude,
							defaultCenter.longitude);
				}

				if (parentActivity != null) {
					List<MapItObjectExtension> mapItObjects = parentActivity
							.getMapItObjects();
					for (MapItObjectExtension mapItObject : mapItObjects) {
						if (mapItObject.polygonPoints != null) {
							PathOverlay myOverlay = new PathOverlay(
									mapItObject.getDrawColor(), parentActivity);
							myOverlay.getPaint().setStyle(Paint.Style.FILL);
							for (GPSPoint point : mapItObject.polygonPoints) {
								myOverlay.addPoint(new GeoPoint(point.latitude,
										point.longitude));
							}
							mMap.getOverlays().add(myOverlay);
						}
					}
				}
			}
		}
	}

}
