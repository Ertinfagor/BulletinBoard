package com.senkatel.bereznikov.bulletinboard.cities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Filter;

/**
 * Class implements dialog fragment to pick city for filter
 * if city filter set check radiobutton on currently set city
 * when picked city send extra to BBGridActivity
 */
public class CitiesDialog extends DialogFragment {

	private ArrayAdapter<String> citiesAdapter;
	private ListView lvCities;

	public CitiesDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dilaog_cities, container);
		lvCities = (ListView) view.findViewById(R.id.lvCities);

		lvCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		citiesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, Cities.getCitiesList());

		lvCities.setAdapter(citiesAdapter);
		getDialog().setTitle(getActivity().getString(R.string.CityTitle));

		lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = lvCities.getCheckedItemPosition();
				Intent intent = new Intent(getActivity(), BBGridActivity.class);
				if (pos != -1) {
					intent.putExtra("city", Cities.getId(citiesAdapter.getItem(position)));

				}

				startActivity(intent);

			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		citiesAdapter.notifyDataSetChanged();
		if (Filter.getCityFilterId() != -1) {
			lvCities.setItemChecked(citiesAdapter.getPosition(Cities.getName(Filter.getCityFilterId())), true);
		}
	}
}