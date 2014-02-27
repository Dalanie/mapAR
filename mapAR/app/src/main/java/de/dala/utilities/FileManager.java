package de.dala.utilities;

import java.io.File;

import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileManager {
	/**
	 * Folder of the whole application
	 */
	public static final String application_folder = "/mapit_reverse/";

	/**
	 * Select memory card as target if no memory card found, select
	 * dataDirectory of cell phone
	 * 
	 * @return Directory or null, if no directory found
	 */
	private static File getStorageLocation() {

		File storage = Environment.getExternalStorageDirectory();
		if (!storage.exists()) {
			storage = Environment.getDataDirectory();
		}

		if (!storage.exists()) {
			return null;
		}
		return storage;
	}

	/**
	 * Gets the folder of this application
	 * 
	 * @return folder location
	 */
	public static File getApplicationFolder() {
		File storageLocation = getStorageLocation();
		if (storageLocation == null) {
			return null;
		}

		File folder = new File(storageLocation.getAbsolutePath()
				+ application_folder);
		if (!folder.exists() && !folder.mkdir()) {
			return null;
		}
		return folder;
	}


	public static BitmapFactory.Options getOptions(String filePath,
			int requiredWidth, int requiredHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		// Avoid actual bitmap load
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = getScale(options.outWidth, options.outHeight,
				requiredWidth, requiredHeight);
		// Restore the inJustDecodeBounds values
		options.inJustDecodeBounds = false;
		// Other options which improve the image load
		options.inPurgeable = true;
		try {

			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(
					options, true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return options;
	}

	public static int getScale(int originalWidth, int originalHeight,
			final int requiredWidth, final int requiredHeight) {
		int scale = 1;
		if ((originalWidth > requiredWidth)
				|| (originalHeight > requiredHeight)) {

			if (originalWidth < originalHeight) {
				scale = Math.round((float) originalWidth / requiredWidth);
			} else {
				scale = Math.round((float) originalHeight / requiredHeight);
			}
		}
		return scale;
	}
}
