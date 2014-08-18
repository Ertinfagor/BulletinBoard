package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.content.Context;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONArray;
import org.json.JSONObject;

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
	private static String categoriesFilter = null;
	private static String citiesFilter = null;
	private static int categoriesFilterId = -1;
	private static int citiesFilterId = -1;


	public Bulletins(Context context) {
		this.context = context;
	}

	public static void update(String url) {

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
				tempBulletins.add(bulletin);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Bulletins update error: " + e.toString());
		}
		synchronized (bulletins){
			bulletins.clear();
			bulletins.addAll(tempBulletins);

		}
	}

	public static Bulletin get(int id){
		if (id >= 0) {
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
		categoriesFilter = null;
		categoriesFilter = "category=" + categoriesId;
		categoriesFilterId = categoriesId;
		buildFilter();


	}
	public static void setFilterCity(int cityId){
		citiesFilter = null;
		citiesFilter = "city="+cityId;
		citiesFilterId = cityId;
		buildFilter();

	}

	private static void buildFilter(){
		filter = "?";
		if (categoriesFilter!=null){
			filter+=categoriesFilter;

		}
		if (citiesFilter!=null){
			if (!filter.endsWith("?")){filter+="&";}
			filter += citiesFilter;

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
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("title", bulletin.getTitle());
					jsonobj.put("text", bulletin.getText());
					jsonobj.put("city_id", bulletin.getCity_id());
					jsonobj.put("contact_uid", bulletin.getContact_uid());
					jsonobj.put("price", bulletin.getPrice());
					jsonobj.put("categories", bulletin.getCategories().toArray());
					ParseJson.postJson(url, jsonobj);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t POST Category: " + e.toString());
				}
				MainSync.stopSyncingBulletinBoard();
				Bulletins.update(Constants.URL);
				MainSync.startSyncingBulletinBoard();
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
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("title", bulletin.getTitle());
					jsonobj.put("text", bulletin.getText());
					jsonobj.put("city_id", bulletin.getCity_id());
					jsonobj.put("contact_uid", bulletin.getContact_uid());
					jsonobj.put("price", bulletin.getPrice());


					JSONArray test1 = new JSONArray();
					List<Integer> inted = bulletin.getCategories();
					for (int i = 0; i < inted.size(); i++){
						test1.put(i,inted.get(i));
					}

					jsonobj.put("categories",test1);
					ParseJson.putJson(url, jsonobj);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t PUT Category: " + e.toString());
				}
				MainSync.stopSyncingBulletinBoard();
				Bulletins.update(Constants.URL);
				MainSync.startSyncingBulletinBoard();
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
				MainSync.stopSyncingBulletinBoard();
				Bulletins.update(Constants.URL);
				MainSync.startSyncingBulletinBoard();
			}
		});
		thread.start();
	}


	public static int getCategoriesFilterId() {
		return categoriesFilterId;
	}

	public static int getCitiesFilterId() {
		return citiesFilterId;
	}
}
