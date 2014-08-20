package com.senkatel.bereznikov.bulletinboard.util;

import android.os.AsyncTask;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletins;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;

/*Class that implements periodical syncing data*/
public class MainSync {






	public static void syncAll(){
		InitialSync initialSync = new InitialSync();
		initialSync.execute();
	}
	private static class InitialSync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Bulletins.getBulletins(Constants.URL);
			Categories.getCategories(Constants.URL);
			Cities.getCities(Constants.URL);
			return null;
		}
	}
}
