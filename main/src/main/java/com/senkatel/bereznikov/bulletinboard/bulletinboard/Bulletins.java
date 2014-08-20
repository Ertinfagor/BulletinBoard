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
 * Class Bulletins
 * Implements collection of Bulletins
 * Implements Filter
 * Implements GET request
 * Implements POST request
 * Implements DELETE request
 *
 */
public class Bulletins {
	private static CopyOnWriteArrayList<Bulletin> arrayBulletins = new CopyOnWriteArrayList<Bulletin>();

	private static CopyOnWriteArrayList<Integer> arrayIndexes = new CopyOnWriteArrayList<Integer>();
	private Context context;
	private static String sFilter = "?";
	private static String sCategoriesFilter = "";
	private static String sCitiesFilter = "";
	private static int iCategoriesFilterId = -1;
	private static int iCitiesFilterId = -1;
	private static String sPriceFilterMax = "";
	private static String sPriceFilterMin = "";
	private static float fPriceFilterMaxValue = -1;
	private static float fPriceFilterMinValue = -1;



	public Bulletins(Context context) {
		this.context = context;
	}

	/**
	 *
	 * @param id get bulletin from bulletins list
	 * @return Bulletin
	 */
	public static Bulletin get(int id){
		if (id >= 0) {
			try {
				return arrayBulletins.get(id);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Bulletins get error: " + e.toString());
				return new Bulletin();
			}
		}
		return null;
	}

	/**
	 * Used for GridView
	 * @return List of all Bulletins
	 */
	public static  CopyOnWriteArrayList<Bulletin> getAll(){
		return Bulletins.arrayBulletins;

	}

	/**
	 * Add to Filter string substring of category filter, and save appropriate value for future usage
	 * @param categoriesId Id of filtered category
	 */
	public static void setFilterCategories(int categoriesId){
		sCategoriesFilter = "";
		sCategoriesFilter = "category=" + categoriesId;
		iCategoriesFilterId = categoriesId;
		buildFilter();


	}

	/**
	 * Add to Filter string substring of city filter, and save appropriate value for future usage
	 * @param cityId  Id of filtered city
	 */
	public static void setFilterCity(int cityId){
		sCitiesFilter = "";
		sCitiesFilter = "city="+cityId;
		iCitiesFilterId = cityId;
		buildFilter();

	}

	/**
	 *  Add to Filter string substring of maximum price filter, and save appropriate value for future usage
	 * @param costMax value of maximum price
	 */
	public static void setFilterPriceMax(Float costMax){
		sPriceFilterMax = "";
		sPriceFilterMax ="pricemore="+costMax;
		fPriceFilterMaxValue = costMax;
		buildFilter();

	}

	/**
	 * Add to Filter string substring of minimum price filter, and save appropriate value for future usage
	 * @param costMin value of minimum price
	 */
	public static void setFilterPriceMin(Float costMin){
		sPriceFilterMin = "";
		sPriceFilterMin ="priceless="+costMin;
		fPriceFilterMinValue = costMin;
		buildFilter();

	}

	/**
	 * Put together all filter substrings and form request string for server
	 */
	private static void buildFilter(){
		sFilter = "?";
		if (!sCategoriesFilter.equals("")){
			sFilter += sCategoriesFilter;

		}
		if (!sCitiesFilter.equals("")){
			if (!sFilter.endsWith("?")){
				sFilter +="&";}
			sFilter += sCitiesFilter;

		}
		if (!sPriceFilterMax.equals("")){
			if (!sFilter.endsWith("?")){
				sFilter +="&";}
			sFilter += sPriceFilterMax;

		}
		if (!sPriceFilterMin.equals("")){
			if (!sFilter.endsWith("?")){
				sFilter +="&";}
			sFilter += sPriceFilterMin;

		}
		Log.v(Constants.LOG_TAG,"Filter Test: " + sFilter);
	}

	/**
	 * return all filter values to its default settings
	 */
	public static void resetFilter(){
		sFilter = "?";
		sCategoriesFilter =null;
		sCitiesFilter =null;
		sPriceFilterMax =null;
		sPriceFilterMin =null;
		iCategoriesFilterId = -1;
		iCitiesFilterId = -1;
		fPriceFilterMaxValue = -1;
		fPriceFilterMinValue = -1;


	}

	public static String getsFilter(){
		return sFilter;
	}

	public static int getCategoryFilterId() {
		return iCategoriesFilterId;
	}

	public static int getCityFilterId() {
		return iCitiesFilterId;
	}

	public static float getfPriceFilterMaxValue() {
		return fPriceFilterMaxValue;
	}

	public static float getfPriceFilterMinValue() {
		return fPriceFilterMinValue;
	}


