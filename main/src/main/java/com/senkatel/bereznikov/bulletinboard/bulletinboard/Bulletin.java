package com.senkatel.bereznikov.bulletinboard.bulletinboard;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.senkatel.bereznikov.bulletinboard.contacts.BulletinContact;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class Bulletin
 * Class is Parcelable
 * Implements container for one bulletin entry
* Class relates to Bulletins
*/

public class Bulletin implements Parcelable{
	public static final Parcelable.Creator<Bulletin> CREATOR = new Parcelable.Creator<Bulletin>() {
		public Bulletin createFromParcel(Parcel in) {
			return new Bulletin(in);
		}
		public Bulletin[] newArray(int size) {
			return new Bulletin[size];
		}
	};

	private Bitmap bmpImage; //Uses only to send bulletin by url and temporary, main images container is Image Class
	private int intBulletinId = -1;
	private String sTitle = null;
	private String sText = null;
	private int intCity_id = -1;
	private String intContact_uid = null;
	private float fPrice = 0;
	private List<Integer> listCategories = new ArrayList<Integer>();
	private boolean bState = true;
	private Date dBulletinDate;
	private BulletinContact bcBulletinContact;



	public Bulletin() {
	}

	@Override
	public String toString() {
		return "Bulletin{" +
				"intBulletinId=" + intBulletinId +
				", sTitle='" + sTitle + '\'' +
				", sText='" + sText + '\'' +
				", intCity_id=" + intCity_id +
				", intContact_uid='" + intContact_uid + '\'' +
				", fPrice=" + fPrice +
				'}';
	}

	/*Getters and setters*/


	public int getIntBulletinId() {
		return intBulletinId;
	}

	public String getsTitle() {
		return sTitle;
	}

	public String getsText() {
		return sText;
	}

	public int getIntCity_id() {
		return intCity_id;
	}

	public String getIntContact_uid() {
		return intContact_uid;
	}

	public float getfPrice() {
		return fPrice;
	}

	public void setIntBulletinId(int intBulletinId) {
		this.intBulletinId = intBulletinId;
	}

	public void setsTitle(String sTitle) {
		this.sTitle = sTitle;
	}

	public void setsText(String sText) {
		this.sText = sText;
	}

	public void setIntCity_id(int intCity_id) {
		this.intCity_id = intCity_id;
	}

	public void setIntContact_uid(String intContact_uid) {
		this.intContact_uid = intContact_uid;
	}

	public void setfPrice(float fPrice) {
		this.fPrice = fPrice;
	}

	public boolean isbState() {
		return bState;
	}

	public void setbState(boolean bState) {
		this.bState = bState;
	}

	public Bitmap getBmpImage() {
		return bmpImage;
	}

	public void setBmpImage(Bitmap bmpImage) {
		this.bmpImage = bmpImage;
	}

	public List<Integer> getListCategories() {
		return listCategories;
	}

	public Date getdBulletinDate() {
		return dBulletinDate;
	}

	public void setdBulletinDate(Date dBulletinDate) {
		this.dBulletinDate = dBulletinDate;
	}

	public void setListCategories(List<Integer> listCategories) {
		this.listCategories = listCategories;
	}

	public BulletinContact getBcBulletinContact() {
		return bcBulletinContact;
	}

	public void setBcBulletinContact(BulletinContact bcBulletinContact) {
		this.bcBulletinContact = bcBulletinContact;
	}

	/*Parcable Implementation*/
	private Bulletin(Parcel parcel) {

		int[] intArrayParcel = new int[2];
		String[] strArrayParcel = new String[3];

		parcel.readIntArray(intArrayParcel);
		parcel.readStringArray(strArrayParcel);

		this.fPrice = parcel.readFloat();

		this.intBulletinId = intArrayParcel[0];
		this.intCity_id = intArrayParcel[1];

		this.sTitle = strArrayParcel[0];
		this.sText = strArrayParcel[1];
		this.intContact_uid = strArrayParcel[2];
		parcel.readList(this.listCategories,Integer.class.getClassLoader());
		this.bcBulletinContact = parcel.readParcelable(Contact.class.getClassLoader());
		this.dBulletinDate = new Date(parcel.readLong());


	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		int[] intArrayParcel = {this.intBulletinId,this.intCity_id};
		String[] strArrayParcel = {this.sTitle,this.sText,this.intContact_uid};

		parcel.writeIntArray(intArrayParcel);
		parcel.writeStringArray(strArrayParcel);
		parcel.writeFloat(this.fPrice);
		parcel.writeList(this.listCategories);
		parcel.writeParcelable(this.bcBulletinContact,0);
		parcel.writeLong(this.dBulletinDate.getTime());
	}
}
