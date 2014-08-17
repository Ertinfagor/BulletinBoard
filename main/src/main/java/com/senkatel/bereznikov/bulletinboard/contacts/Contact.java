package com.senkatel.bereznikov.bulletinboard.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONObject;


public class Contact {

	private static String name;
	private static String lastName;
	private static String email;
	private static String phone;
	private static String uid;
	private static Context context;



public static boolean init(Context context){
	Contact.context = context;
	loadPreferences();
	return name != null;

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

	public static void save(){
		savePreferences();

	}

	/*Generate User ID yb checking MAC addres of the device*/
	private static void generateUid(){
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		uid = info.getMacAddress();

	}
	private static void savePreferences(){
		generateUid();
		SharedPreferences preferences = context.getSharedPreferences("contact", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("name", name);
		editor.putString("lastName", lastName);
		editor.putString("email", email);
		editor.putString("phone", phone);
		editor.putString("uid", uid);
		editor.commit();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constants.URL + Constants.CONTACT;
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("name", name);
					jsonobj.put("lastname", lastName);
					jsonobj.put("email", email);
					jsonobj.put("phone", phone);
					jsonobj.put("uid", uid);
					ParseJson.postJson(url,jsonobj);
				}catch (Exception e){
					Log.e(Constants.LOG_TAG,"Can`t POST preferences: " + e.toString());
				}
			}
		});
		thread.start();

		Log.v(Constants.LOG_TAG,"Preferences Saved");
	}
	private static void loadPreferences(){
		SharedPreferences preferences = context.getSharedPreferences("contact", Context.MODE_PRIVATE);
		name =preferences.getString("name",null);
		lastName=preferences.getString("lastName", null);
		email=preferences.getString("email", null);
		phone=preferences.getString("phone", null);
		uid=preferences.getString("uid", null);
		Log.v(Constants.LOG_TAG,"Preferences Loaded");


	}



}
