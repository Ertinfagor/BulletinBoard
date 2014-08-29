package com.senkatel.bereznikov.bulletinboard.contacts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class BulletinContact
 * Class Parcelable
 * Implements container for contacts
 * Class relates to bulletin
 */
public class BulletinContact implements Parcelable {
	public static final Parcelable.Creator<BulletinContact> CREATOR = new Parcelable.Creator<BulletinContact>() {
		public BulletinContact createFromParcel(Parcel in) {
			return new BulletinContact(in);
		}

		public BulletinContact[] newArray(int size) {
			return new BulletinContact[size];
		}
	};

	private String sName = "";
	private String sLastName = "";
	private String sEmail = "";
	private String sPhone = "";
	private String sUid = "";

	public BulletinContact() {
	}

	/**
	 * Create non-static instance of current user Contact for sending
	 */
	public BulletinContact(Contact contact) {
		this.sName = Contact.getName();
		this.sLastName = Contact.getLastName();
		this.sEmail = Contact.getEmail();
		this.sPhone = Contact.getPhone();
		this.sUid = Contact.getUid();

	}



/*Getters and setters*/

	public String getName() {
		return sName;
	}

	public void setName(String name) {
		this.sName = name;
	}

	public String getLastName() {
		return sLastName;
	}

	public void setLastName(String lastName) {
		this.sLastName = lastName;
	}

	public String getPhone() {
		return sPhone;
	}

	public void setPhone(String phone) {
		this.sPhone = phone;
	}

	public String getEmail() {
		return sEmail;
	}

	public void setEmail(String email) {
		this.sEmail = email;
	}

	public String getUid() {
		return sUid;
	}

	public void setUid(String uid) {
		this.sUid = uid;
	}


	/*Parcelable Implementation*/
	private BulletinContact(Parcel parcel) {

		String[] strings = new String[5];
		parcel.readStringArray(strings);
		this.sName = strings[0];
		this.sLastName = strings[1];
		this.sPhone = strings[2];
		this.sEmail = strings[3];
		this.sUid = strings[4];


	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {

		String[] strings = {this.sName, this.sLastName, this.sPhone, this.sEmail, this.sUid};

		parcel.writeStringArray(strings);

	}

}
