package com.senkatel.bereznikov.bulletinboard.categories;

import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Static Class Categories
 * Implements Categories array
 * Implements GET method
 * Implements POST Method
 * Implements method to obtain name by id
 * Implements method to obtain id by name
 * Has separate array that contains only categories names without ids used in ListView Activities
 */
@SuppressWarnings("ALL")
public class Categories {
	private static CopyOnWriteArrayList<Category> listCategories = new CopyOnWriteArrayList<Category>();

	private static List<String> listCategoriesNames = new ArrayList<String>();



	public static List<String> getListCategoriesNames() {

		return listCategoriesNames;
	}

	public static int getId(String name){
		for (Category category : listCategories){
			if (category.getName().equals(name)){
				return category.getId();
			}
		}
		return -1;
	}
	public static String getName(int id){
		for (Category category : listCategories) {
			if (category.getId() == id){
				return category.getName();
			}
		}
	return null;
	}

	/**
	 * Forms GET request from base address + directory category and call ParseJson Class to get JSON Array
	 * Parse JSON Array and set categories
	 * Must use Thread or AsyncTask to Implement this method
	 * When execute method data load to temp array
	 * @param url base address of server
	 */
	public synchronized static void getCategories(String url) {

		url += Constants.CATEGORY;

		CopyOnWriteArrayList<Category> tempCategories = new CopyOnWriteArrayList<Category>();
		try {

			JSONArray jsonArrayCategories = ParseJson.getJsonArray(url);

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
			Log.e(Constants.LOG_TAG, "Categories getCategories error: " + e.toString());
		}
		synchronized (listCategories){
			listCategories.clear();
			listCategories.addAll(tempCategories);

			listCategoriesNames.clear();
			for (Category category : listCategories) {
				listCategoriesNames.add(category.getName());
			}

		}
	}

	/**
	 * Forms POST request to upload new Category and then force to load categories from server
	 * Execute in separate thread
	 * @param name Name of new category
	 */
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
				Categories.getCategories(Constants.URL);

			}
		});
		thread.start();


	}
}
