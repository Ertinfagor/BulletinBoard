package com.senkatel.bereznikov.bulletinboard.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;


/**
 * Created by Bereznik on 16.08.2014.
 */
public class ParseJson {

/*Get JSON Array from http*/
	public static JSONArray getJson(String url) throws Exception{

		InputStream rawContent = null;
		String jsonRawString = null;
		JSONArray resultJSONArray = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			rawContent = entity.getContent();

		}catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getJson Network error" + e.toString());

		}
		if (rawContent != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(rawContent, "UTF-8"), 8);//Было  iso-8859-1
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				rawContent.close();
				jsonRawString = sb.toString();
				resultJSONArray = new JSONArray(jsonRawString);
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "getJson Error parsing string: " + e.toString());
			} catch (JSONException e1) {
				Log.e(Constants.LOG_TAG, "getJson Error parsing JSON: " + e1.toString());
				throw new JSONException(jsonRawString);
			}catch (Exception e2){
				Log.e(Constants.LOG_TAG, "getJson Error: " + e2.toString());
			}
		}
		return resultJSONArray;

	}
	public static JSONObject postJson(String url, JSONObject jsonObject) throws Exception{
		JSONObject json = null;
		String responseText = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(url);
			StringEntity se = new StringEntity(jsonObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json;charset=UTF-8");
			httppostreq.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httppostreq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
		}catch (JSONException e1){
			throw new JSONException(responseText);
		} catch (Exception e) {
			Log.e("log_tag", "POST: " + e.toString());
		}
		return json;

	}
	public static JSONObject putJson(String url, JSONObject jsonObject)throws Exception{
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
		}catch (JSONException e1){
			throw new JSONException(responseText);
		} catch (Exception e) {
			Log.e("log_tag", "PUT: " + e.toString());
			throw new JSONException(responseText);
		}
		return json;

	}

	public static JSONObject deleteJson(String url)throws Exception{
		JSONObject json = null;
		String responseText = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpDelete httpdeletereq = new HttpDelete(url);
			HttpResponse httpresponse = httpclient.execute(httpdeletereq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
		}catch (JSONException e1){
			throw new JSONException(responseText);
		} catch (Exception e) {
			Log.e("log_tag", "Delete: " + e.toString());
		}
		return json;

	}

	public static void postImage(String url, Bitmap image)throws Exception{
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
		}


	}

	public static Bitmap getImage(String url)throws Exception{

			InputStream rawContent = null;
			Bitmap resultBitmap = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				rawContent = entity.getContent();
				resultBitmap = BitmapFactory.decodeStream(rawContent);
			}catch (Exception e) {
				Log.e(Constants.LOG_TAG, "getBitmap Network error" + e.toString());

			}
	return resultBitmap;

	}

}
