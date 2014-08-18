package com.senkatel.bereznikov.bulletinboard.cities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletins;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class CitiesActivity extends Activity {
	private ListView lvCities;
	private  ArrayAdapter<String> citiesAdapter;
	private MenuItem menuItemRefreshCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cities);




		lvCities = (ListView) findViewById(R.id.lvCities);

		lvCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	citiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, Cities.getCitiesList());

		lvCities.setAdapter(citiesAdapter);







		lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = lvCities.getCheckedItemPosition();
				Intent intent = new Intent(getApplicationContext(),BBGridActivity.class);
				if(pos != -1) {
					intent.putExtra("city", Cities.getId(citiesAdapter.getItem(position)));

				}

				startActivity(intent);

			}
		});

	}



	@Override
	protected void onResume() {
		super.onResume();
		citiesAdapter.notifyDataSetChanged();
		if (Bulletins.getCityFilterId()!=-1){
			lvCities.setItemChecked(citiesAdapter.getPosition(Cities.getName(Bulletins.getCityFilterId())),true);
		}
		try {
			UpdateCities updateNow = new UpdateCities();
			updateNow.execute();
		}catch (Exception e){
			Log.e(Constants.LOG_TAG, "Cannot start Cities update task: " + e.toString());
		}
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_filter, menu);
		menuItemRefreshCity = menu.findItem(R.id.menuFilterUpdate);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;

		switch (item.getItemId()) {
			case R.id.menuFilterUpdate:
				try {
					UpdateCities updateNow = new UpdateCities();
					updateNow.execute();
				}catch (Exception e){
					Log.e(Constants.LOG_TAG, "Cannot start Cities update task: " + e.toString());
				}
				ret = true;
				break;

			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}


	private class UpdateCities extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			menuItemRefreshCity.setActionView(R.layout.progressbar_layout);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Cities.update(Constants.URL);
				Log.v(Constants.LOG_TAG, "Refresh ");
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Can`t get Cities: " + e.toString());
				Toast.makeText(getApplicationContext(), getString(R.string.ErrorConnectToServer), Toast.LENGTH_LONG).show();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			citiesAdapter.notifyDataSetChanged();
			menuItemRefreshCity.setActionView(null);

		}
	}

}

