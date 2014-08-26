package com.senkatel.bereznikov.bulletinboard.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;
import org.apache.commons.io.IOUtils;
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

/**
 * Static Class Images
 * implements work with images
 * Implements Memory cache
 */
@SuppressWarnings("ALL")
public class Images {
	private static LruCache<Integer, Bitmap> mMemoryCache;
	private static byte[] tempByteArrayBitmap;
	static int maxMemory;

	/**
	 * Init Memory Cache
	 * calculates cache size
	 */
	public static void init() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

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
	 * @param id     Bulletin id
	 * @param width  scale width
	 * @param height scale height
	 * @return Scaled bitmap
	 */
	public static Bitmap loadImage(int id, int width, int height) {
		Bitmap resultBitmap = null;
		try {
			resultBitmap = getBitmapFromMemCache(id);
			if (resultBitmap == null) {
				String url = Constants.URL + Constants.BULLETIN + "/" + id + Constants.IMAGE;
				resultBitmap = decodeBitmapFromByteArray(getByteArrayFromUrl(url), width, height);
				if (resultBitmap != null) {
					addBitmapToMemoryCache(id, resultBitmap);
				}

			}
		} catch (Exception e) {

			Log.e(Constants.LOG_TAG, "loadImage load image error" + e.toString());
		}
		return resultBitmap;

	}

	/**
	 * Load InputStream with image from server
	 *
	 * @param url server url
	 * @return InputStream with image
	 */
	public static byte[] getByteArrayFromUrl(String url) {

		InputStream rawContent;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			rawContent = entity.getContent();
			byte[] bytes = IOUtils.toByteArray(rawContent);
			return bytes;
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getBitmap Network error" + e.toString());

		}

		return null;

	}


	/**
	 * Load Image on server
	 * Using external apache libraries
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
	 * @param url             URL for upload
	 * @param byteArrayBitmap Bitmap byte array for Upload
	 * @throws Exception
	 */
	public static void postImage(String url, byte[] byteArrayBitmap) throws Exception {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			ByteArrayBody bab = new ByteArrayBody(byteArrayBitmap, "test.jpg");
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("file", bab);
			postRequest.setEntity(reqEntity);
			httpClient.execute(postRequest);

		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}


	}

	/**
	 * Calculates level of cropping image
	 *
	 * @param options   loaded Bitmap options for cropped Bitmap
	 * @param reqWidth  required width
	 * @param reqHeight required height
	 * @return cropping ratio
	 */
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/**
	 * Get bitmap correct size
	 * Use apache common-io IOUtils
	 * changes to build.grandle:
	 * exclude 'org/apache/commons/io/FileUtilsTestDataCR.dat'
	 * exclude 'org/apache/commons/io/FileUtilsTestDataCRLF.dat'
	 * exclude 'org/apache/commons/io/FileUtilsTestDataLF.dat'
	 * exclude 'org/apache/commons/io/testfileBOM.xml'
	 * exclude 'org/apache/commons/io/testfileNoBOM.xml'
	 * exclude 'test-file-20byteslength.bin'
	 * exclude 'test-file-empty.bin'
	 * exclude 'test-file-iso8859-1-shortlines-win-linebr.bin'
	 * exclude 'test-file-iso8859-1.bin'
	 * exclude 'test-file-shiftjis.bin'
	 * exclude 'test-file-utf16be.bin'
	 *
	 * @param imageByteArray Bitmap ByteArray for scaling
	 * @param reqWidth       required width
	 * @param reqHeight      required height
	 * @return scaled Bitmap
	 */
	public static Bitmap decodeBitmapFromByteArray(byte[] imageByteArray, int reqWidth, int reqHeight) {

		try {


			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;

			return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, options);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Can`t decode Bitmap" + e.toString());
		}
		return null;
	}

	/**
	 * Convert bitmap loaded from file to appropriate size IMAGE_WIDTH_MAX  IMAGE_HEIHT_MAX
	 *
	 * @param uriFile   URI of loaded file
	 * @param reqWidth  IMAGE_WIDTH_MAX
	 * @param reqHeight IMAGE_HEIHT_MAX
	 * @param context   context for load file
	 * @return scaled Bitmap
	 */
	public static Bitmap decodeBitmapFromFile(Uri uriFile, int reqWidth, int reqHeight, Context context) {

		try {

			InputStream input = context.getContentResolver().openInputStream(uriFile);

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			InputStream inputSecond = context.getContentResolver().openInputStream(uriFile);
			return BitmapFactory.decodeStream(inputSecond, null, options);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Can`t decode Bitmap" + e.toString());
		}
		return null;
	}

	/**
	 * Temporary save loaded from file Bitmap for new Bulletin until text part loaded and ID for loading returned
	 *
	 * @param imagePath URI of loaded image
	 * @param context   context for get image
	 */
	public static void setTempByteArrayBitmap(final Uri imagePath, final Context context) {

		try {
			Bitmap tempBitmap = decodeBitmapFromFile(imagePath, Constants.IMAGE_WIDTH_MAX, Constants.IMAGE_HEIHT_MAX, context);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			tempBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
			byte[] byteArray = stream.toByteArray();
			tempByteArrayBitmap = byteArray;


		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Can`t get image from file: " + e.toString());
		}


	}

	/**
	 * Returns temporary saved bitmap when postBulletins has ID of new bulletin
	 *
	 * @return temporary saved Bitmap
	 */
	public static Bitmap getTempBitmap() {
		try {
			return decodeBitmapFromByteArray(tempByteArrayBitmap, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIHT);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Cant decode temp image: " + e.toString());
		}
		return null;
	}

	/**
	 * when postBulletins has ID of new bulletin runs to upload image to server
	 *
	 * @param id ID of new bulletin
	 */
	public static void uploadTemp(int id) {
		String url = Constants.URL + Constants.BULLETIN + "/" + id + Constants.IMAGE;
		try {
			postImage(url, tempByteArrayBitmap);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Can`t POST image: " + e.toString());
		}


	}
}
