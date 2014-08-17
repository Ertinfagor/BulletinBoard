package com.senkatel.bereznikov.bulletinboard.categories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletins;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class CategoriesActivity extends Activity{
	private ListView lvCategories;
	ArrayAdapter<String> categoriesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);




		lvCategories = (ListView) findViewById(R.id.lvCategories);

		lvCategories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		categoriesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,Categories.getCategoriesList());

		lvCategories.setAdapter(categoriesAdapter);

		MainSync.initSyncingCategories(categoriesAdapter);
		MainSync.startSyncingCategories();




		lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = lvCategories.getCheckedItemPosition();
				Intent intent = new Intent(getApplicationContext(),BBGridActivity.class);
				if(pos != -1) {
					intent.putExtra("category", Categories.getId(categoriesAdapter.getItem(position)));
				}

				startActivity(intent);

			}
		});
	}



	@Override
	protected void onResume() {
		super.onResume();
		MainSync.startSyncingCategories();
		if (Bulletins.getCategoriesFilterId()!=-1){
			lvCategories.setItemChecked(categoriesAdapter.getPosition(Categories.getName(Bulletins.getCategoriesFilterId())),true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MainSync.stopSyncingCategories();
	}

	@Override
	protected void onStop() {
		super.onStop();
		MainSync.stopSyncingCategories();
	}

	public void onCategoriesClickOk(View view){
		int pos = lvCategories.getCheckedItemPosition();
		Intent intent = new Intent(this,BBGridActivity.class);
		if(pos != -1) {
			intent.putExtra("category", Categories.getId(categoriesAdapter.getItem(pos)));
		}

		startActivity(intent);




	}
	public void onCategoriesClickReset(View view){Bulletins.resetFilter();
	Intent intent = new Intent(getApplicationContext(),BBGridActivity.class);
	startActivity(intent);
	}

	public void onCategoriesClickUpdate(View view){
		categoriesAdapter.notifyDataSetChanged();
	}
}
