package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.contacts.BulletinContact;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Filter;
import com.senkatel.bereznikov.bulletinboard.util.Images;
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
 */
@SuppressWarnings("ALL")
public class Bulletins {
	private static CopyOnWriteArrayList<Bulletin> arrayBulletins = new CopyOnWriteArrayList<Bulletin>();

	private static CopyOnWriteArrayList<Integer> arrayIndexes = new CopyOnWriteArrayList<Integer>();
	private Context context;


	public Bulletins(Context context) {
		this.context = context;
	}

	/**
	 * @param id get bulletin from bulletins list
	 * @return Bulletin
	 */
	public static Bulletin get(int id) {
		if (id < arrayBulletins.size()) {
			try {
				return arrayBulletins.get(id);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Bulletins get error: " + e.toString());
				return new Bulletin();
			}
		}
		return new Bulletin();
	}

	/**
	 * Used for GridView
	 *
	 * @return List of all Bulletins
	 */
	public static CopyOnWriteArrayList<Bulletin> getAll() {
		return Bulletins.arrayBulletins;

	}


	/**
	 * Forms GET request from base address + directory bulletin and filter string and call ParseJson Class to get JSON Array
	 * Parse JSON Array and set values for bulletins
	 * Must use Thread or AsyncTask to Implement this method
	 *
	 * @param url base address of server
	 */
	public synchronized static void getBulletins(String url) {

		url += Constants.BULLETIN;
		if (Filter.isFilter()) {
			url += Filter.getsFilter();
		}
		CopyOnWriteArrayList<Bulletin> tempBulletins = new CopyOnWriteArrayList<Bulletin>();
		try {
			JSONArray jsonArrayBulletins = ParseJson.getJsonArray(url);
			int l = jsonArrayBulletins.length();
			int index = -1;

			for (int i = 0; i < jsonArrayBulletins.length(); i++) {
				JSONObject jsonBulletin = jsonArrayBulletins.getJSONObject(i);
				index = jsonBulletin.getInt("id");
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
				for (int j = 0; j < k; j++) {
					listCategories.add(jsonArrayCategories.getInt(j));
				}
				bulletin.setListCategories(listCategories);
				/*Parse int to bool*/
				if (jsonBulletin.getInt("state") == 0) {
					bulletin.setbState(true);
				} else {
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
		arrayBulletins.clear();
		arrayBulletins.addAll(tempBulletins);


	}

	/**
	 * Forms POST request to send new bulletin to server
	 * Executed on separate thread
	 * After POST Text data receive ID of bulletin and send image
	 *
	 * @param bulletin bulletin that will sended
	 * @return true if bulletin posted
	 */
	public static boolean postBulletin(final Bulletin bulletin) throws Exception {
		final Handler handler = new Handler();

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
					} else {
						postJsonObj.put("state", 0);
					}

					JSONArray categories = new JSONArray();
					List<Integer> categoriesIds = bulletin.getListCategories();
					for (int i = 0; i < categoriesIds.size(); i++) {
						categories.put(i, categoriesIds.get(i));
					}

					postJsonObj.put("categories", categories);

					JSONObject result = ParseJson.postJson(url, postJsonObj);
					Images.uploadTemp(result.getInt("id"));
					handler.sendEmptyMessage(Constants.STATUS_OK);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t POST Bulletin: " + e.toString());
					handler.sendEmptyMessage(Constants.STATUS_FAIL);

				}
				Bulletins.getBulletins(Constants.URL);
			}
		});
		thread.start();
		thread.join();


		if (handler.hasMessages(Constants.STATUS_OK)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Forms PUT request to send edited bulletin to server
	 * Executed on separate thread
	 * removes current bitmap from cache
	 *
	 * @param bulletin edited bulletin that will sended
	 * @return true if bulletin puted
	 */
	public static boolean putBulletin(final Bulletin bulletin) throws Exception {
		final Handler handler = new Handler();
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
					} else {
						putJsonObj.put("state", 0);
					}

					JSONArray categories = new JSONArray();
					List<Integer> categoriesIds = bulletin.getListCategories();
					for (int i = 0; i < categoriesIds.size(); i++) {
						categories.put(i, categoriesIds.get(i));
					}

					putJsonObj.put("categories", categories);
					JSONObject result = ParseJson.putJson(url, putJsonObj);
					Images.uploadTemp(bulletin.getIntBulletinId());
					Images.removeBitmapFromMemoryCache(bulletin.getIntBulletinId());
					handler.sendEmptyMessage(Constants.STATUS_OK);

				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t PUT Bulletin: " + e.toString());
					handler.sendEmptyMessage(Constants.STATUS_FAIL);
				}
				Bulletins.getBulletins(Constants.URL);
			}
		});
		thread.start();
		thread.join();


		if (handler.hasMessages(Constants.STATUS_OK)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Send DELETE command to server
	 * Executed on separate thread
	 *
	 * @param bulletin bulletin that's will be deleted
	 */
	public static void deleteBulletin(final Bulletin bulletin) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.BULLETIN + "/" + String.valueOf(bulletin.getIntBulletinId());
					ParseJson.deleteJson(url);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t DELETE bulletin: " + e.toString());
				}
				Bulletins.getBulletins(Constants.URL);
			}
		});
		thread.start();
	}

}
