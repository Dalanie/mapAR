package de.dala;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import de.dala.common.EvaluationPicture;
import de.dala.common.GPSPoint;
import de.dala.common.MapItObject;
import de.dala.common.MapItObjectExtension;
import de.dala.common.OrientationPoint;
import de.dala.common.ScreenPoint;
import de.dala.common.TransformationParameter;
import de.dala.database.DatabaseHandler;
import de.dala.projection_algorithm.IWorldToScreenProjector;
import de.dala.projection_algorithm.MapItGridWorldToScreenProjector;
import de.dala.projection_algorithm.PerspectiveScreenProjector;
import de.dala.smoothing.location.BestAccuracySmoothingMethod;
import de.dala.smoothing.location.ISmoothingLocationDataMethod;
import de.dala.smoothing.location.IndoorLocationMethod;
import de.dala.smoothing.location.LastPositionSmoothingMethod;
import de.dala.smoothing.location.MapLocationMethod;
import de.dala.smoothing.sensor.AverageSensorSmoothingMethod;
import de.dala.smoothing.sensor.ISmoothingSensorDataMethod;
import de.dala.smoothing.sensor.LowPassAverageSensorSmoothingMethod;
import de.dala.smoothing.sensor.LowPassSensorSmoothingMethod;
import de.dala.smoothing.sensor.LowPassWeightedAverageMethod;
import de.dala.smoothing.sensor.WeightedMovingAverageMethod;
import de.dala.utilities.FileManager;
import de.dala.utilities.PrefUtilities;
import de.dala.utilities.SystemProperties;
import de.dala.utilities.TransformCoordinatesUtilities;
import de.dala.views.AccuracyView;
import de.dala.views.CameraPreviewView;
import de.dala.views.DebugView;
import de.dala.views.DirectionView;
import de.dala.views.ObjectOverlay;
import de.dala.views.RadarView;

