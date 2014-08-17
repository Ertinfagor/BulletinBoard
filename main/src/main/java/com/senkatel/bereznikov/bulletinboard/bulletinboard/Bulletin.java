package com.senkatel.bereznikov.bulletinboard.bulletinboard;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;


public class Bulletin implements Parcelable{
	public static final Parcelable.Creator<Bulletin> CREATOR = new Parcelable.Creator<Bulletin>() {
		public Bulletin createFromParcel(Parcel in) {
			return new Bulletin(in);
		}
		public Bulletin[] newArray(int size) {
			return new Bulletin[size];
		}
	};

	private int id = -1;
	private String title = null;
	private String text = null;
	private int city_id = -1;
	private String contact_uid = null;
	private float price = 0;
	//state
	//private Date date = null;
	//private int[] categories = null;

	public Bulletin(int id, String title, String text, int city_id, String contact_uid, float price) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.city_id = city_id;
		this.contact_uid = contact_uid;
		this.price = price;
	}

	private Bulletin(Parcel parcel) {

		int[] ints = new int[2];
		String[] strings = new String[3];

		parcel.readIntArray(ints);
		parcel.readStringArray(strings);

		this.price = parcel.readLong();

		this.id = ints[0];
		this.city_id = ints[1];

		this.title = strings[0];
		this.text = strings[1];
		this.contact_uid = strings[2];
	}

	public Bulletin() {
	}

	@Override
	public String toString() {
		return "Bulletin{" +
				"id=" + id +
				", title='" + title + '\'' +
				", text='" + text + '\'' +
				", city_id=" + city_id +
				", contact_uid='" + contact_uid + '\'' +
				", price=" + price +
				'}';
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public int getCity_id() {
		return city_id;
	}

	public String getContact_uid() {
		return contact_uid;
	}

	public float getPrice() {
		return price;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public void setContact_uid(String contact_uid) {
		this.contact_uid = contact_uid;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		int[] ints = {this.id,this.city_id};
		String[] strings = {this.title,this.text,this.contact_uid};

		parcel.writeIntArray(ints);
		parcel.writeStringArray(strings);
		parcel.writeFloat(this.price);
	}
}
