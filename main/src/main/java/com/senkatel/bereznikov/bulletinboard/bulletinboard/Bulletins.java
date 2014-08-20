package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.content.Context;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.contacts.BulletinContact;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private static float costFilterMaxValue = -1;
	private static float costFilterMinValue = -1;



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
				/*Parse contact object*/
				JSONObject jsonObjectContact = jsonBulletin.getJSONObject("contact");
				BulletinContact bulletinContact = new BulletinContact();
				bulletinContact.setName(jsonObjectContact.getString("name"));
				bulletinContact.setLastName(jsonObjectContact.getString("lastname"));
				bulletinContact.setEmail(jsonObjectContact.getString("email"));
				bulletinContact.setPhone(jsonObjectContact.getString("phone"));
				bulletinContact.setUid(jsonObjectContact.getString("uid"));
				bulletin.setContact(bulletinContact);
				/*Parse categories array*/
				JSONArray jsonArrayCategories = jsonBulletin.getJSONArray("categories");
				int k = jsonArrayCategories.length();
				List<Integer> listCategories = new ArrayList<Integer>();
				for (int j=0 ; j<k; j++){
					listCategories.add(jsonArrayCategories.getInt(j));
				}
				bulletin.setCategories(listCategories);
				/*Parse int to bool*/
				if(jsonBulletin.getInt("state")==0){
					bulletin.setState(true);
				}else {
					bulletin.setState(false);
				}
				bulletin.setPrice(jsonBulletin.getLong("price"));
				/*Parse date*/
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATEFORMAT);
				bulletin.setDate(sdf.parse(jsonBulletin.getString("date")));


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
		costFilterMaxValue = costMax;
		buildFilter();

	}

	public static void setFilterCostMin(Float costMin){
		costFilterMin = "";
		costFilterMin ="priceless="+costMin;
		costFilterMinValue = costMin;
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
		Log.v(Constants.LOG_TAG,"Filter Test: " + filter);
	}


	public static void resetFilter(){
		filter = "?";
		categoriesFilter=null;
		citiesFilter=null;
		costFilterMax=null;
		costFilterMin=null;
		categoriesFilterId = -1;
		citiesFilterId = -1;
		costFilterMaxValue = -1;
		costFilterMinValue = -1;


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
					/*convert bool to int*/
					if (bulletin.isState()) {
						postJsonObj.put("state", 1);
					}else{
						postJsonObj.put("state", 0);
					}

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
					/*convert bool to int*/
					if (bulletin.isState()) {
						putJsonObj.put("state", 1);
					}else{
						putJsonObj.put("state", 0);
					}

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

	public static float getCostFilterMaxValue() {
		return costFilterMaxValue;
	}

	public static float getCostFilterMinValue() {
		return costFilterMinValue;
	}
}
