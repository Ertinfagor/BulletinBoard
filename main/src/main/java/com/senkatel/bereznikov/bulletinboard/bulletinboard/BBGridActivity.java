package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
	private BBArrayAdapter adapter;
	private Button btnCity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbgrid);

		MainSync mainSync = new MainSync(this);

		gvBB  = (GridView)findViewById(R.id.gridView);
		btnCity = (Button)findViewById(R.id.btnBBMainCities);
		adapter = new BBArrayAdapter(this,R.layout.grid_layout);


		MainSync.syncAll();

		gvBB.setAdapter(adapter);
		MainSync.initSyncingBulletinBoard(adapter);
		if(!Contact.init(this)){
			Intent intent = new Intent(this, ContactActivity.class);
			startActivity(intent);
		}

		gvBB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Intent intent = new Intent(getApplicationContext(), BBDetailedActivity.class);
				Bundle extras = new Bundle();
				extras.putParcelable("bulletin", Bulletins.get(position));

				intent.putExtras(extras);
				startActivity(intent);
			}
		});
	}



	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();

		if (getIntent().hasExtra("city")) {
			int id = getIntent().getIntExtra("city", -1);
			Bulletins.setFilterCity(id);
			btnCity.setText(Cities.getName(id));

			Log.v(Constants.LOG_TAG, "Filter city " + Bulletins.getFilter());
		} else if (getIntent().hasExtra("category")) {
			Bulletins.setFilterCategories(getIntent().getIntExtra("category", -1));
			Log.v(Constants.LOG_TAG, "Filter category " + Bulletins.getFilter());
		}
		MainSync.startSyncingBulletinBoard();
	}

	@Override
	protected void onPause() {
		super.onPause();


	}
	@Override
	protected void onStop() {
		super.onStop();
		MainSync.stopSyncingBulletinBoard();
	}

	public void onBBMainClick(View view){
		Intent intent;
		switch (view.getId()){
			case R.id.btnBBMainCategories:
				intent = new Intent(this, CategoriesActivity.class);
				startActivity(intent);
				break;
			case R.id.btnBBMainCities:
				intent = new Intent(this, CitiesActivity.class);
				startActivity(intent);

				break;
			case R.id.btnBBMainCreateBulletin:
				intent = new Intent(getApplicationContext(), BBDetailedActivity.class);
				startActivity(intent);
				break;
		}

	}


}