	/**
	 * Forms GET request from base address + directory bulletin and filter string and call ParseJson Class to get JSON Array
	 * Parse JSON Array and set values for bulletins
	 * Must use Thread or AsyncTask to Implement this method
	 * @param url base address of server
	 */
	public static void getBulletins(String url) {

		url += Constants.BULLETIN;
		if (!sFilter.equals("?")){
			url+= sFilter;
		}

		CopyOnWriteArrayList<Bulletin> tempBulletins = new CopyOnWriteArrayList<Bulletin>();
		try {

			JSONArray jsonArrayBulletins = ParseJson.getJson(url);

			int l = jsonArrayBulletins.length();
			int index = -1;

			for (int i = 0; i < l; i++) {
				JSONObject jsonBulletin = jsonArrayBulletins.getJSONObject(i);
				index = jsonBulletin.getInt("Id");
				arrayIndexes.add(index);
				Bulletin bulletin = new Bulletin();
				bulletin.setIntBulletinId(index);
				bulletin.setsTitle(jsonBulletin.getString("title"));
				bulletin.setsText(jsonBulletin.getString("text"));
				bulletin.setIntCity_id(jsonBulletin.getInt("city_id"));
				bulletin.setIntContact_uid(jsonBulletin.getString("contact_uid"));
				/*Parse contact object*/
				JSONObject jsonObjectContact = jsonBulletin.getJSONObject("contact");
				BulletinContact bulletinContact = new BulletinContact();
				bulletinContact.setName(jsonObjectContact.getString("name"));
				bulletinContact.setLastName(jsonObjectContact.getString("lastname"));
				bulletinContact.setEmail(jsonObjectContact.getString("email"));
				bulletinContact.setPhone(jsonObjectContact.getString("phone"));
				bulletinContact.setUid(jsonObjectContact.getString("uid"));
				bulletin.setBcBulletinContact(bulletinContact);
				/*Parse categories array*/
				JSONArray jsonArrayCategories = jsonBulletin.getJSONArray("categories");
				int k = jsonArrayCategories.length();
				List<Integer> listCategories = new ArrayList<Integer>();
				for (int j=0 ; j<k; j++){
					listCategories.add(jsonArrayCategories.getInt(j));
				}
				bulletin.setListCategories(listCategories);
				/*Parse int to bool*/
				if(jsonBulletin.getInt("state")==0){
					bulletin.setbState(true);
				}else {
					bulletin.setbState(false);
				}
				bulletin.setfPrice(jsonBulletin.getLong("price"));
				/*Parse date*/
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATEFORMAT);
				bulletin.setdBulletinDate(sdf.parse(jsonBulletin.getString("date")));


				tempBulletins.add(bulletin);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "getBulletins error: " + e.toString());
		}
		synchronized (arrayBulletins){
			arrayBulletins.clear();
			arrayBulletins.addAll(tempBulletins);

		}
	}

	/**
	 * Forms POST request to send new bulletin to server
	 * Executed on separate thread
	 * After POST Text data receive ID of bulletin and send image
	 * @param bulletin bulletin that will sended
	 */
	public static void postBulletin(final Bulletin  bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN;
					JSONObject postJsonObj = new JSONObject();
					postJsonObj.put("title", bulletin.getsTitle());
					postJsonObj.put("text", bulletin.getsText());
					postJsonObj.put("city_id", bulletin.getIntCity_id());
					postJsonObj.put("contact_uid", bulletin.getIntContact_uid());
					postJsonObj.put("price", bulletin.getfPrice());
					/*convert bool to int*/
					if (bulletin.isbState()) {
						postJsonObj.put("state", 1);
					}else{
						postJsonObj.put("state", 0);
					}

					JSONArray categories = new JSONArray();
					List<Integer> categoriesIds = bulletin.getListCategories();
					for (int i = 0; i < categoriesIds.size(); i++){
						categories.put(i, categoriesIds.get(i));
					}

					postJsonObj.put("categories", categories);

					JSONObject result = ParseJson.postJson(url, postJsonObj);
					String imageurl = Constants.URL + Constants.BULLETIN + "/" + result.getInt("Id") + "/image";
					ParseJson.postImage(imageurl,bulletin.getBmpImage());

				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t POST Category: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
				}
		});
		thread.start();
	}

	/**
	 * Forms PUT request to send edited bulletin to server
	 * Executed on separate thread
	 *
	 * @param bulletin edited bulletin that will sended
	 */
	public static void putBulletin(final Bulletin  bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN + "/" + String.valueOf(bulletin.getIntBulletinId());
					JSONObject putJsonObj = new JSONObject();
					putJsonObj.put("title", bulletin.getsTitle());
					putJsonObj.put("text", bulletin.getsText());
					putJsonObj.put("city_id", bulletin.getIntCity_id());
					putJsonObj.put("contact_uid", bulletin.getIntContact_uid());
					putJsonObj.put("price", bulletin.getfPrice());
					/*convert bool to int*/
					if (bulletin.isbState()) {
						putJsonObj.put("state", 1);
					}else{
						putJsonObj.put("state", 0);
					}

					JSONArray categories = new JSONArray();
					List<Integer> categoriesIds = bulletin.getListCategories();
					for (int i = 0; i < categoriesIds.size(); i++){
						categories.put(i, categoriesIds.get(i));
					}

					putJsonObj.put("categories", categories);
					ParseJson.putJson(url, putJsonObj);
					String imageurl = Constants.URL + Constants.BULLETIN + "/" + bulletin.getIntBulletinId() + "/image";
					ParseJson.postImage(imageurl,bulletin.getBmpImage());

				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t PUT Category: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
				}
		});
		thread.start();
	}

	/**
	 * Send DELETE command to server
	 * Executed on separate thread
	 * @param bulletin bulletin that's will be deleted
	 */
	public static void deleteBulletin(final Bulletin  bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN + "/" + String.valueOf(bulletin.getIntBulletinId());
					ParseJson.deleteJson(url);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t PUT Category: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
				}
		});
		thread.start();
	}

}
