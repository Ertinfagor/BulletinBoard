package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Filter;

/**
 * Class PriceFilterActivity
 * Implements simple interface for filtering by Max/Min price
 */
public class PriceFilterActivity extends Activity {
	private EditText edMax;
	private EditText edMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiti_price_filter);
		edMax = (EditText) findViewById(R.id.edCostFilterMax);
		edMin = (EditText) findViewById(R.id.edCostFilterMin);


	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Filter.getfPriceFilterMaxValue() != -1) {
			edMax.setText(String.valueOf(Filter.getfPriceFilterMaxValue()));
		}
		if (Filter.getfPriceFilterMinValue() != -1) {
			edMin.setText(String.valueOf(Filter.getfPriceFilterMinValue()));
		}
	}

	public void onCostFilterOk(View view) {
		float max;
		float min;

		try {
			max = Float.valueOf(edMax.getText().toString());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG,"Incorrect max value " + e.toString());
			max = -1;
		}
		try {
			min = Float.valueOf(edMin.getText().toString());
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG,"Incorrect min value " + e.toString());
			min = -1;
		}

		Intent intent = new Intent(getApplicationContext(),BBGridActivity.class);

		if (min != -1) {
			intent.putExtra("pricemin", min);
			Log.v(Constants.LOG_TAG,"min: " + min);
		}
		if (max != -1) {
			intent.putExtra("pricemax", max);
		}
		startActivity(intent);
	}


}
