package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.main.R;


public class CostFilterActivity extends Activity {
	private EditText edMax;
	private EditText edMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiti_costfilter);
		edMax = (EditText) findViewById(R.id.edCostFilterMax);
		edMin = (EditText) findViewById(R.id.edCostFilterMin);


	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Bulletins.getCostFilterMaxValue() != -1) {
			edMax.setText(String.valueOf(Bulletins.getCostFilterMaxValue()));
		}
		if (Bulletins.getCostFilterMinValue() != -1) {
			edMin.setText(String.valueOf(Bulletins.getCostFilterMinValue()));
		}
	}

	public void onCostFilterOk(View view) {
		float max = -1;
		try {
			max = Float.valueOf(edMax.getText().toString());
		} catch (Exception e) {
			max = -1;
		}

		float min = -1;
		try {
			min = Float.valueOf(edMin.getText().toString());
		} catch (Exception e) {
			min = -1;
		}
		if (max < min) {
			max = min;
		}
		if (min != -1) {
			Bulletins.setFilterCostMin(min);
		}
		if (max != -1) {
			Bulletins.setFilterCostMax(max);

		}
		finish();
	}


}
