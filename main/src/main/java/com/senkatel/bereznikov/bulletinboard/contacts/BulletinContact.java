package com.senkatel.bereznikov.bulletinboard.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/*Class implements container for contacts
* Class relates to bulletin */
public class BulletinContact implements Parcelable{
	public static final Parcelable.Creator<BulletinContact> CREATOR = new Parcelable.Creator<BulletinContact>() {
		public BulletinContact createFromParcel(Parcel in) {
			return new BulletinContact(in);
		}
		public BulletinContact[] newArray(int size) {
			return new BulletinContact[size];
		}
	};

	private  String name = "";
	private  String lastName = "";
	private  String email = "";
	private  String phone = "";
	private  String uid = "";

	public BulletinContact() {
	}

	/*Create non-static instance of curent user Contact for sending*/
	public BulletinContact(Contact contact) {
		this.name = Contact.getName();
		this.lastName = Contact.getLastName();
		this.email = Contact.getEmail();
		this.phone = Contact.getPhone();
		this.uid = Contact.getUid();

	}



/*Getters and setters*/

	public  String getName() {
		return name;
	}

	public  void setName(String name) {
		this.name = name;
	}

	public  String getLastName() {
		return lastName;
	}

	public  void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public  String getPhone() {
		return phone;
	}

	public  void setPhone(String phone) {
		this.phone = phone;
	}

	public  String getEmail() {
		return email;
	}

	public  void setEmail(String email) {
		this.email = email;
	}

	public  String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}


	/*Parcable Implementation*/
	private BulletinContact(Parcel parcel) {

		String[] strings = new String[5];
		parcel.readStringArray(strings);
		this.name = strings[0];
		this.lastName = strings[1];
		this.phone = strings[2];
		this.email = strings[3];
		this.uid = strings[4];



	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {

		String[] strings = {this.name,this.lastName,this.phone,this.email,this.uid};

		parcel.writeStringArray(strings);

	}

}
