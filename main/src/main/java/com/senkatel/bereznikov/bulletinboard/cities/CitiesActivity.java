package com.senkatel.bereznikov.bulletinboard.cities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
	ArrayAdapter<String> citiesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cities);




		lvCities = (ListView) findViewById(R.id.lvCities);

		lvCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	citiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, Cities.getCitiesList());

		lvCities.setAdapter(citiesAdapter);

		MainSync.initSyncingCities(citiesAdapter);
		MainSync.startSyncingCities();




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
		MainSync.startSyncingCities();
		if (Bulletins.getCitiesFilterId()!=-1){
			int id = Bulletins.getCitiesFilterId();
			Log.v(Constants.LOG_TAG,"ID: " + id);
			String name =Cities.getName(id);
			Log.v(Constants.LOG_TAG,"Name: " + name);
			int pos = citiesAdapter.getPosition(name);
			Log.v(Constants.LOG_TAG,"Pos: " + pos);
			lvCities.setItemChecked(pos,true);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MainSync.stopSyncingCities();
	}

	@Override
	protected void onStop() {
		super.onStop();
		MainSync.stopSyncingCities();
	}

	public void onCitiesClickOk(View view){
		int pos = lvCities.getCheckedItemPosition();
		Intent intent = new Intent(this,BBGridActivity.class);
		if(pos != -1) {
			intent.putExtra("city",Cities.getId(citiesAdapter.getItem(pos)));
		}


		startActivity(intent);




	}
	public void onCitiesClickReset(View view){
		Bulletins.resetFilter();
		Intent intent = new Intent(getApplicationContext(),BBGridActivity.class);
		startActivity(intent);
	}

	public void onCitiesClickUpdate(View view){
		citiesAdapter.notifyDataSetChanged();
	}
}

