package com.senkatel.bereznikov.bulletinboard.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.widget.ArrayAdapter;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBArrayAdapter;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class Images {
	private static LruCache<Integer, Bitmap> mMemoryCache;

	public static void init() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}

		};
	}

	public static void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(Integer key) {
		return mMemoryCache.get(key);
	}



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

	public static Bitmap loadImage(int id, int width, int height) {
		Log.v(Constants.LOG_TAG, "ID: " + id);
		Bitmap resultBitmap = getBitmapFromMemCache(id);
		Bitmap scaledBitmap = null;
		if (resultBitmap == null) {
			String url = Constants.URL + Constants.BULLETIN + "/" + id + "/image";
			InputStream imageStream = getImageStreamFromUrl(url);
			resultBitmap = BitmapFactory.decodeStream(imageStream);

			if (resultBitmap != null) {
				scaledBitmap = Bitmap.createScaledBitmap(resultBitmap,width,height,false);
				addBitmapToMemoryCache(id, scaledBitmap);

			}

		}else {
			scaledBitmap = Bitmap.createScaledBitmap(resultBitmap,width,height,false);
		}

		//resultBitmap = getResizedBitmap(resultBitmap, heigt,width);
		return scaledBitmap;

	}
	public static Bitmap loadImage(int id, BBArrayAdapter adapter) {

		Log.v(Constants.LOG_TAG, "ID: " + id);
		Bitmap resultBitmap = getBitmapFromMemCache(id);
		if (resultBitmap == null) {
			String url = Constants.URL + Constants.BULLETIN + "/" + id + "/image";
			InputStream imageStream = getImageStreamFromUrl(url);
			resultBitmap = BitmapFactory.decodeStream(imageStream);

			if (resultBitmap != null) {
				addBitmapToMemoryCache(id, resultBitmap);
			}
		}
		adapter.notifyDataSetChanged();
		return resultBitmap;

	}


	public static Bitmap loadImage(Uri path, int id, Activity activity) {
		Bitmap resultBitmap = null;
		try {
			resultBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addBitmapToMemoryCache(id, resultBitmap);
		return resultBitmap;

	}

	public static boolean uploadImage(Bitmap bitmap, int id) {
		String url = Constants.URL + Constants.BULLETIN + "/" + id + "/image";
		addBitmapToMemoryCache(id, bitmap);
		postImageToUrl(url, bitmap);
		return true;

	}

	public void syncCache(List<Integer> ids) {
		for (Integer id : ids){
			loadImage(id);
		}


	}

	public static boolean postImageToUrl(String url, Bitmap image) {
		boolean result = true;
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
			// handle exception here
			Log.e(e.getClass().getName(), e.getMessage());
			result = false;
			return result;
		}
		return result;


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


	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

// create a matrix for the manipulation

		Matrix matrix = new Matrix();

// resize the bit map

		matrix.postScale(scaleWidth, scaleHeight);

// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

		return resizedBitmap;
	}



}
