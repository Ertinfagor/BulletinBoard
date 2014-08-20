package com.senkatel.bereznikov.bulletinboard.cities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.main.R;


public class CityAddActivity extends Activity {
	EditText addItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcity);

		addItem = (EditText)findViewById(R.id.edAddCityActivity);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add_categry_city, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		switch (item.getItemId()) {

			case R.id.menuAddCategoryCityAdd:
				if(!addItem.getText().toString().trim().equals("")){
					Cities.postCity(addItem.getText().toString());
				}
				finish();
				ret = true;
				break;
			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}
	}

