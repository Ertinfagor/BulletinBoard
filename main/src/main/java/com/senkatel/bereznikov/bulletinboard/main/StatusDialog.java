package com.senkatel.bereznikov.bulletinboard.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.util.Filter;

@SuppressWarnings("ALL")
/**
 * Class StatusDialog implements dialog interface to set status filter
 * on set send extra to BBGrid Activity
 */
public  class StatusDialog extends DialogFragment {

	private RadioButton rbnNew;
	private RadioButton rbnUsed;

	public StatusDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_status, container);
		rbnNew =(RadioButton)view.findViewById(R.id.rbnDialogStatusNew);
		rbnUsed =(RadioButton)view.findViewById(R.id.rbnDialogStatusUsed);

		getDialog().setTitle(getActivity().getString(R.string.StatusDialogStatus));


		rbnNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),BBGridActivity.class);
					intent.putExtra("status", "new");
				startActivity(intent);
			}
		});
		rbnUsed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BBGridActivity.class);
				intent.putExtra("status", "used");
				startActivity(intent);
			}
		});

		return view;

	}
	@Override
	public void onResume() {
		super.onResume();
		if (Filter.getiStatusFilterValue() == 1){
			rbnNew.setChecked(true);
			rbnUsed.setChecked(false);
		}else if(Filter.getiStatusFilterValue() == 0) {
			rbnNew.setChecked(false);
			rbnUsed.setChecked(true);
		}
	}



}
