package de.dala.network;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
 
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
 
public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleton(Context context) {

        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(this.mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
                            10);

                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }

                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }
                });
    }

    public static void init(Context context) {
        mInstance = new VolleySingleton(context);
    }

    public static VolleySingleton getInstance() {
        return mInstance;
    }

    public static RequestQueue getRequestQueue() {
        return mInstance.mRequestQueue;
    }

    public static ImageLoader getImageLoader() {
        return mInstance.mImageLoader;
    }
}