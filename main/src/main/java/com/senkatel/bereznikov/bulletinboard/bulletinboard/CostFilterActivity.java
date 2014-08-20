package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.main.R;

/**
 * Created by Bereznik on 19.08.2014.
 */
public class CostFilterActivity extends Activity {
	private EditText edMax;
	private EditText edMin;
	private float max;
	private float min;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activiti_costfilter);
		edMax = (EditText)findViewById(R.id.edCostFilterMax);
		edMin = (EditText)findViewById(R.id.edCostFilterMin);


	}
public void onCostFilterOk(View view){
	max = Float.valueOf(edMax.getText().toString());
	min = Float.valueOf(edMin.getText().toString());

	if (max < min){max = min;}
	if (min!=-1) {
		Bulletins.setFilterCostMax(max);
	}
	if (max!=-1) {
		Bulletins.setFilterCostMin(min);
	}
}


}
