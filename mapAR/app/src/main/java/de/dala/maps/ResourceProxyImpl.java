package de.dala.maps;

import org.osmdroid.DefaultResourceProxyImpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import de.dala.R;

/**
 * https://code.google.com/p/osmdroid/source/browse/trunk/OpenStreetMapViewer/
 * src/org/andnav/osm/ResourceProxyImpl.java?r=641
 * 
 * @author Daniel
 * 
 */
public class ResourceProxyImpl extends DefaultResourceProxyImpl {
	private final Context mContext;

	public ResourceProxyImpl(final Context pContext) {
		super(pContext);
		mContext = pContext;
	}

	@Override
	public String getString(string pResId) {
		try {
			final int res = R.string.class.getDeclaredField(pResId.name())
					.getInt(null);
			return mContext.getString(res);
		} catch (final Exception e) {
			return super.getString(pResId);
		}
	}

	@Override
	public Bitmap getBitmap(bitmap pResId) {
		try {
			final int res = R.drawable.class.getDeclaredField(pResId.name())
					.getInt(null);
			return BitmapFactory.decodeResource(mContext.getResources(), res);
		} catch (final Exception e) {
			return super.getBitmap(pResId);
		}
	}

	@Override
	public Drawable getDrawable(bitmap pResId) {
		try {
			final int res = R.drawable.class.getDeclaredField(pResId.name())
					.getInt(null);
			return mContext.getResources().getDrawable(res);
		} catch (final Exception e) {
			return super.getDrawable(pResId);
		}
	}
}