package com.senkatel.bereznikov.bulletinboard.categories;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Category
 * Class is Parcelable
 * Implements container for one Category entry
 * Class relates to Categories
 */
public class Category implements Parcelable {
	private int intCategoryId;
	private String sCategoryName;


	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};




	public Category() {
	}
	/*Getters and Setters*/
	public int getId() {
		return intCategoryId;
	}

	public void setId(int id) {
		this.intCategoryId = id;
	}

	public String getName() {
		return sCategoryName;
	}

	public void setName(String name) {
		this.sCategoryName = name;
	}

	@Override
	public String toString() {
		return "Category{" +
				"intCategoryId=" + intCategoryId +
				", sCategoryName='" + sCategoryName + '\'' +
				'}';
	}

	private Category(Parcel parcel){
		intCategoryId = parcel.readInt();
		sCategoryName = parcel.readString();
	}
	/*Parcelable implementation*/
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(intCategoryId);
		parcel.writeString(sCategoryName);
	}
}
