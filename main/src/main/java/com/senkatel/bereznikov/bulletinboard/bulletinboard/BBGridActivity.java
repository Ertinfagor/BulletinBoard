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
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;

public class BBGridActivity extends Activity {
	private GridView gvBB;
	private BBArrayAdapter adapterBB;
	private MenuItem menuItemRefreshBB;
	private MenuItem menuItemCategoryBB;
	private MenuItem menuItemCityBB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbgrid);
		getActionBar().setHomeButtonEnabled(true);

		MainSync mainSync = new MainSync(this);/*TODO Убрать*/

		gvBB = (GridView) findViewById(R.id.gridView);
		adapterBB = new BBArrayAdapter(this, R.layout.grid_layout);


		MainSync.syncAll();

		gvBB.setAdapter(adapterBB);


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


			UpdateBulletins updateBulletins = new UpdateBulletins();
			updateBulletins.execute();
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Update task start error: " + e.toString());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();


	}

	@Override
	protected void onStop() {
		super.onStop();
		/*TODO Reset all periodic tasks*/
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_bbgrid, menu);
		menuItemRefreshBB = menu.findItem(R.id.menubbgridactivityUpdate);
		menuItemCategoryBB = menu.findItem(R.id.menubbgridactivityCategory);
		menuItemCityBB = menu.findItem(R.id.menubbgridactivityCity);
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
				UpdateBulletins upd = new UpdateBulletins();
				upd.execute();

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
				Log.e(Constants.LOG_TAG, "Can`t get bulletins: " + e.getMessage());
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


