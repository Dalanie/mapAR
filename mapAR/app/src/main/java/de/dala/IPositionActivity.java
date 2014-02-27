package de.dala;

import android.content.Context;
import android.location.Location;

/**
 * This interface must be used from activities, which deal with the
 * position-manager
 * 
 * @author Daniel Langerenken
 * 
 */
public interface IPositionActivity {
	Context getContext();

	void gpsStatusChanged(Location newLocation);
}
