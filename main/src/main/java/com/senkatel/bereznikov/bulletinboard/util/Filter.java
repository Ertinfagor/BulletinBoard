package com.senkatel.bereznikov.bulletinboard.util;

import com.senkatel.bereznikov.bulletinboard.contacts.Contact;

/**
 * Static Class Filter
 * Implements filtering options (form filtering string)
 */
public class Filter {
	private static String sFilter = "?";
	private static String sCategoriesFilter = "";
	private static int iCategoriesFilterId = -1;
	private static String sCitiesFilter = "";
	private static int iCitiesFilterId = -1;
	private static String sStatusFilter = "";
	private static int iStatusFilterValue = -1;
	private static String sTagFilter = "";
	private static String sTagFilterValue = "";
	private static String sPriceFilterMax = "";
	private static float fPriceFilterMaxValue = -1;
	private static String sPriceFilterMin = "";
	private static float fPriceFilterMinValue = -1;
	private static boolean myBulletins = false;

	/**
	 * Add to Filter string substring of category filter, and save appropriate value for future usage
	 * reset My Bulletin filter
	 *
	 * @param categoriesId Id of filtered category
	 */
	public static void setFilterCategories(int categoriesId) {
		myBulletins = false;
		sCategoriesFilter = "";
		sCategoriesFilter = "category=" + categoriesId;
		iCategoriesFilterId = categoriesId;
		buildFilter();
	}

	/**
	 * Add to Filter string substring of city filter, and save appropriate value for future usage
	 * reset My Bulletin filter
	 *
	 * @param cityId Id of filtered city
	 */
	public static void setFilterCity(int cityId) {
		myBulletins = false;
		sCitiesFilter = "";
		sCitiesFilter = "city=" + cityId;
		iCitiesFilterId = cityId;
		buildFilter();
	}

	/**
	 * Add to Filter string substring of maximum price filter, and save appropriate value for future usage
	 * reset My Bulletin filter
	 *
	 * @param costMax value of maximum price
	 */
	public static void setFilterPriceMax(Float costMax) {
		if (costMax >= 0) {
			myBulletins = false;
			sPriceFilterMax = "";
			sPriceFilterMax = "priceless=" + costMax;
			fPriceFilterMaxValue = costMax;
		} else {
			sPriceFilterMax = "";
			fPriceFilterMaxValue = costMax;
		}
		buildFilter();

	}

	/**
	 * Add to Filter string substring of minimum price filter, and save appropriate value for future usage
	 * reset My Bulletin filter
	 *
	 * @param costMin value of minimum price
	 */
	public static void setFilterPriceMin(Float costMin) {
		if (costMin >= 0) {
			myBulletins = false;
			sPriceFilterMin = "";
			sPriceFilterMin = "pricemore=" + costMin;
			fPriceFilterMinValue = costMin;
		} else {
			sPriceFilterMin = "";
			fPriceFilterMinValue = costMin;
		}
		buildFilter();
	}

	/**
	 * filtering by tag name
	 * reset My Bulletin filter
	 *
	 * @param tag string for search
	 */
	public static void setFilterTag(String tag) {
		myBulletins = false;
		sTagFilter = "";
		sTagFilter = "tag=" + tag;
		sTagFilterValue = tag;
		buildFilter();
	}
	public static void setFilterStatus(String status) {
		myBulletins = false;
		sStatusFilter = "";
		if (status.equals("new")) {
			sStatusFilter = "state=" + 1;
			iStatusFilterValue = 1;
		}else{
			sStatusFilter = "state=" + 0;
			iStatusFilterValue = 0;
		}

		buildFilter();
	}

	/**
	 * setting filter for bulletins
	 * resets all other filter
	 */
	public static void setFilterMyBulletins() {
		resetFilter();
		myBulletins = true;
		buildFilter();

	}

	/**
	 * Put together all filter substrings and form request string for server
	 * if my bulletins checked then only one filter
	 */
	private static void buildFilter() {
		if (myBulletins) {
			if (!Contact.getUid().equals("")) {
				sFilter = "?uid=" + Contact.getUid();
			}
		} else {
			sFilter = "?";
			if (!sCategoriesFilter.equals("")) {
				sFilter += sCategoriesFilter;

			}
			if (!sCitiesFilter.equals("")) {
				if (!sFilter.endsWith("?")) {
					sFilter += "&";
				}
				sFilter += sCitiesFilter;

			}
			if (!sPriceFilterMax.equals("")) {
				if (!sFilter.endsWith("?")) {
					sFilter += "&";
				}
				sFilter += sPriceFilterMax;

			}
			if (!sPriceFilterMin.equals("")) {
				if (!sFilter.endsWith("?")) {
					sFilter += "&";
				}
				sFilter += sPriceFilterMin;

			}
			if (!sTagFilter.equals("")) {
				if (!sFilter.endsWith("?")) {
					sFilter += "&";
				}
				sFilter += sTagFilter;

			}if (!sStatusFilter.equals("")) {
				if (!sFilter.endsWith("?")) {
					sFilter += "&";
				}
				sFilter += sStatusFilter;
			}
		}

	}

	/**
	 * return all filter values to its default settings
	 */
	public static void resetFilter() {
		sFilter = "?";

		sCategoriesFilter = "";
		iCategoriesFilterId = -1;

		sCitiesFilter = "";
		iCitiesFilterId = -1;

		sPriceFilterMax = "";
		fPriceFilterMaxValue = -1;

		sPriceFilterMin = "";
		fPriceFilterMinValue = -1;

		sTagFilter = "";
		sTagFilterValue = "";

		sStatusFilter = "";
		iStatusFilterValue = -1;

		myBulletins = false;

	}

	/**
	 * Check is filter have setted
	 *
	 * @return is filter setted
	 */
	public static boolean isFilter() {
		if (sFilter.equals("?")) {
			return false;
		} else {
			return true;
		}
	}

	public static String getsFilter() {
		return sFilter;
	}

	public static int getCategoryFilterId() {
		return iCategoriesFilterId;
	}

	public static int getCityFilterId() {
		return iCitiesFilterId;
	}

	public static float getfPriceFilterMaxValue() {
		return fPriceFilterMaxValue;
	}

	public static float getfPriceFilterMinValue() {
		return fPriceFilterMinValue;

	}

	public static String getsTagFilterValue() {
		return sTagFilterValue;
	}

	public static boolean isMyBulletins() {
		return myBulletins;
	}
}
