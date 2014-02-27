package de.dala.projection_algorithm;

import java.util.List;

import de.dala.common.GPSPoint;
import de.dala.common.ScreenPoint;
import de.dala.common.TransformationParameter;

/**
 * This interface provides methods, which are used by different algorithm to
 * project world coordinates to screen coordinates
 * 
 * @author Daniel Langerenken
 * 
 */
public interface IWorldToScreenProjector {
	/**
	 * Converts gpsPoints of near objects to equivalent screen points (might be
	 * negative - if not visible)
	 * 
	 * @param parameter
	 * @param gpsPoints
	 * @return
	 */
	List<ScreenPoint> transformWorldToScreen(TransformationParameter parameter,
			List<GPSPoint> gpsPoints);

	String getProjectionName();

}
