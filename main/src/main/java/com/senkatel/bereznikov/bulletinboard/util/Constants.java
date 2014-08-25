package com.senkatel.bereznikov.bulletinboard.util;

/**
 * Interface Constants
 * Contains constants of application
 */
public interface Constants {
	public static final String LOG_TAG = "BulletinBoard";
	public static final String URL = "http://195.66.136.204:3000";
	//public static final String URL = "http://mdm.senkatel.com:3000";
	public static final String BULLETIN = "/bulletin";
	public static final String CATEGORY = "/category";
	public static final String CITY = "/city";
	public static final String CONTACT = "/contact";
	public static final String IMAGE = "/image";
	//new public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ";
	public static final int IMAGE_HEIHT = 100;
	public static final int IMAGE_HEIHT_MAX = 1024;
	public static final int IMAGE_WIDTH = 100;
	public static final int IMAGE_WIDTH_MAX = 1024;
	public static final int UPDATE_INTERVAL = 60;
	public static final int MEMORY_CACHE_USAGE_DIVISOR = 8;

}
