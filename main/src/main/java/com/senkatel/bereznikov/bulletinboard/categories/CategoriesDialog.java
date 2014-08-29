package com.senkatel.bereznikov.bulletinboard.categories;

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
 * Class implements dialog fragment to pick category for filter
 * if category filter set check radiobutton on currently set category
 * when picked category send extra to BBGridActivity
 */
public  class CategoriesDialog extends DialogFragment {


	private  ArrayAdapter<String> categoriesAdapter;
	private ListView lvCategories;

	public CategoriesDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_categories, container);
		lvCategories = (ListView) view.findViewById(R.id.lvCategories);

		lvCategories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		categoriesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_single_choice,Categories.getListCategoriesNames());

		lvCategories.setAdapter(categoriesAdapter);

		getDialog().setTitle(getActivity().getString(R.string.CategoryTitle));





		lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = lvCategories.getCheckedItemPosition();
				Intent intent = new Intent(getActivity(),BBGridActivity.class);
				if(pos != -1) {
					intent.putExtra("category", Categories.getId(categoriesAdapter.getItem(position)));
				}

				startActivity(intent);

			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Filter.getCategoryFilterId()!=-1){
			lvCategories.setItemChecked(categoriesAdapter.getPosition(Categories.getName(Filter.getCategoryFilterId())),true);
		}

	}
}