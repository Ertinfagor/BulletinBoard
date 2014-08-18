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
import android.widget.Toast;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.categories.CategoriesActivity;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.cities.CitiesActivity;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.contacts.ContactActivity;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BBGridActivity extends Activity {
	private GridView gvBB;
	private BBArrayAdapter adapterBB;
	private MenuItem menuItemRefreshBB;
	private MenuItem menuItemCategoryBB;
	private MenuItem menuItemCityBB;
	private MenuItem menuItemResetFiltersBB;

	UpdateBulletins updateNow = new UpdateBulletins();

	ScheduledExecutorService updateBB  = Executors.newSingleThreadScheduledExecutor();
	private Runnable updateNotifierTask = new Runnable() {
		public void run() {
			adapterBB.notifyDataSetChanged();
		}
	};

	private Runnable periodicSyncTask = new Runnable() {
		@Override
		public void run() {
			try {
				Bulletins.getBulletins(Constants.URL);

				if (adapterBB != null) {
					runOnUiThread(updateNotifierTask);
				}
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "startSyncingBulletinBoard Error bulletins update from server: " + e.toString());
				Toast.makeText(getApplicationContext(),getString(R.string.ErrorConnectToServer), Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbgrid);
		getActionBar().setHomeButtonEnabled(true);


		gvBB = (GridView) findViewById(R.id.gridView);
		adapterBB = new BBArrayAdapter(this, R.layout.grid_layout);


		MainSync.syncAll();

		gvBB.setAdapter(adapterBB);


		if (!Contact.init(this)) {
			Intent intent = new Intent(this, ContactActivity.class);
			startActivity(intent);
		}


		updateBB.scheduleWithFixedDelay(periodicSyncTask, 0, 60, TimeUnit.SECONDS);

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
		adapterBB.notifyDataSetChanged();

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
			UpdateBulletins updateNow = new UpdateBulletins();
			updateNow.execute();
		}catch (Exception e){
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
		updateBB.shutdown();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_bbgrid, menu);
		menuItemRefreshBB = menu.findItem(R.id.menubbgridactivityUpdate);
		menuItemCategoryBB = menu.findItem(R.id.menubbgridactivityCategory);
		menuItemCityBB = menu.findItem(R.id.menubbgridactivityCity);
		menuItemResetFiltersBB = menu.findItem(R.id.menubbgridactivityResetFilters);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (Bulletins.getCityFilterId() != -1) {
			menuItemCityBB.setTitle(Cities.getName(Bulletins.getCityFilterId()));
		}
		if (Bulletins.getCategoryFilterId() != -1) {
			menuItemCategoryBB.setTitle(Categories.getName(Bulletins.getCategoryFilterId()));
		}

		if (Bulletins.getFilter()!="?"){
			menuItemResetFiltersBB.setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		Intent intent;
		switch (item.getItemId()) {
			case R.id.menubbgridactivityCategory:
				intent = new Intent(this, CategoriesActivity.class);
				startActivity(intent);
				ret = true;
				break;
			case R.id.menubbgridactivityCity:
				intent = new Intent(this, CitiesActivity.class);
				startActivity(intent);
				ret = true;
				break;
			case R.id.menubbgridactivityNewBulletin:
				intent = new Intent(getApplicationContext(), BBDetailedActivity.class);
				startActivity(intent);
				ret = true;
				break;
			case R.id.menubbgridactivityUpdate:
				try {
					UpdateBulletins updateNow = new UpdateBulletins();
					updateNow.execute();
				}catch (Exception e){
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				ret = true;
				break;
			case R.id.menubbgridactivityResetFilters:
				Bulletins.resetFilter();
				try {
					UpdateBulletins updateNow = new UpdateBulletins();
					updateNow.execute();
				}catch (Exception e){
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				menuItemResetFiltersBB.setVisible(false);
				menuItemCategoryBB.setTitle(getString(R.string.BBMainCategories));
				menuItemCityBB.setTitle(getString(R.string.BBMainCity));
				ret = true;
				break;
			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}


	private class UpdateBulletins extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			menuItemRefreshBB.setActionView(R.layout.progressbar_layout);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Bulletins.getBulletins(Constants.URL);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Can`t get bulletins: " + e.toString());
				Toast.makeText(getApplicationContext(),getString(R.string.ErrorConnectToServer), Toast.LENGTH_LONG).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			adapterBB.notifyDataSetChanged();
			menuItemRefreshBB.setActionView(null);
		}
	}
}