public class ProjectionViewFragment extends SherlockFragment implements
		ISensorActivity, IPositionActivity, OnTouchListener {
	private static final String CURRENT_LOCATION_METHOD_INDEX = "currentLocationMethodIndex";
	private static final String CURRENT_SENSOR_METHOD_INDEX = "currentSensorMethodIndex";
	private static final String CURRENT_PROJECTION_METHOD_INDEX = "currentProjectionMethodIndex";
	private MainActivity parentActivity;
	private OnMapClickListener mCallback;
	private CameraPreviewView previewView;
	private long timeForPicture = 0;

	private int canvasWidth;
	private int canvasHeight;

	private LayerManager layerManager;
	private ObjectOverlay objectOverlay;

	private DebugView debugView;
	private RadarView radarView;
	private DirectionView directionView;
	private AccuracyView accuracyView;

	private MapItObject mapItObject = new MapItObject(
			new ArrayList<GPSPoint>(), null);
	private int countOfPresses = 0;

	private Thread currentDrawThread;

	private boolean showRadarView = false;
	private boolean showDirectionView = false;
	private boolean showAccuracyView = false;
	private boolean projecting = false;
	private boolean debugMode = false;
	private boolean objectsCreatable = false;

	private GPSPoint lastLocation;

	/**
	 * Index of mapItObject in list
	 */
	private int nearestMapItObjectIndex = 0;

	private List<IWorldToScreenProjector> projectionMethods;
	private List<ISmoothingSensorDataMethod> smoothingSensorMethods;
	private List<ISmoothingLocationDataMethod> smoothingLocationMethods;

	private IWorldToScreenProjector currentProjectingMethod;
	private ISmoothingSensorDataMethod currentSmoothingSensorMethod;
	private ISmoothingLocationDataMethod currentSmoothingLocationMethod;

	private boolean showBottomBar = false;

	private TransformationParameter currentTransformationParameter;

	private View bottomBar;
	/*
	 * get camera properties
	 */
	private float cameraVerticalAngle = 0.0f;
	private float cameraHorizontalAngle = 0.0f;
	private float cameraDistanceInPixel = 0.0f;

	public ProjectionViewFragment() {
	}

	/**
	 * Container Activity must implement this interface
	 */
	public interface OnMapClickListener {
		public void onMapClicked();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		/**
		 * This makes sure that the container activity has implemented the
		 * callback interface. If not, it throws an exception
		 */
		try {
			mCallback = (OnMapClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMapClickListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Activity activity = getActivity();
		if (activity instanceof MainActivity) {
			parentActivity = (MainActivity) activity;
			initialize();
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
		if (!projecting) {
			projecting = true;
			updateProjection();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		projecting = false;
	}

	private void initialize() {
		canvasWidth = PrefUtilities.getInstance().getDisplayWidth();
        canvasHeight = PrefUtilities.getInstance().getDisplayHeight();
        cameraVerticalAngle = PrefUtilities.getInstance().getCameraVerticalViewAngle();
        cameraHorizontalAngle = PrefUtilities.getInstance().getCameraHorizontalViewAngle();
        cameraDistanceInPixel = PrefUtilities.getInstance().getCameraDistanceInPixel();

		debugView = new DebugView(parentActivity);
		objectOverlay = new ObjectOverlay(parentActivity);
		radarView = new RadarView(parentActivity);
		directionView = new DirectionView(parentActivity);
		accuracyView = new AccuracyView(parentActivity);

		addAndSetSmoothingSensorAlgorithm();
		addAndSetSmoothingLocationAlgorithm();
		addAndSetProjectors();

		initBottomBar();
	}

	private void initBottomBar() {
		bottomBar = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.fragment_projection_actionbar_bottom, null);

		setupProjectionSpinner(bottomBar
				.findViewById(R.id.action_projection_spinner));
		setupSmoothingSensorSpinner(bottomBar
				.findViewById(R.id.action_smoothing_sensor_spinner));
		setupSmoothingLocationSpinner(bottomBar
				.findViewById(R.id.action_smoothing_location_spinner));
	}

	private float lowPassFilterDifference = 0.15f;

	private void addAndSetSmoothingSensorAlgorithm() {
		smoothingSensorMethods = new ArrayList<ISmoothingSensorDataMethod>();
		smoothingSensorMethods.add(new AverageSensorSmoothingMethod());
		smoothingSensorMethods.add(new LowPassSensorSmoothingMethod(
				lowPassFilterDifference));
		smoothingSensorMethods.add(new LowPassAverageSensorSmoothingMethod(
				lowPassFilterDifference));
		smoothingSensorMethods.add(new LowPassWeightedAverageMethod(
				lowPassFilterDifference));
		smoothingSensorMethods.add(new WeightedMovingAverageMethod());
		currentSmoothingSensorMethod = smoothingSensorMethods.get(0);
	}

	private void addAndSetSmoothingLocationAlgorithm() {
		smoothingLocationMethods = new ArrayList<ISmoothingLocationDataMethod>();
		smoothingLocationMethods.add(new LastPositionSmoothingMethod());
		smoothingLocationMethods.add(new BestAccuracySmoothingMethod());
		smoothingLocationMethods.add(new IndoorLocationMethod());
		smoothingLocationMethods.add(new MapLocationMethod());
		currentSmoothingLocationMethod = smoothingLocationMethods.get(0);
	}

	private void addAndSetProjectors() {
		projectionMethods = new ArrayList<IWorldToScreenProjector>();
		projectionMethods.add(new PerspectiveScreenProjector());
		projectionMethods.add(new MapItGridWorldToScreenProjector(
				parentActivity));
		currentProjectingMethod = projectionMethods.get(0);
	}

	private View contentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layerManager = new LayerManager(this);
		contentView = layerManager.initializeViews();

		/*
		 * Initializing ProjectionCamera-Preview and adding to the basic layout
		 */
		previewView = new CameraPreviewView(parentActivity);
		previewView.setOnTouchListener(this);

		layerManager.addBasicLayout(previewView);
		layerManager.addBasicLayout(objectOverlay);

		/*
		 * restore last transformation settings
		 */
		if (savedInstanceState != null) {
			currentProjectingMethod = projectionMethods.get(savedInstanceState
					.getInt(CURRENT_PROJECTION_METHOD_INDEX, 0));
			currentSmoothingSensorMethod = smoothingSensorMethods
					.get(savedInstanceState.getInt(CURRENT_SENSOR_METHOD_INDEX,
							0));
			currentSmoothingLocationMethod = smoothingLocationMethods
					.get(savedInstanceState.getInt(
							CURRENT_LOCATION_METHOD_INDEX, 0));
		}
		return contentView;
	}

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        item.setChecked(!item.isChecked());
        switch (item.getItemId()) {
            case R.id.action_map:
                mapClicked();
                break;
            case R.id.action_compass:
                compassClicked();
                break;
            case R.id.action_accuracy:
                accuracyClicked();
                break;
            case R.id.action_direction:
                directionClicked();
                break;
            case R.id.action_settings:
                settingsClicked();
                break;
            case R.id.action_remove:
                removePolygonsClicked();
                break;
            case R.id.action_debug_overlay:
                debugClicked();
                break;
            case R.id.action_create_polygons:
                createPolygonsClicked();
                break;
            case R.id.action_camera:
                timeForPicture = (new Date()).getTime();
                previewView.takePicture(handler);
                break;
        }
        return false;
    }

	private PictureHandler handler = new PictureHandler(this);

	private static class PictureHandler extends Handler {
		private ProjectionViewFragment fragment;

		public PictureHandler(ProjectionViewFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof Bitmap) {
				Bitmap bm1 = (Bitmap) msg.obj;
				// http://stackoverflow.com/questions/15276346/how-to-take-screenshot-programmatically-and-save-it-on-gallery
				try {
					Bitmap bitmap;
					fragment.contentView.setDrawingCacheEnabled(true);
					bitmap = Bitmap.createBitmap(fragment.contentView
							.getDrawingCache());
					// View v =
					// fragment.parentActivity.getWindow().getDecorView();
					// v.setDrawingCacheEnabled(true);
					// bitmap = v.getDrawingCache();

					fragment.contentView.setDrawingCacheEnabled(false);

					Bitmap bmResult = overlay(bm1, bitmap);
					bitmap.recycle();
					bm1.recycle();

					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					bmResult.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
					File f = new File(FileManager.getApplicationFolder()
							+ File.separator + (new Date()).getTime() + ".jpg");

					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.flush();
					fo.close();
					bmResult.recycle();
					String imagePath = f.getAbsolutePath();

					ArrayList<GPSPoint> gpsPoints = new ArrayList<GPSPoint>();
					ArrayList<ScreenPoint> screenPointList = new ArrayList<ScreenPoint>();

					if (fragment.screenPointList != null) {
						screenPointList = new ArrayList<ScreenPoint>(
								fragment.screenPointList);
					}
					gpsPoints = new ArrayList<GPSPoint>(
							fragment.transformDisplayCoordsToGPSCoords(screenPointList));

					EvaluationPicture picture = new EvaluationPicture(
							imagePath, fragment.currentTransformationParameter,
							screenPointList, gpsPoints);
					long id = DatabaseHandler.getInstance().addPicture(
                            picture);
					if (id > -1) {
						Toast.makeText(
								fragment.parentActivity,
								"Saved with id: "
										+ id
										+ ". Duration: "
										+ ((new Date()).getTime() - fragment.timeForPicture)
										+ " ms", Toast.LENGTH_SHORT).show();
					}

				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	// http://stackoverflow.com/questions/11505093/how-to-capture-screenshot-of-surfaceview-with-background
	public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);

		canvas.drawBitmap(bmp2, 0, 0, null);
		Log.i("bmOverlay.......", "" + bmOverlay);
		return bmOverlay;
	}

	private void accuracyClicked() {
		showAccuracyView = !showAccuracyView;
		if (showAccuracyView) {
			layerManager.addBasicLayoutRight(accuracyView);
		} else {
			layerManager.removeBasicLayout(accuracyView);
		}
	}

	private void createPolygonsClicked() {
		objectsCreatable = !objectsCreatable;
		if (!objectsCreatable) {
			countOfPresses = 0;
			mapItObject = new MapItObject(new ArrayList<GPSPoint>(), null);
		}
	}

	private void debugClicked() {
		debugMode = !debugMode;
		if (debugMode) {
			layerManager.addBasicLayout(debugView);
		} else {
			layerManager.removeBasicLayout(debugView);
		}
	}

	private void mapClicked() {
		/*
		 * Send the event to the host activity
		 */
		mCallback.onMapClicked();
	}

	private void compassClicked() {
		showRadarView = !showRadarView;
		layerManager.removeBasicLayout(radarView);

		if (updateRadar()) {
			layerManager.addBasicLayout(radarView, getRadarParams());
		}
	}

	private boolean updateRadar() {
		if (radarView != null && showRadarView) {
			refreshDistancesOfObjects();
			radarView.setResourcesList(parentActivity.getMapItObjects());
			return true;
		}
		return false;

	}

	private void directionClicked() {
		showDirectionView = !showDirectionView;
		layerManager.removeBasicLayout(directionView);
		if (showDirectionView) {
			refreshDistancesOfObjects();
			layerManager.addBasicLayoutCenter(directionView);
		}
	}

	private void removePolygonsClicked() {
		parentActivity.dropMapItObjects();
	}

	private void settingsClicked() {
		showBottomBar = !showBottomBar;
		if (showBottomBar && bottomBar != null) {
			layerManager.addBasicLayoutBottom(bottomBar);
		} else {
			if (bottomBar != null) {
				layerManager.removeBasicLayout(bottomBar);
			}
		}
	}

	private LayoutParams getRadarParams() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		int actionBarHeight = parentActivity.getActionBarHeight();
		params.setMargins(0, actionBarHeight + 20, 0, 0);
		return params;
	}

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_projection_actionbar, menu);
    }

	private void setupSmoothingSensorSpinner(View view) {
		if (view instanceof Spinner) {
			Spinner spinner = (Spinner) view;
			if (smoothingSensorMethods == null) {
				addAndSetSmoothingSensorAlgorithm();
			}
			/*
			 * Specify a SpinnerAdapter to populate the dropdown list.
			 */
			ArrayAdapter<ISmoothingSensorDataMethod> adapter = new ArrayAdapter<ISmoothingSensorDataMethod>(
					getContext(), android.R.layout.simple_spinner_item,
					android.R.id.text1, smoothingSensorMethods) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					TextView textView = (TextView) super.getView(position,
							convertView, parent);
					textView.setTextColor(getResources().getColor(
							R.color.textViewColor));
					return textView;
				}
			};
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					if (smoothingSensorMethods != null) {
						if (smoothingSensorMethods.size() > position) {
							currentSmoothingSensorMethod = smoothingSensorMethods
									.get(position);
							parentActivity.toast("Sensor-Smoothing changed");
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
		}
	}

	private void setupSmoothingLocationSpinner(View view) {
		if (view instanceof Spinner) {
			Spinner spinner = (Spinner) view;
			if (smoothingLocationMethods == null) {
				addAndSetSmoothingLocationAlgorithm();
			}
			/*
			 * Specify a SpinnerAdapter to populate the dropdown list.
			 */
			ArrayAdapter<ISmoothingLocationDataMethod> adapter = new ArrayAdapter<ISmoothingLocationDataMethod>(
					getContext(), android.R.layout.simple_spinner_item,
					android.R.id.text1, smoothingLocationMethods) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					TextView textView = (TextView) super.getView(position,
							convertView, parent);
					textView.setTextColor(getResources().getColor(
							R.color.textViewColor));
					return textView;
				}
			};
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					if (smoothingLocationMethods != null) {
						if (smoothingLocationMethods.size() > position) {
							currentSmoothingLocationMethod = smoothingLocationMethods
									.get(position);
							parentActivity.toast("Location-Smoothing changed");
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
		}
	}

	private void setupProjectionSpinner(View view) {
		if (view instanceof Spinner) {
			Spinner spinner = (Spinner) view;
			if (projectionMethods == null) {
				addAndSetProjectors();
			}
			/*
			 * Specify a SpinnerAdapter to populate the dropdown list.
			 */
			ArrayAdapter<IWorldToScreenProjector> adapter = new ArrayAdapter<IWorldToScreenProjector>(
					getContext(), android.R.layout.simple_spinner_item,
					android.R.id.text1, projectionMethods) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					TextView textView = (TextView) super.getView(position,
							convertView, parent);
					textView.setTextColor(getResources().getColor(
							R.color.textViewColor));
					return textView;
				}
			};
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					if (projectionMethods != null) {
						if (projectionMethods.size() > position) {
							currentProjectingMethod = projectionMethods
									.get(position);
							parentActivity.toast("Projector changed");
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
		}
	}

	@Override
	public void sensorStatusChanged(double bearing, double pitch,
			double rotation, int accuracy) {
		if (debugMode && debugView != null) {
			debugView.refreshSensorData(bearing, pitch, rotation,
					getMarginBottom());
			debugView.refreshOwnPosition(lastLocation);
			debugView.refreshGPSData(getScreenVerticesGPSPoints(),
					getMarginBottom());
		}
		/*
		 * only update radar, if it's visible
		 */
		if (showRadarView && radarView != null) {
			radarView.setBearing((float) bearing);
		}

		/*
		 * only update arrow, if it's visible
		 */
		if (showDirectionView && directionView != null) {
			if (directionView != null) {
				refreshArrowData(bearing);
			}
		}
	}

	private void refreshArrowData(double currentBearing) {
		GPSPoint location = lastLocation;
		if (currentTransformationParameter != null) {
			location = currentTransformationParameter.observerPosition;
		}
		if (parentActivity != null && location != null) {
			List<MapItObjectExtension> mapItObjects = parentActivity
					.getMapItObjects();

			if (mapItObjects != null
					&& mapItObjects.size() > nearestMapItObjectIndex) {
				MapItObjectExtension currentObject = parentActivity
						.getMapItObjects().get(nearestMapItObjectIndex);
				double bearingToPoint = location.bearingTo(currentObject
						.getNearestPoint(location));
				double distanceToPoint = currentObject.getCurrentDistance();

				directionView.refreshArrowData(
						(float) (currentBearing - bearingToPoint),
						currentObject.description, distanceToPoint);
			}
		}
	}

	public void updateTransformationParameter() {
		/*
		 * get the observer height
		 */
		int height = PrefUtilities.getInstance().getObserverHeight();
		/*
		 * get the current orientation readings
		 */
		if (parentActivity != null
				&& parentActivity.getPositionManager() != null) {
			LinkedList<OrientationPoint> orientationPointList = parentActivity
					.getPositionManager().getOrientationList();
			LinkedList<Location> locationPointList = parentActivity
					.getPositionManager().getLocationList();

			if (orientationPointList != null
					&& currentSmoothingLocationMethod != null) {
				OrientationPoint orientationPoint = currentSmoothingSensorMethod
						.getSmoothingOrientationData(orientationPointList);

				GPSPoint observerPosition = null;
				if (currentSmoothingLocationMethod != null) {
					observerPosition = currentSmoothingLocationMethod
							.getSmoothingLocationData(locationPointList);
				} else {
					/*
					 * if no gps available, use indoor-position
					 */
					if (lastLocation == null) {
						observerPosition = SystemProperties.INDOOR_POSITION;
					} else {
						/*
						 * get observer position object
						 */
						observerPosition = new GPSPoint(lastLocation.latitude,
								lastLocation.longitude, height);
					}
				}
				float focalLength = PrefUtilities.getInstance().getCameraFocalLength();

				TransformationParameter parameter = null;
				if (orientationPoint != null) {
					parameter = new TransformationParameter(height,
							orientationPoint.bearing, orientationPoint.pitch,
							orientationPoint.rotation, cameraVerticalAngle,
							cameraHorizontalAngle, cameraDistanceInPixel,
							canvasWidth, canvasHeight, observerPosition,
							focalLength);
				}
				currentTransformationParameter = parameter;
			}
		}
	}

	public List<GPSPoint> transformDisplayCoordsToGPSCoords(
			List<ScreenPoint> screenPoints) {
		return TransformCoordinatesUtilities.transformGeoCoords(
                currentTransformationParameter, screenPoints);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (objectsCreatable) {
			ScreenPoint pressedScreenPoint = new ScreenPoint(
					(int) event.getX(), (int) event.getY());
			List<ScreenPoint> screenPoints = new ArrayList<ScreenPoint>();
			screenPoints.add(pressedScreenPoint);
			List<GPSPoint> gpsPoints = transformDisplayCoordsToGPSCoords(screenPoints);
			if (gpsPoints.size() > 0) {
				GPSPoint gpsPosition = gpsPoints.get(0);
				// Log.d("Debug-Point", "Lat:" + gpsPosition.latitude + " Long:"
				// + gpsPosition.longitude);
				if (countOfPresses < 4) {
					countOfPresses++;
				} else {
					countOfPresses = 1;
					mapItObject.polygonPoints.clear();
				}
				parentActivity.toast("Point saved: " + countOfPresses);

				mapItObject.polygonPoints.add(gpsPosition);
				if (countOfPresses == 4) {
					savePolygon();
				}
			}
		}
		return false;
	}

	private void updateProjection() {
		if (currentDrawThread == null || !currentDrawThread.isAlive()) {
			currentDrawThread = new Thread() {
				public void run() {
					while (projecting) {
						updateTransformationParameter();
						List<MapItObjectExtension> objects = parentActivity
								.getMapItObjects();
						if (objects != null) {
							synchronized (objects) {
								for (int i = 0; i < objects.size(); i++) {
									calculateAndDrawObject(
											currentTransformationParameter,
											objects.get(i));
								}
								objectOverlay.setMapItObjects(objects);
								objectOverlay.mHandler.sendEmptyMessage(0);
								try {
									sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

					}
				};
			};
			currentDrawThread.start();
		}
	}

	private void calculateAndDrawObject(
			TransformationParameter transformationParameter,
			MapItObjectExtension mapItObjectExtension) {
		if (mapItObjectExtension.polygonPoints != null) {
			List<ScreenPoint> drawableScreenPoints = currentProjectingMethod
					.transformWorldToScreen(transformationParameter,
							mapItObjectExtension.polygonPoints);
			mapItObjectExtension.setScreenPoints(drawableScreenPoints);
			screenPointList = drawableScreenPoints;
		}
	}

	public void savePolygon() {
		if (mapItObject != null && mapItObject.polygonPoints.size() == 4) {

			Log.d("Save Polygon", "This are the values");
			for (int i = 0; i < 4; i++) {
				GPSPoint point = mapItObject.polygonPoints.get(i);
				Log.d("Point " + i, "Lat: " + point.latitude + " Lon: "
						+ point.longitude);
			}
			if (parentActivity != null) {
				if (DatabaseHandler.getInstance().addMapItObject(mapItObject) > 0) {
					parentActivity.toast("Object stored");
					parentActivity.reloadMapItObjects();
					refreshDistancesOfObjects();
				}
			}
		}
	}

	@Override
	public Context getContext() {
		return parentActivity;
	}

	private void refreshDistancesOfObjects() {
		if (parentActivity != null) {
			List<MapItObjectExtension> mapItObjects = parentActivity
					.getMapItObjects();
			GPSPoint location = lastLocation;
			/*
			 * Refreshs the radar
			 */
			if (currentTransformationParameter != null) {
				location = currentTransformationParameter.observerPosition;
			}
			if (location != null && mapItObjects != null) {
				for (int i = 0; i < mapItObjects.size(); i++) {
					MapItObjectExtension object = mapItObjects.get(i);
					object.setDrawnValues(location);
				}
				/*
				 * lowest distance first (reverse() for highest distances first)
				 */
				Collections.sort(mapItObjects);
			}
		}
	}

	@Override
	public void gpsStatusChanged(Location newLocation) {
		updateGPSPosition(new GPSPoint(newLocation));
	}

	private void updateGPSPosition(GPSPoint newLocation) {
		lastLocation = newLocation;
		updateRadar();
		if (debugMode && debugView != null) {
			debugView.refreshGPSData(getScreenVerticesGPSPoints(),
					getMarginBottom());
		}
		if (showAccuracyView && accuracyView != null) {
			accuracyView.onAccuracyChanged(newLocation.accuracy);
		}
	}

	private int getMarginBottom() {
		if (showBottomBar && bottomBar != null) {
			return bottomBar.getHeight();
		}
		return 0;
	}

	private float millisecondsToCacheScreenVertices = 5000;
	private float lastUpdate;
	private List<GPSPoint> screenVertices;
	List<ScreenPoint> screenPointList;

	public List<GPSPoint> getScreenVerticesGPSPoints() {
		float currentTime = new Date().getTime();
		if (screenVertices == null
				|| (currentTime > lastUpdate
						+ millisecondsToCacheScreenVertices)) {
			if (screenPointList == null || screenPointList.isEmpty()) {
				screenPointList = new ArrayList<ScreenPoint>();
				screenPointList.add(new ScreenPoint(0, 0));
				screenPointList.add(new ScreenPoint(0,
						currentTransformationParameter.canvasHeight));
				screenPointList.add(new ScreenPoint(
						currentTransformationParameter.canvasWidth, 0));
				screenPointList.add(new ScreenPoint(
						currentTransformationParameter.canvasWidth,
						currentTransformationParameter.canvasHeight));
			}

			screenVertices = TransformCoordinatesUtilities.transformGeoCoords(
					currentTransformationParameter, screenPointList);
			lastUpdate = currentTime;
		}
		return screenVertices;
	}

	public void keyCodeDown() {
		if (currentProjectingMethod instanceof MapItGridWorldToScreenProjector) {
			((MapItGridWorldToScreenProjector) currentProjectingMethod)
					.keyCodeDown();
		}
	}

	public void keyCodeUp() {
		if (currentProjectingMethod instanceof MapItGridWorldToScreenProjector) {
			((MapItGridWorldToScreenProjector) currentProjectingMethod)
					.keyCodeUp();
		}
	}

	public void mapItObjectsReloaded() {
		updateRadar();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/*
		 * Save transformation details
		 */
		outState.putInt(CURRENT_PROJECTION_METHOD_INDEX,
				projectionMethods.indexOf(currentProjectingMethod));
		outState.putInt(CURRENT_SENSOR_METHOD_INDEX,
				smoothingSensorMethods.indexOf(currentSmoothingSensorMethod));
		outState.putInt(CURRENT_LOCATION_METHOD_INDEX, smoothingLocationMethods
				.indexOf(currentSmoothingLocationMethod));
	}
}
