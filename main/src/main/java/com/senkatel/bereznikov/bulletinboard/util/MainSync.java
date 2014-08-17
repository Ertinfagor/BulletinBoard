package com.senkatel.bereznikov.bulletinboard.util;

import android.app.Activity;
import android.content.Context;
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
	private static Activity context;
	private static ScheduledExecutorService updateBB  = Executors.newSingleThreadScheduledExecutor();
	private static ScheduledExecutorService updateCategories  = Executors.newSingleThreadScheduledExecutor();
	private static ScheduledExecutorService updateCities  = Executors.newSingleThreadScheduledExecutor();

	private static ScheduledFuture updateBBTask;
	private static ScheduledFuture updateCategoriesTask;
	private static ScheduledFuture updateCitiesTask;

	private static  BBArrayAdapter adapterBB;
	private static  ArrayAdapter adapterCities;
	private static  ArrayAdapter adapterCategories;

	public MainSync(Context context) {
		MainSync.context = (Activity) context;

	}

	public static void initSyncingBulletinBoard(BBArrayAdapter adapter){
		adapterBB = adapter;
	}
	public static void startSyncingBulletinBoard() {
		Log.v(Constants.LOG_TAG, "UpdateBB Task Started");
			updateBBTask = updateBB.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						Bulletins.update(Constants.URL);

						Log.v(Constants.LOG_TAG, "BulletinUpdate");
						if (adapterBB != null) {
							context.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapterBB.notifyDataSetChanged();
									Log.v(Constants.LOG_TAG, "BulletinAdapterUpdate");
								}
							});

						}
					} catch (Exception e) {
						Log.e(Constants.LOG_TAG, "startSyncingBulletinBoard Error bulletins update from server: " + e.toString());
					}
				}
			}, 0, 10, TimeUnit.SECONDS);


	}

	public static void stopSyncingBulletinBoard() {
		updateBBTask.cancel(true);
		Log.v(Constants.LOG_TAG, "UpdateBB Task Stoped");
	}

	public static void initSyncingCategories(ArrayAdapter adapter){
		adapterCategories = adapter;
	}

	public static void startSyncingCategories() {
		Log.v(Constants.LOG_TAG, "UpdateCategories Task Started");
		updateCategoriesTask = updateCategories.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						Categories.update(Constants.URL);
						if (adapterCategories != null) {
							context.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapterCategories.notifyDataSetChanged();
									Log.v(Constants.LOG_TAG, "CategoriesAdapterUpdate");
								}
							});

						}
					} catch (Exception e) {
						Log.e(Constants.LOG_TAG, "startSyncingCategories Error categories update from server: " + e.toString());
					}
				}
			}, 0, 10, TimeUnit.SECONDS);


	}

	public static void stopSyncingCategories() {
		updateCategoriesTask.cancel(true);
		Log.v(Constants.LOG_TAG, "UpdateCategories Task Stoped");
	}

	public static void initSyncingCities(ArrayAdapter adapter){
		adapterCities = adapter;
	}

	public static void startSyncingCities() {
		Log.v(Constants.LOG_TAG, "UpdateCities Task Started");
		updateCitiesTask = updateCities.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					Cities.update(Constants.URL);
					if (adapterCities != null) {
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapterCities.notifyDataSetChanged();
								Log.v(Constants.LOG_TAG, "CitiesAdapterUpdate");
							}
						});

					}
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "startSyncingCities Error categories update from server: " + e.toString());
				}
			}
		}, 0, 10, TimeUnit.SECONDS);


	}

	public static void stopSyncingCities() {
		updateCitiesTask.cancel(true);
		Log.v(Constants.LOG_TAG, "UpdateCities Task Stoped");
	}
}
