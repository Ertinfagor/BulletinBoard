package com.senkatel.bereznikov.bulletinboard.cities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class City implements Parcelable {
	private int id;
	private String name;


	public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
		// распаковываем объект из Parcel
		public City createFromParcel(Parcel in) {
			return new City(in);
		}

		public City[] newArray(int size) {
			return new City[size];
		}
	};


	private City(Parcel parcel){
		id = parcel.readInt();
		name = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(id);
		parcel.writeString(name);
	}

	public City() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "City{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
