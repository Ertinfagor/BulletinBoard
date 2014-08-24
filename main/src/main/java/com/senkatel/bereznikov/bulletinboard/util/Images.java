package com.senkatel.bereznikov.bulletinboard.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Static Class Images
 * implements work with images
 * Implements Memory cache
 */
public class Images {
	private static LruCache<Integer, Bitmap> mMemoryCache;

	/**
	 * Init Memory Cache
	 */
	public static void init() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / Constants.MEMORY_CACHE_USAGE_DIVISOR;

		mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}

		};
	}

	/**
	 * Load Bitmap to Cache
	 *
	 * @param key    Bulletin id
	 * @param bitmap Bitmap
	 */
	public static void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * Get bitmap from cache
	 *
	 * @param key Bulletin id
	 * @return bitmap
	 */
	public static Bitmap getBitmapFromMemCache(Integer key) {
		return mMemoryCache.get(key);
	}


	/**
	 * Try to load bitmap from cache if no load from server
	 *
	 * @param id Bulletin id
	 * @return bitmap
	 */
	public static Bitmap loadImage(int id) {
		Bitmap resultBitmap = getBitmapFromMemCache(id);
		if (resultBitmap == null) {
			String url = Constants.URL + Constants.BULLETIN + "/" + id + "/image";
			InputStream imageStream = getImageStreamFromUrl(url);
			resultBitmap = BitmapFactory.decodeStream(imageStream);

			if (resultBitmap != null) {
				addBitmapToMemoryCache(id, resultBitmap);
			}
		}
		return resultBitmap;

	}

	/**
	 * Try to load bitmap from cache if no load from server
	 *
	 * @param id     Bulletin id
	 * @param width  scale width
	 * @param height scale height
	 * @return Scaled bitmap
	 */
	public static Bitmap loadImage(int id, int width, int height) {
		Bitmap scaledBitmap = null;
		try {
			Bitmap resultBitmap = getBitmapFromMemCache(id);

			if (resultBitmap == null) {
				String url = Constants.URL + Constants.BULLETIN + "/" + id + Constants.IMAGE;
				InputStream imageStream = getImageStreamFromUrl(url);
				resultBitmap = BitmapFactory.decodeStream(imageStream);

				if (resultBitmap != null) {
					scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, width, height, false);
					addBitmapToMemoryCache(id, scaledBitmap);

				}

			} else {
				scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, width, height, false);
			}
		}catch (Exception e){

			Log.e(Constants.LOG_TAG, "loadImage load image error" + e.toString());
		}
			return scaledBitmap;

	}

	/**
	 * Load all bitmaps to cache
	 * @param ids List of bulletins ids
	 */
	public void syncCache(List<Integer> ids) {
		for (Integer id : ids) {
			loadImage(id, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIHT);
		}


	}

	public static InputStream getImageStreamFromUrl(String url) {

		InputStream rawContent = null;


		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			rawContent = entity.getContent();

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getBitmap Network error" + e.toString());

		}
		return rawContent;

	}


	/**
	 * Load Image on server
	 * Using externale apache libraries
	 * for compile on Intelij IDEA change in build.gradle:
	 * packagingOptions{
	 * <p/>
	 * exclude 'META-INF/DEPENDENCIES'
	 * exclude 'META-INF/NOTICE'
	 * exclude 'META-INF/LICENSE'
	 * exclude 'META-INF/NOTICE.txt'
	 * exclude 'META-INF/LICENSE.txt'
	 * }
	 *
	 * @param url   URL for upload
	 * @param image Bitmap for Upload
	 * @throws Exception
	 */
	public static void postImage(String url, Bitmap image) throws Exception {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 75, bos);
			byte[] data = bos.toByteArray();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("file", bab);
			postRequest.setEntity(reqEntity);
			httpClient.execute(postRequest);

		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}


	}
}
