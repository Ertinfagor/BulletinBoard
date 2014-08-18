package com.senkatel.bereznikov.bulletinboard.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBArrayAdapter;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletins;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
			Categories.update(Constants.URL);
			Cities.update(Constants.URL);
			return null;
		}
	}
}
