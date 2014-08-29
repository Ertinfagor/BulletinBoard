package com.senkatel.bereznikov.bulletinboard.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Filter;

/**
 * Class PriceDialog implements interface of price filter
 * if price filter set fill edittext on currently values
 * when set filter send extra to BBGridActivity
 */
@SuppressWarnings("ALL")
public class PriceDialog extends DialogFragment {


	private EditText edMax;
	private EditText edMin;
	private Button btnOk;

	public PriceDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_price_filter, container);
		edMax = (EditText) view.findViewById(R.id.edCostFilterMax);
		edMin = (EditText) view.findViewById(R.id.edCostFilterMin);
		btnOk = (Button) view.findViewById(R.id.btnCostFilterOk);

		getDialog().setTitle(getActivity().getString(R.string.price));
		edMin.requestFocus();
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				float max;
				float min;

				try {
					max = Float.valueOf(edMax.getText().toString());
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Incorrect max value " + e.toString());
					max = -1;
				}
				try {
					min = Float.valueOf(edMin.getText().toString());
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Incorrect min value " + e.toString());
					min = -1;
				}

				Intent intent = new Intent(getActivity(), BBGridActivity.class);


				intent.putExtra("pricemin", min);


				intent.putExtra("pricemax", max);

				startActivity(intent);

			}
		});

		return view;

	}


	@Override
	public void onResume() {
		super.onResume();
		if (Filter.getfPriceFilterMaxValue() != -1) {
			edMax.setText(String.valueOf(Filter.getfPriceFilterMaxValue()));
		}
		if (Filter.getfPriceFilterMinValue() != -1) {
			edMin.setText(String.valueOf(Filter.getfPriceFilterMinValue()));
		}
	}

}
