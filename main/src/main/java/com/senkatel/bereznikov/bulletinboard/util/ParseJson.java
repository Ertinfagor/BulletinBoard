package com.senkatel.bereznikov.bulletinboard.util;

import android.graphics.Bitmap;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;


/**
 * Static Class ParseJson
 * Implements functionality work with server
 */
public class ParseJson {


	/**
	 * GET JSON Array from http
	 *
	 * @param url URL string for load
	 * @return Loaded JSON Array
	 * @throws Exception
	 */
	public static JSONArray getJsonArray(String url) throws Exception {

		Log.v(Constants.LOG_TAG,"GET Array: " + url);
		InputStream isRawContent = null;
		String sJSONRawString = null;
		JSONArray jaResultJSONArray = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			isRawContent = entity.getContent();

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getJsonArray Network error" + e.toString());

		}
		if (isRawContent != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(isRawContent, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				isRawContent.close();
				sJSONRawString = sb.toString();
				jaResultJSONArray = new JSONArray(sJSONRawString);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "getJsonArray Error parsing string: " + e.toString());
			} catch (JSONException e1) {
				Log.e(Constants.LOG_TAG, "getJsonArray Error parsing JSON: " + e1.toString());
				throw new JSONException(sJSONRawString);
			} catch (Exception e2) {
				Log.e(Constants.LOG_TAG, "getJsonArray Error: " + e2.toString());
			}
		}
		return jaResultJSONArray;

	}
	/**
	 * GET JSON Array from http
	 *
	 * @param url URL string for load
	 * @return Loaded JSON Array
	 * @throws Exception
	 */
	public static JSONObject getJsonObject(String url) throws Exception {
		Log.v(Constants.LOG_TAG,"GET Object: " + url);
		InputStream isRawContent = null;
		String sJSONRawString = null;
		JSONObject jaResultJSONObject = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			isRawContent = entity.getContent();

		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getJson Network error" + e.toString());

		}
		if (isRawContent != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(isRawContent, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				isRawContent.close();
				sJSONRawString = sb.toString();
				jaResultJSONObject = new JSONObject(sJSONRawString);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "getJson Error parsing string: " + e.toString());
			} catch (JSONException e1) {
				Log.e(Constants.LOG_TAG, "getJson Error parsing JSON: " + e1.toString());
				throw new JSONException(sJSONRawString);
			} catch (Exception e2) {
				Log.e(Constants.LOG_TAG, "getJson Error: " + e2.toString());
			}
		}
		return jaResultJSONObject;

	}

	/**
	 * POST JSON Array from http
	 *
	 * @param url        URL string for upload
	 * @param jsonObject JSON Object that will be loaded
	 * @return server response
	 * @throws Exception
	 */
	public static JSONObject postJson(String url, JSONObject jsonObject) throws Exception {
		Log.v(Constants.LOG_TAG,"POST: " + url);
		JSONObject json = null;
		String responseText = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(url);
			StringEntity se = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
			se.setContentType("application/json;charset=UTF-8");
			httppostreq.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httppostreq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
		} catch (JSONException e1) {
			throw new JSONException(responseText);
		} catch (Exception e) {
			Log.e("log_tag", "POST: " + e.toString());
		}
		return json;

	}

	/**
	 * PUT JSON Array from http
	 *
	 * @param url        URL string for upload
	 * @param jsonObject JSON Object that will be loaded
	 * @return server response
	 * @throws Exception
	 */
	public static JSONObject putJson(String url, JSONObject jsonObject) throws Exception {
		Log.v(Constants.LOG_TAG,"PUT: " + url);
		JSONObject json = null;
		String responseText = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPut httpputreq = new HttpPut(url);
			StringEntity se = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
			se.setContentType("application/json;charset=UTF-8");
			httpputreq.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httpputreq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
		} catch (JSONException e1) {
			throw new JSONException(responseText);
		} catch (Exception e) {
			Log.e("log_tag", "PUT: " + e.toString());
			throw new JSONException(responseText);
		}
		return json;

	}

	/**
	 * DELETE object in server
	 *
	 * @param url URL string for delete
	 * @return server response
	 * @throws Exception
	 */
	public static JSONObject deleteJson(String url) throws Exception {
		Log.v(Constants.LOG_TAG,"DELETE: " + url);
		JSONObject json = null;
		String responseText = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpDelete httpdeletereq = new HttpDelete(url);
			HttpResponse httpresponse = httpclient.execute(httpdeletereq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
		} catch (Exception e) {
			Log.e("log_tag", "Delete: " + e.toString());
		}
		return json;

	}
/*
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
	 *//*
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


	}*/


}
