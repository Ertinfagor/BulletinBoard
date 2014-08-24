package com.senkatel.bereznikov.bulletinboard.categories;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletins;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Filter;

/**
 * Class CategoriesActivity
 * Implements ListView of Categories to filter
 * Load from Bulletins previously saved value of filter
 * Has force update task running in separate thread
 */
public class CategoriesActivity extends Activity{
	private ListView lvCategories;
	ArrayAdapter<String> categoriesAdapter;
	private MenuItem menuItemRefreshCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);




		lvCategories = (ListView) findViewById(R.id.lvCategories);

		lvCategories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		categoriesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,Categories.getListCategoriesNames());

		lvCategories.setAdapter(categoriesAdapter);







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
		if (Filter.getCategoryFilterId()!=-1){
			lvCategories.setItemChecked(categoriesAdapter.getPosition(Categories.getName(Filter.getCategoryFilterId())),true);
		}
		try {
			new ForceUpdate().execute();
		}catch (Exception e){
			Log.e(Constants.LOG_TAG, "Cannot start Categories getCategories task: " + e.toString());
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_filter, menu);
		menuItemRefreshCategory = menu.findItem(R.id.menuFilterUpdate);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;

		switch (item.getItemId()) {
			case R.id.menuFilterUpdate:
				try {
					new ForceUpdate().execute();
				}catch (Exception e){
					Log.e(Constants.LOG_TAG, "Cannot start Categories getCategories task: " + e.toString());
				}
				ret = true;
				break;

			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}

	private class ForceUpdate extends AsyncTask<Void, Void, Void> {
		private ProgressDialog Dialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Dialog = new ProgressDialog(CategoriesActivity.this);
			Dialog.setMessage("Загрузка");
			Dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Categories.getCategories(Constants.URL);
				Bulletins.getBulletins(Constants.URL);
				Cities.getCities(Constants.URL);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Can`t get categories: " + e.toString());

			}
			return null;
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			categoriesAdapter.notifyDataSetChanged();
			if(Dialog.isShowing())
			{
				Dialog.dismiss();
			}
		}
	}

}
