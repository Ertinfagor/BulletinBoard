package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import com.senkatel.bereznikov.bulletinboard.categories.CategoriesDialog;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.cities.CitiesDialog;
import com.senkatel.bereznikov.bulletinboard.main.*;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Filter;
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
@SuppressWarnings("ALL")
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
		setContentView(R.layout.activity_bb_grid);
		sesPeriodicUpdate.scheduleWithFixedDelay(runPeriodicUpdateTask, 0, Constants.UPDATE_INTERVAL, TimeUnit.SECONDS);
		getActionBar().setTitle("");

		Images.init();


		gvBB = (GridView) findViewById(R.id.gridView);
		bbArrayAdapter = new BBArrayAdapter(this, R.layout.grid_layout);


		gvBB.setAdapter(bbArrayAdapter);


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
	/**
	 * recievs extras from filter dialogs and set filters
	 */
	protected void onResume() {
		super.onResume();

		try {

			if (getIntent().hasExtra("city")) {
				Filter.setFilterCity(getIntent().getIntExtra("city", -1));
			}
			if (getIntent().hasExtra("category")) {
				Filter.setFilterCategories(getIntent().getIntExtra("category", -1));
			}
			if (getIntent().hasExtra("tag")) {
				Filter.setFilterTag(getIntent().getStringExtra("tag"));
			}
			if (getIntent().hasExtra("pricemin")) {
				Filter.setFilterPriceMin(getIntent().getFloatExtra("pricemin", -1));
			}
			if (getIntent().hasExtra("pricemax")) {
				Filter.setFilterPriceMax(getIntent().getFloatExtra("pricemax", -1));
			}
			if (getIntent().hasExtra("status")) {
				Filter.setFilterStatus(getIntent().getStringExtra("status"));
			}
		} catch (Exception e) {

			Log.e(Constants.LOG_TAG, "GetIntent error: " + e.toString());
		}
		try {
			new ForceUpdate().execute();
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
		}
		bbArrayAdapter.notifyDataSetChanged();
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
		getMenuInflater().inflate(R.menu.menu_bb_grid, menu);
		miRefreshBB = menu.findItem(R.id.menubbgridactivityUpdate);
		miCategoryFilterBB = menu.findItem(R.id.menubbgridactivityCategory);
		miCityFilterBB = menu.findItem(R.id.menubbgridactivityCity);
		miResetFiltersBB = menu.findItem(R.id.menubbgridactivityResetFilters);

		return true;
	}

	/**
	 * if filter for category or city set then change apropriate button text to filter value
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (Filter.getCityFilterId() != -1) {
			miCityFilterBB.setTitle(Cities.getName(Filter.getCityFilterId()));
		}
		if (Filter.getCategoryFilterId() != -1) {
			miCategoryFilterBB.setTitle(Categories.getName(Filter.getCategoryFilterId()));
		}

		if (Filter.isFilter()) {
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
				FragmentTransaction ftCategories = getFragmentManager().beginTransaction();
				Fragment fCategoriesPrev = getFragmentManager().findFragmentByTag("category");
				if (fCategoriesPrev != null) {
					ftCategories.remove(fCategoriesPrev);
				}
				ftCategories.addToBackStack(null);
				CategoriesDialog fNewCategory = new CategoriesDialog();
				fNewCategory.show(ftCategories, "category");
				boolReturn = true;
				break;
			case R.id.menubbgridactivityCity:
				FragmentTransaction ftCities = getFragmentManager().beginTransaction();
				Fragment fCitiesPrev = getFragmentManager().findFragmentByTag("city");
				if (fCitiesPrev != null) {
					ftCities.remove(fCitiesPrev);
				}
				ftCities.addToBackStack(null);
				CitiesDialog fCities = new CitiesDialog();
				fCities.show(ftCities, "city");
				boolReturn = true;
				break;
			case R.id.menubbgridactivityNewBulletin:
				intent = new Intent(getApplicationContext(), AddBulletinActivity.class);
				startActivity(intent);
				boolReturn = true;
				break;
			case R.id.menubbgridactivityMyBulletin:
				Filter.setFilterMyBulletins();
				miCategoryFilterBB.setTitle(getString(R.string.BBMainCategories));
				miCityFilterBB.setTitle(getString(R.string.BBMainCity));
				try {
					new ForceUpdate().execute();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				boolReturn = true;
				break;
			case R.id.menubbgridactivityTag:
				FragmentTransaction ftTag = getFragmentManager().beginTransaction();
				Fragment fTagPrev = getFragmentManager().findFragmentByTag("tag");
				if (fTagPrev != null) {
					ftTag.remove(fTagPrev);
				}
				ftTag.addToBackStack(null);
				TagDialog fTagNew = new TagDialog();
				fTagNew.show(ftTag, "tag");
				boolReturn = true;
				break;
			case R.id.menubbgridactivityStatusFilter:
				FragmentTransaction ftStatus = getFragmentManager().beginTransaction();
				Fragment fStatusPrev = getFragmentManager().findFragmentByTag("status");
				if (fStatusPrev != null) {
					ftStatus.remove(fStatusPrev);
				}
				ftStatus.addToBackStack(null);
				StatusDialog fStatusNew = new StatusDialog();
				fStatusNew.show(ftStatus, "status");
				boolReturn = true;
				break;
			case R.id.menubbgridactivityUpdate:
				try {
					new ForceUpdate().execute();
					Images.reloadCache();
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				boolReturn = true;
				break;
			case R.id.menubbgridactivityResetFilters:
				Filter.resetFilter();
				try {
					new ForceUpdate().execute();
					intent = new Intent(getApplicationContext(), BBGridActivity.class);
					startActivity(intent);
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Cannot start Bulletin update task: " + e.toString());
				}
				miResetFiltersBB.setVisible(false);
				miCategoryFilterBB.setTitle(getString(R.string.BBMainCategories));
				miCityFilterBB.setTitle(getString(R.string.BBMainCity));
				boolReturn = true;
				break;
			case R.id.menubbgridactivityCostFilter:
				FragmentTransaction ftPrice = getFragmentManager().beginTransaction();
				Fragment fPricePrev = getFragmentManager().findFragmentByTag("price");
				if (fPricePrev != null) {
					ftPrice.remove(fPricePrev);
				}
				ftPrice.addToBackStack(null);
				PriceDialog fPrice = new PriceDialog();
				fPrice.show(ftPrice, "price");
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

	/**
	 * Forces to load JSON objects from server
	 */
	private class ForceUpdate extends AsyncTask<Void, Void, Void> {
		private ProgressDialog Dialog;

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Dialog = new ProgressDialog(BBGridActivity.this);
			Dialog.setMessage("Загрузка");
			Dialog.show();


		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Bulletins.getBulletins(Constants.URL);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bbArrayAdapter.notifyDataSetChanged();
					}
				});
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
			if (Dialog.isShowing()) {
				Dialog.dismiss();
			}

		}
	}

}


