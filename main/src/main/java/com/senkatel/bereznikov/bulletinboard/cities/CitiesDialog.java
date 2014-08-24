package com.senkatel.bereznikov.bulletinboard.cities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.senkatel.bereznikov.bulletinboard.main.R;

public  class CitiesDialog extends DialogFragment {

	private EditText mEditText;
	private  ArrayAdapter<String> citiesAdapter;
	private ListView lvCities;

	public CitiesDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_cities, container);
		lvCities = (ListView) view.findViewById(R.id.lvCities);

		lvCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		citiesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, Cities.getCitiesList());

		lvCities.setAdapter(citiesAdapter);
		//mEditText = (EditText) view.findViewById(R.id.txt_your_name);
		getDialog().setTitle("Hello");


		return view;
	}



}