package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.content.Context;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class Bulletins {
	private static CopyOnWriteArrayList<Bulletin> bulletins = new CopyOnWriteArrayList<Bulletin>();

	private static CopyOnWriteArrayList<Integer> indexes = new CopyOnWriteArrayList<Integer>();
	private Context context;
	private static String filter = "?";
	private static String categoriesFilter = "";
	private static String citiesFilter = "";
	private static int categoriesFilterId = -1;
	private static int citiesFilterId = -1;
	private static String costFilterMax = "";
	private static String costFilterMin = "";
	private static float costFilterMaxVale = -1;
	private static float costFilterMinVale = -1;



	public Bulletins(Context context) {
		this.context = context;
	}

	public static void getBulletins(String url) {

		url += Constants.BULLETIN;
		if (!filter.equals("?")){
			url+=filter;

		}

		CopyOnWriteArrayList<Bulletin> tempBulletins = new CopyOnWriteArrayList<Bulletin>();
		try {

			JSONArray jsonArrayBulletins = ParseJson.getJson(url);

			int l = jsonArrayBulletins.length();
			int index = -1;

			for (int i = 0; i < l; i++) {
				JSONObject jsonBulletin = jsonArrayBulletins.getJSONObject(i);
				index = jsonBulletin.getInt("Id");
				indexes.add(index);
				Bulletin bulletin = new Bulletin();
				bulletin.setId(index);
				bulletin.setTitle(jsonBulletin.getString("title"));
				bulletin.setText(jsonBulletin.getString("text"));
				bulletin.setCity_id(jsonBulletin.getInt("city_id"));
				bulletin.setContact_uid(jsonBulletin.getString("contact_uid"));
				bulletin.setPrice(jsonBulletin.getLong("price"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

				tempBulletins.add(bulletin);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getBulletins error: " + e.toString());
		}
		synchronized (bulletins){
			bulletins.clear();
			bulletins.addAll(tempBulletins);

		}
	}

	public static Bulletin get(int id){
		if (id >= 0) {//TODO Подумать как решить не через exception
			try {
				return bulletins.get(id);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Bulletins get error: " + e.toString());
				return new Bulletin();
			}
		}
		return null;
	}

	public static  CopyOnWriteArrayList<Bulletin> getAll(){
		return Bulletins.bulletins;

	}

	public static void setFilterCategories(int categoriesId){
		categoriesFilter = "";
		categoriesFilter = "category=" + categoriesId;
		categoriesFilterId = categoriesId;
		buildFilter();


	}
	public static void setFilterCity(int cityId){
		citiesFilter = "";
		citiesFilter = "city="+cityId;
		citiesFilterId = cityId;
		buildFilter();

	}

	public static void setFilterCostMax(Float costMax){
		costFilterMax = "";
		costFilterMax ="pricemore="+costMax;
		costFilterMaxVale = costMax;
		buildFilter();

	}

	public static void setFilterCostMin(Float costMin){
		costFilterMin = "";
		costFilterMin ="priceless="+costMin;
		costFilterMinVale = costMin;
		buildFilter();

	}

	private static void buildFilter(){
		filter = "?";
		if (!categoriesFilter.equals("")){
			filter+=categoriesFilter;

		}
		if (!citiesFilter.equals("")){
			if (!filter.endsWith("?")){filter+="&";}
			filter += citiesFilter;

		}
		if (!costFilterMax.equals("")){
			if (!filter.endsWith("?")){filter+="&";}
			filter += costFilterMax;

		}
		if (!costFilterMin.equals("")){
			if (!filter.endsWith("?")){filter+="&";}
			filter += costFilterMin;

		}
	}


	public static void resetFilter(){
		filter = "?";
		categoriesFilter=null;
		citiesFilter=null;
		categoriesFilterId = -1;
		citiesFilterId = -1;


	}

	public static String getFilter(){
		return filter;
	}

	public static void postBulletin(final Bulletin  bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN;
					JSONObject postJsonObj = new JSONObject();
					postJsonObj.put("title", bulletin.getTitle());
					postJsonObj.put("text", bulletin.getText());
					postJsonObj.put("city_id", bulletin.getCity_id());
					postJsonObj.put("contact_uid", bulletin.getContact_uid());
					postJsonObj.put("price", bulletin.getPrice());

					JSONArray categories = new JSONArray();
					List<Integer> categoriesIds = bulletin.getCategories();
					for (int i = 0; i < categoriesIds.size(); i++){
						categories.put(i, categoriesIds.get(i));
					}

					postJsonObj.put("categories", categories);

					JSONObject result = ParseJson.postJson(url, postJsonObj);
					String imageurl = Constants.URL + Constants.BULLETIN + "/" + result.getInt("Id") + "/image";
					ParseJson.postImage(imageurl,bulletin.getImage());

				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t POST Category: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
				}
		});
		thread.start();
	}
	public static void putBulletin(final Bulletin  bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN + "/" + String.valueOf(bulletin.getId());
					JSONObject putJsonObj = new JSONObject();
					putJsonObj.put("title", bulletin.getTitle());
					putJsonObj.put("text", bulletin.getText());
					putJsonObj.put("city_id", bulletin.getCity_id());
					putJsonObj.put("contact_uid", bulletin.getContact_uid());
					putJsonObj.put("price", bulletin.getPrice());


					JSONArray categories = new JSONArray();
					List<Integer> categoriesIds = bulletin.getCategories();
					for (int i = 0; i < categoriesIds.size(); i++){
						categories.put(i, categoriesIds.get(i));
					}

					putJsonObj.put("categories", categories);
					ParseJson.putJson(url, putJsonObj);

				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t PUT Category: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
				}
		});
		thread.start();
	}

	public static void deleteBulletin(final Bulletin  bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN + "/" + String.valueOf(bulletin.getId());
					ParseJson.deleteJson(url);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t PUT Category: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
				}
		});
		thread.start();
	}


	public static int getCategoryFilterId() {
		return categoriesFilterId;
	}

	public static int getCityFilterId() {
		return citiesFilterId;
	}
}
