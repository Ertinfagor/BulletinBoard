package com.senkatel.bereznikov.bulletinboard.util;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class ParseJson {

/*Get JSON Array from http*/
	public static JSONArray getJson(String url){

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
			} catch (IOException e) {
				Log.e(Constants.LOG_TAG, "getJson Error parsing string: " + e.toString());
			}
			try {
				resultJSONArray = new JSONArray(jsonRawString);

			} catch (JSONException e) {
				Log.e(Constants.LOG_TAG, "getJson Error parsing JSON: " + e.toString());
			}
		}
		return resultJSONArray;

	}
	public static JSONObject postJson(String url, JSONObject jsonObject){
		JSONObject json = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(url);
			String responseText = null;
			StringEntity se = new StringEntity(jsonObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json;charset=UTF-8");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			httppostreq.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httppostreq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
			Log.v(Constants.LOG_TAG, "POST: " + json.toString());
		} catch (Exception e) {
			Log.e("log_tag", "POST: " + e.toString());
		}
		return json;

	}
	public static JSONObject putJson(String url, JSONObject jsonObject){
		JSONObject json = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPut httpputreq = new HttpPut(url);
			String responseText = null;
			StringEntity se = new StringEntity(jsonObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json;charset=UTF-8");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			httpputreq.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httpputreq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
			Log.v(Constants.LOG_TAG, "PUT: " + json.toString());
		} catch (Exception e) {
			Log.e("log_tag", "PUT: " + e.toString());
		}
		return json;

	}

	public static JSONObject deleteJson(String url){
		JSONObject json = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpDelete httpdeletereq = new HttpDelete(url);
			String responseText = null;
			HttpResponse httpresponse = httpclient.execute(httpdeletereq);
			responseText = EntityUtils.toString(httpresponse.getEntity());
			json = new JSONObject(responseText);
			Log.v(Constants.LOG_TAG, "Delete: " + json.toString());
		} catch (Exception e) {
			Log.e("log_tag", "Delete: " + e.toString());
		}
		return json;

	}


}
