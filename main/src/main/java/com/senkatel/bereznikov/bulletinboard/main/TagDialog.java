package com.senkatel.bereznikov.bulletinboard.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Filter;

public  class TagDialog extends DialogFragment {

	private EditText edTag;
	private Button btnOk;

	public TagDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_tag, container);
		edTag = (EditText) view.findViewById(R.id.edDialogTag);
		btnOk = (Button) view.findViewById(R.id.btnDiaolgTagOk);

		getDialog().setTitle(getResources().getString(R.string.findtag));
		edTag.requestFocus();
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),BBGridActivity.class);
				if(!edTag.getText().toString().equals("")) {
					intent.putExtra("tag", edTag.getText().toString());

				}

				startActivity(intent);

			}
		});

		return view;

	}



}
