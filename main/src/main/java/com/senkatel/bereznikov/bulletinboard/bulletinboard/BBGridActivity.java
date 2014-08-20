package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.categories.CategoriesActivity;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.cities.CitiesActivity;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.contacts.ContactActivity;
import com.senkatel.bereznikov.bulletinboard.main.PreferencesActivity;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main Interface Class BBGridActivity GridView that show all loaded Bulletins
 * Run periodical update task by ScheduledExecutorService in separate thread
 * Has force update task running in separate thread
 * Change names of filter buttons
 */
public class BBGridActivity extends Activity {
	private GridView gvBB;
	private BBArrayAdapter bbArrayAdapter;
	private MenuItem miRefreshBB;
	private MenuItem miCategoryFilterBB;
	private MenuItem miCityFilterBB;
	private MenuItem miResetFiltersBB;

	ScheduledExecutorService sesPeriodicUpdate = Executors.newSingleThreadScheduledExecutor();
	private Runnable runUpdateNotifierTask = new Runnable() {
		public void run() {
			bbArrayAdapter.notifyDataSetChanged();
		}
	};

	private Runnable runPeriodicUpdateTask = new Runnable() {
		@Override
		public void run() {
			try {
				Bulletins.getBulletins(Constants.URL);
				Categories.getCategories(Constants.URL);
				Cities.getCities(Constants.URL);

				if (bbArrayAdapter != null) {
					runOnUiThread(runUpdateNotifierTask);
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "startSyncingBulletinBoard Error bulletins update from server: " + e.toString());

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbgrid);
		sesPeriodicUpdate.scheduleWithFixedDelay(runPeriodicUpdateTask, 0, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);
		getActionBar().setTitle("");

		Images.init();


		gvBB = (GridView) findViewById(R.id.gridView);
		bbArrayAdapter = new BBArrayAdapter(this, R.layout.grid_layout);


		//MainSync.syncAll();

		gvBB.setAdapter(bbArrayAdapter);


		if (!Contact.init(this)) {
			Intent intent = new Intent(this, ContactActivity.class);
			startActivity(intent);
		}


		gvBB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Intent intent = new Intent(getApplicationContext(), BBDetailedActivity.class);
				intent.putExtra("bulletin", Bulletins.get(position));
				startActivity(intent);
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		bbArrayAdapter.notifyDataSetChanged();

		try {

			if (getIntent().hasExtra("city")) {
				int id = getIntent().getIntExtra("city", -1);
				Bulletins.setFilterCity(id);
			}
			if (getIntent().hasExtra("category")) {
				Bulletins.setFilterCategories(getIntent().getIntExtra("category", -1));
			}
		} catch (Exception e) {

			Log.e(Constants.LOG_TAG, "GetIntent error: " + e.toString());
		}
		try {
			new ForceUpdate().execute();
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sesPeriodicUpdate.shutdown();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_bbgrid, menu);
		miRefreshBB = menu.findItem(R.id.menubbgridactivityUpdate);
		miCategoryFilterBB = menu.findItem(R.id.menubbgridactivityCategory);
		miCityFilterBB = menu.findItem(R.id.menubbgridactivityCity);
		miResetFiltersBB = menu.findItem(R.id.menubbgridactivityResetFilters);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (Bulletins.getCityFilterId() != -1) {
			miCityFilterBB.setTitle(Cities.getName(Bulletins.getCityFilterId()));
		}
		if (Bulletins.getCategoryFilterId() != -1) {
			miCategoryFilterBB.setTitle(Categories.getName(Bulletins.getCategoryFilterId()));
		}

		if (Bulletins.getsFilter() != "?") {
			miResetFiltersBB.setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean boolReturn;
		Intent intent;
		switch (item.getItemId()) {
			case R.id.menubbgridactivityCategory:
				intent = new Intent(this, CategoriesActivity.class);
				startActivity(intent);
				boolReturn = true;
				break;
			case R.id.menubbgridactivityCity:
				intent = new Intent(this, CitiesActivity.class);
				startActivity(intent);
				boolReturn = true;
				break;
			case R.id.menubbgridactivityNewBulletin:
				intent = new Intent(getApplicationContext(), AddBulletinActivity.class);
				startActivity(intent);
				boolReturn = true;
				break;
			case R.id.menubbgridactivityUpdate:
				try {
					new ForceUpdate().execute();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				boolReturn = true;
				break;
			case R.id.menubbgridactivityResetFilters:
				Bulletins.resetFilter();
				try {
					new ForceUpdate().execute();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				miResetFiltersBB.setVisible(false);
				miCategoryFilterBB.setTitle(getString(R.string.BBMainCategories));
				miCityFilterBB.setTitle(getString(R.string.BBMainCity));
				boolReturn = true;
				break;
			case R.id.menubbgridactivityCostFilter:
				intent = new Intent(getApplicationContext(), PriceFilterActivity.class);
				startActivity(intent);
				boolReturn = true;
				break;
			case R.id.menubbgridactivitPreferences:
				intent = new Intent(getApplicationContext(), PreferencesActivity.class);
				startActivity(intent);
				boolReturn = true;
				break;
			default:
				boolReturn = super.onOptionsItemSelected(item);

		}
		return boolReturn;
	}


	private class ForceUpdate extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Bulletins.getBulletins(Constants.URL);
				Categories.getCategories(Constants.URL);
				Cities.getCities(Constants.URL);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Can`t get bulletins: " + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			bbArrayAdapter.notifyDataSetChanged();

		}
	}
}


