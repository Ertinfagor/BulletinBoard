package com.senkatel.bereznikov.bulletinboard.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Static Class Contact
 * Implements Contact of current user
 * For identification use ANDROID_ID
 * Implements Save and load prefernces
 */
@SuppressWarnings("ALL")
public class Contact  {

	private static String name = "";
	private static String lastName = "";
	private static String email = "";
	private static String phone = "";
	private static String uid = "";
	private static Context context;
	private static boolean exist = false;



public static boolean init(Context context){
	Contact.context = context;
	loadContact();
	return exist;

}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		Contact.name = name;
	}

	public static String getLastName() {
		return lastName;
	}

	public static void setLastName(String lastName) {
		Contact.lastName = lastName;
	}

	public static String getPhone() {
		return phone;
	}

	public static void setPhone(String phone) {
		Contact.phone = phone;
	}

	public static String getEmail() {
		return email;
	}

	public static void setEmail(String email) {
		Contact.email = email;
	}

	public static String getUid() {
		return uid;
	}

	public static boolean isExist() {
		return exist;
	}

	public static void setExist(boolean exist) {
		Contact.exist = exist;
	}

	public static void save(){
		saveContact();

	}

	/**
	 * Generate unique ID and save class
	 */
	private static void saveContact(){
		uid =  Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		Log.v(Constants.LOG_TAG,"UID: " + uid);
		SharedPreferences preferences = context.getSharedPreferences("contact", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("name", name);
		editor.putString("lastName", lastName);
		editor.putString("email", email);
		editor.putString("phone", phone);
		editor.putString("uid", uid);
		editor.commit();
		uploadContact();
		exist = true;

		Log.v(Constants.LOG_TAG,"Preferences Saved, uid: " + uid);
	}

	/**
	 * Try to load svaed class and if able to load set exist to true
	 */
	private static void loadContact(){
		try {
			SharedPreferences preferences = context.getSharedPreferences("contact", Context.MODE_PRIVATE);
			name = preferences.getString("name", null);
			lastName = preferences.getString("lastName", null);
			email = preferences.getString("email", null);
			phone = preferences.getString("phone", null);
			uid = preferences.getString("uid", null);
		}catch (Exception e){

			Log.e(Constants.LOG_TAG,"Can`t load preferences: " + e.toString());
		}
		try {

			if (uid == null) {

				Contact.setExist(false);
			} else {
				Contact.setExist(true);
			}

		}catch (Exception e){

			Log.e(Constants.LOG_TAG,"Can`t set exist: " + e.toString());
		}

	}

	/**
	 * upload contact to server using ParseJson
	 * First GET Request
	 * If Get Request returns value use PUT else POST
	 * Execute in separate thread
	 */
	public static void uploadContact(){

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean existInDb = true;
				try {
					String getUrl = Constants.URL + Constants.CONTACT;
					getUrl += "/" + uid;
					ParseJson.getJsonObject(getUrl);

				}catch (JSONException e1) {
					existInDb = false;
				}
				catch (Exception e){
					Log.e(Constants.LOG_TAG,"Can`t GET preferences: " + e.toString());
				}
				try {

					String postUrl = Constants.URL + Constants.CONTACT;
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("name", name);
					jsonobj.put("lastname", lastName);
					jsonobj.put("email", email);
					jsonobj.put("phone", phone);
					jsonobj.put("uid", uid);
					if (existInDb){
						postUrl += "/" + uid;
						ParseJson.putJson(postUrl, jsonobj);
					}else {
						ParseJson.postJson(postUrl, jsonobj);
					}
					Log.v(Constants.LOG_TAG,"Exist: " + existInDb);
				}catch (Exception e){
					Log.e(Constants.LOG_TAG,"Can`t POST preferences: " + e.toString());
				}
			}
		});
		thread.start();
	}



}
