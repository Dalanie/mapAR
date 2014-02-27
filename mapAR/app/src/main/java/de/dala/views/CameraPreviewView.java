package de.dala.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import de.dala.utilities.SystemProperties;

/**
 * This class shows the preview pictures of the camera
 * 
 * @author Daniel Langerenken
 * 
 */
public class CameraPreviewView extends ViewGroup implements
		SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private Size pictureSize;
	private Size previewSize;
	private List<Size> supportedPreviewSizes;
	private List<Size> supportedPictureSizes;
	private List<String> supportedFlashModes;
	private List<String> autoFocus;
	private SurfaceView surfaceView;

	private int containerWidth = 0;
	private int containerHeight = 0;

	public CameraPreviewView(Context context) {
		super(context);
		surfaceView = new SurfaceView(context);
		addView(surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		initSizes();
	}

	private void initSizes() {
		try {
			camera = Camera.open();
			if (camera != null) {
				/*
				 * get a group of supported preview size
				 */
				supportedPreviewSizes = camera.getParameters()
						.getSupportedPreviewSizes();

				supportedPictureSizes = camera.getParameters()
						.getSupportedPictureSizes();

				supportedFlashModes = camera.getParameters()
						.getSupportedFlashModes();
				autoFocus = camera.getParameters().getSupportedFocusModes();
				requestLayout();
			}
		} finally {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = resolveSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		setMeasuredDimension(width, height);

		if (containerWidth < width || containerHeight < height) {
			containerWidth = width;
			containerHeight = height;
		}

		if (supportedPreviewSizes != null) {
			previewSize = getOptimalPreviewSize(supportedPreviewSizes,
					containerWidth, containerHeight);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (previewSize != null) {
				previewWidth = previewSize.width;
				previewHeight = previewSize.height;
			}

			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height
						/ previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2, width,
						(height + scaledChildHeight) / 2);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Parameters params = camera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, width, height);
		params.setPreviewSize(optimalSize.width, optimalSize.height);
		params.setPictureFormat(ImageFormat.JPEG);
		camera.setParameters(params);
		camera.startPreview();

		mapItSurfaceChanged();
	}

	private void mapItSurfaceChanged() {
		SharedPreferences settings = getContext().getSharedPreferences(
				SystemProperties.SYSTEM_PREFERENCE_FILE, Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = settings.edit();

		if (previewSize != null) {
			editor.putInt(SystemProperties.CAMERA_PREVIEW_WIDTH,
					previewSize.width);
			editor.putInt(SystemProperties.CAMERA_PREVIEW_HEIGHT,
					previewSize.height);
		}

		if (pictureSize != null) {

			editor.putInt(SystemProperties.PHOTO_WIDTH, pictureSize.width);
			editor.putInt(SystemProperties.PHOTO_HEIGHT, pictureSize.height);

			Double tempD = (pictureSize.width / 2)
					/ (Math.atan(Math.toRadians(settings.getFloat(
							SystemProperties.CAMERA_HORIZONTAIL_VIEW_ANGLE,
							0.0f) / 2)));

			editor.putFloat(SystemProperties.CAMERA_DISTANCE_IN_PIXEL,
					tempD.floatValue());
		}

		editor.commit();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);

			/*
			 * Assign the camera trigger listener here, instead of being in
			 * onCreated method. we leave the camera initialize here
			 */

			/*
			 * retrieve the parameters of cameras
			 */
			Camera.Parameters params = camera.getParameters();

			/*
			 * set the preview size for camera view
			 */
			float verticalViewAngle = params.getVerticalViewAngle();
			float horizontalViewAngle = params.getHorizontalViewAngle();

			pictureSize = getOptimalPictureSize(supportedPictureSizes,
					horizontalViewAngle / verticalViewAngle);
			pictureSize = previewSize;
			if (previewSize != null) {
				params.setPreviewSize(previewSize.width, previewSize.height);
			}
			/*
			 * set the picture size for taking photo
			 */
			if (pictureSize != null) {
				params.setPictureSize(pictureSize.width, pictureSize.height);
			}
			/*
			 * set the picture type for taking photo
			 */
			params.setPictureFormat(ImageFormat.JPEG);

			/*
			 * if auto focus support, use auto focus
			 */
			if (autoFocus != null
					&& autoFocus.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}

			/*
			 * if auto flash support, use flash
			 */
			if (supportedFlashModes != null
					&& supportedFlashModes
							.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
				params.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
			}
		} catch (IOException e) {
			camera.release();
			Log.e("surfaceCreated",
					String.format("ProjectionCamera.open() result: %s",
							e.getMessage()));
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		/*
		 * Necessary to release the camera, so other applications can use it
		 */
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	/*
	 * @Function: get optimal picture size according to camera view angles
	 */
	private Size getOptimalPictureSize(List<Size> sizes, double targetRatio) {
		final double ASPECT_TOLERANCE = 0.1;
		if (sizes == null) {
			return null;
		}
		Size optimalSize = null;

		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			} else {
				optimalSize = size;
				return optimalSize;
			}
		}
		return optimalSize;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		// calculate the ratio of preview display
		double targetRatio = (double) w / h;
		// if no supported preview sizes, return null
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		// Set target Height based on the
		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;

			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;

			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {

			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void takePicture(Handler handler) {
		PictureCallBack mPictureCallback = new PictureCallBack(handler);
		camera.setPreviewCallback(mPictureCallback);
	}

	private class PictureCallBack implements Camera.PreviewCallback {
		private Handler handler;

		public PictureCallBack(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (data != null) {
				Camera.Parameters parameters = camera.getParameters();
				Size size = parameters.getPreviewSize();
				YuvImage image = new YuvImage(data,
						parameters.getPreviewFormat(), size.width, size.height,
						null);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, size.width, size.height),
						100, out);
				byte[] imageBytes = out.toByteArray();
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0,
						imageBytes.length);
				Message message = new Message();
				message.obj = bitmap;
				handler.sendMessage(message);
				camera.setPreviewCallback(null);
			}
		}

	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
