package com.senkatel.bereznikov.bulletinboard.cities;

import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Cities {
	private static CopyOnWriteArrayList<City> cities = new CopyOnWriteArrayList<City>();
	private static List<String> citiesList = new ArrayList<String>();

	public static void update(String url) {

		url += Constants.CITY;

		CopyOnWriteArrayList<City> tempCities = new CopyOnWriteArrayList<City>();
		try {

			JSONArray jsonArrayCities = ParseJson.getJson(url);

			int l = jsonArrayCities.length();
			int index = -1;

			for (int i = 0; i < l; i++) {
				JSONObject jsonCity = jsonArrayCities.getJSONObject(i);
				City city = new City();

				city.setId(jsonCity.getInt("id"));
				city.setName(jsonCity.getString("name"));


				tempCities.add(city);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Cities update error: " + e.toString());
		}
		synchronized (cities){
			cities.clear();
			cities.addAll(tempCities);

			citiesList.clear();
			for (City city : cities) {
				citiesList.add(city.getName());
			}

		}
	}

	public static List<String> getCitiesList() {
		return citiesList;
	}

	public static int getId(String name){
		for (City city : cities) {
			if (city.getName().equals(name)){
				return city.getId();
			}
		}
		return -1;
	}

	public static String getName(int id){
		for (City city : cities) {
			if (city.getId() == id){
				return city.getName();
			}
		}
		return null;


	}

	public static void postCity(final String  name) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.CITY;
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("name", name);
					ParseJson.postJson(url, jsonobj);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t POST Category: " + e.toString());
				}
				MainSync.stopSyncingCities();
				Cities.update(Constants.URL);
				MainSync.startSyncingCities();
			}
		});
		thread.start();
	}

}
