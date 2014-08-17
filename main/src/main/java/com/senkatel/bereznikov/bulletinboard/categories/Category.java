package com.senkatel.bereznikov.bulletinboard.categories;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class Category implements Parcelable {
	private int id;
	private String name;


	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		// распаковываем объект из Parcel
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};


	private Category(Parcel parcel){
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

	public Category() {
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
		return "Category{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
