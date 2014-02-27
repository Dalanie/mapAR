package de.dala.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import de.dala.common.EvaluationPicture;
import de.dala.common.GPSPoint;
import de.dala.common.MapItObject;
import de.dala.common.MapItObjectExtension;

/**
 * Mock database which is used for testing the application without having a
 * running database
 * 
 * @author Daniel
 * 
 */
public class MockDatabaseHandler implements IDatabaseHandler {

	public MockDatabaseHandler(Context context) {
	}

	@Override
	public long addMapItObject(MapItObject mapItObject) {
		return 0;
	}

	@Override
	public boolean removeMapItObject(MapItObject mapItObject) {
		return false;
	}

	@Override
	public boolean removeMapItObjectById(long id) {
		return false;
	}

	@Override
	public boolean removeAllMapItObjects() {
		return false;
	}

	@Override
	public MapItObject getMapItObjectById(long id) {
		return null;
	}

	@Override
	public List<MapItObjectExtension> getAllMapItObjects() {
		ArrayList<GPSPoint> points = new ArrayList<GPSPoint>();
		points.add(new GPSPoint(53.08844048581575, 8.855672520825696, 0));
		points.add(new GPSPoint(53.088441904021366, 8.855656245438349, 0));
		points.add(new GPSPoint(53.08842629505921, 8.855655169629722, 0));
		points.add(new GPSPoint(53.08842567148306, 8.855670303443121, 0));
		MapItObjectExtension extension = new MapItObjectExtension(points,
				"Mock-Polygon");
		List<MapItObjectExtension> objects = new ArrayList<MapItObjectExtension>();
		objects.add(extension);
		return objects;
	}

	@Override
	public long addPicture(EvaluationPicture picture) {
		return 0;
	}

	@Override
	public List<EvaluationPicture> getAllPictures() {
		return new ArrayList<EvaluationPicture>();
	}
}
