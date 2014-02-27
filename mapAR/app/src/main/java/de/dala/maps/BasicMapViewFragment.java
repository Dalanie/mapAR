package de.dala.maps;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.model.LatLng;

import de.dala.R;
import de.dala.utilities.SystemProperties;


public abstract class BasicMapViewFragment extends SherlockFragment {
	/**
	 * width of the polygon border
	 */
	public static final int POLYGON_STROKE_WIDTH = 2;

	/**
	 * equivalent to #df8b28
	 */
	public static final int FILL_COLOR = -2127064;

	/**
	 * equivalent to #976825
	 */
	public static final int STROKE_COLOR = -6854619;

	/**
	 * zoom value for the view of the map (min 2.0, max 21.0)
	 */
	public static final float zoomOnStart = 20f;

	public static final int ZOOM = 15;

	public static final LatLng defaultCenter = new LatLng(
			SystemProperties.BREMEN_LAT, SystemProperties.BREMEN_LON);

	OnProjectionClickListener mCallback;

	/*
	 * Container Activity must implement this interface
	 */
	public interface OnProjectionClickListener {
		public void onProjectionClicked();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnProjectionClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnProjectionClickListener");
		}
	}

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_projection:
                mCallback.onProjectionClicked();
                break;
        }
        return true;
    }
}
