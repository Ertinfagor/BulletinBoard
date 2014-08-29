package com.senkatel.bereznikov.bulletinboard.cities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class City
 * Class is Parcelable
 * Implements container for one City entry
 * Class relates to Cities
 */
public class City implements Parcelable {
	private int intCityId;
	private String sCityName;


	public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {

		public City createFromParcel(Parcel in) {
			return new City(in);
		}

		public City[] newArray(int size) {
			return new City[size];
		}
	};


	public City() {
	}

	/*Getters and setters*/
	public int getId() {
		return intCityId;
	}

	public void setId(int id) {
		this.intCityId = id;
	}

	public String getName() {
		return sCityName;
	}

	public void setName(String name) {
		this.sCityName = name;
	}

	@Override
	public String toString() {
		return "City{" +
				"intCityId=" + intCityId +
				", sCityName='" + sCityName + '\'' +
				'}';
	}

	/*Implements Parcelable*/
	private City(Parcel parcel) {
		intCityId = parcel.readInt();
		sCityName = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(intCityId);
		parcel.writeString(sCityName);
	}
}
