package com.senkatel.bereznikov.bulletinboard.categories;

import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.cities.City;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class Categories {
	private static CopyOnWriteArrayList<Category> categories = new CopyOnWriteArrayList<Category>();

	private static List<String> categoriesList = new ArrayList<String>();

	public static void update(String url) {

		url += Constants.CATEGORY;

		CopyOnWriteArrayList<Category> tempCategories = new CopyOnWriteArrayList<Category>();
		try {

			JSONArray jsonArrayCategories = ParseJson.getJson(url);

			int l = jsonArrayCategories.length();
			int index = -1;

			for (int i = 0; i < l; i++) {
				JSONObject jsonCategorie = jsonArrayCategories.getJSONObject(i);
				Category category = new Category();

				category.setId(jsonCategorie.getInt("id"));
				category.setName(jsonCategorie.getString("name"));


				tempCategories.add(category);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Categories update error: " + e.toString());
		}
		synchronized (categories){
			categories.clear();
			categories.addAll(tempCategories);

			categoriesList.clear();
			for (Category category : categories) {
				categoriesList.add(category.getName());
			}

		}
	}

	public static List<String> getCategoriesList() {


		return categoriesList;
	}

	public static int getId(String name){
		for (Category category : categories){
			if (category.getName().equals(name)){
				return category.getId();
			}
		}
		return -1;
	}
	public static String getName(int id){
		for (Category category : categories) {
			if (category.getId() == id){
				return category.getName();
			}
		}
		return null;


	}

	public static void postCategory(final String  name){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.CATEGORY;
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("name", name);
							ParseJson.postJson(url,jsonobj);
				}catch (Exception e){
					Log.e(Constants.LOG_TAG,"Can`t POST Category: " + e.toString());
				}
				MainSync.stopSyncingCategories();
				Categories.update(Constants.URL);
				MainSync.startSyncingCategories();
			}
		});
		thread.start();


	}


}
