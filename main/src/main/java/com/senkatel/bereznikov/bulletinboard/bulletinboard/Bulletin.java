package com.senkatel.bereznikov.bulletinboard.bulletinboard;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.senkatel.bereznikov.bulletinboard.contacts.BulletinContact;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*Class implements container for one bulletin entry
* Class relates to Bulletins */
public class Bulletin implements Parcelable{
	public static final Parcelable.Creator<Bulletin> CREATOR = new Parcelable.Creator<Bulletin>() {
		public Bulletin createFromParcel(Parcel in) {
			return new Bulletin(in);
		}
		public Bulletin[] newArray(int size) {
			return new Bulletin[size];
		}
	};

	private Bitmap image; //Uses only to send bulletin by url and temporary, main images container is Image Class
	private int id = -1;
	private String title = null;
	private String text = null;
	private int city_id = -1;
	private String contact_uid = null;
	private float price = 0;
	private List<Integer> categories = new ArrayList<Integer>();
	private boolean state = true;
	private Date date;
	private BulletinContact contact;



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

	/*Getters and setters*/
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

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public List<Integer> getCategories() {
		return categories;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCategories(List<Integer> categories) {
		this.categories = categories;
	}

	public BulletinContact getContact() {
		return contact;
	}

	public void setContact(BulletinContact contact) {
		this.contact = contact;
	}

	/*Parcable Implementation*/
	private Bulletin(Parcel parcel) {

		int[] ints = new int[2];
		String[] strings = new String[3];

		parcel.readIntArray(ints);
		parcel.readStringArray(strings);

		this.price = parcel.readFloat();

		this.id = ints[0];
		this.city_id = ints[1];

		this.title = strings[0];
		this.text = strings[1];
		this.contact_uid = strings[2];
		parcel.readList(this.categories,Integer.class.getClassLoader());
		this.contact = parcel.readParcelable(Contact.class.getClassLoader());
		this.date = new Date(parcel.readLong());


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
		parcel.writeList(this.categories);
		parcel.writeParcelable(this.contact,0);
		parcel.writeLong(this.date.getTime());
	}
}
