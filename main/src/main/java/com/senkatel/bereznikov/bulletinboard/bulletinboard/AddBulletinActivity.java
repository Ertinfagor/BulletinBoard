package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.categories.CategoryAddActivity;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.cities.CityAddActivity;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddBulletinActivity extends Activity {

	private ImageView ivImage;
	private EditText edTitle;
	private EditText edText;
	private EditText edPrice;
	private Spinner spnCity;
	private Spinner spnCategories;
	private TextView tvCategories;
	private CheckBox cbState;

	List<Integer> categoriesIds = new ArrayList<Integer>();

	private ArrayAdapter<String> adapterCity;
	private ArrayAdapter<String> adapterCategories;

	private Bitmap bmpImage = null;

	private Bulletin bulletin = new Bulletin();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addbulletin);

		adapterCity = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, Cities.getCitiesList());
		adapterCategories = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, Categories.getCategoriesList());

		adapterCity.setDropDownViewResource(R.layout.spinner_layout);
		adapterCategories.setDropDownViewResource(R.layout.spinner_layout);

		ivImage = (ImageView) findViewById(R.id.imgAddBulletin);
		edTitle = (EditText) findViewById(R.id.edaddBulletinTitle);
		edText = (EditText) findViewById(R.id.edaddBulletinText);
		edPrice = (EditText) findViewById(R.id.edaddBulletinPrice);
		spnCity = (Spinner) findViewById(R.id.spnaddBulletinCity);
		spnCategories = (Spinner) findViewById(R.id.spnaddBulletinCategories);
		tvCategories = (TextView) findViewById(R.id.tvaddBulletinSelectedCategories);
		cbState = (CheckBox) findViewById(R.id.cbaddBulletinState);

		spnCity.setAdapter(adapterCity);
		spnCategories.setAdapter(adapterCategories);


		if (getIntent().hasExtra("bulletin")) {
			Bundle extras = getIntent().getExtras();

			bulletin = extras.getParcelable("bulletin");

			edTitle.setText(bulletin.getTitle());
			edText.setText(bulletin.getText());
			edPrice.setText(Float.toString(bulletin.getPrice()));
			cbState.setEnabled(bulletin.isState());
			spnCity.setSelection(adapterCity.getPosition(Cities.getName((bulletin.getCity_id()))));
			String categories = "";
			for (int id : bulletin.getCategories()) {
				categories += Categories.getName(id);
			}
			tvCategories.setText(categories);
		}

		spnCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String categoriesString = "";
				if (!categoriesIds.contains(Categories.getId(adapterCategories.getItem(position)))) {
					categoriesIds.add(Categories.getId(adapterCategories.getItem(position)));
					for (Integer categorieId : categoriesIds) {
						categoriesString += Categories.getName(categorieId);
						categoriesString += " ";
					}
					tvCategories.setText(categoriesString);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public void onAddBulletinClear(View view) {
		tvCategories.setText("");

	}

	private void post() {

		if (edTitle.getText().toString().trim().equals("")) {
			edTitle.setError(getString(R.string.needSetTitle));
		} else if (edText.getText().toString().trim().equals("")) {
			edText.setError(getString(R.string.needSetDetail));
		} else if (edPrice.getText().toString().trim().equals("")) {
			edPrice.setError(getString(R.string.needSetCost));
		} else {
			Bulletin newBulletin = new Bulletin();

			newBulletin.setTitle(edTitle.getText().toString());
			newBulletin.setText(edText.getText().toString());
			newBulletin.setCity_id(Cities.getId((String) spnCity.getSelectedItem()));
			newBulletin.setCategories(categoriesIds);
			newBulletin.setContact_uid(Contact.getUid());
			newBulletin.setPrice(Float.valueOf(edPrice.getText().toString()));
			newBulletin.setState(cbState.isChecked());
			newBulletin.setImage(bmpImage);

			if (bulletin.getId() != -1) {
				newBulletin.setId(bulletin.getId());
				Bulletins.putBulletin(newBulletin);

			} else {
				Bulletins.postBulletin(newBulletin);

			}

			Intent intent = new Intent(this, BBGridActivity.class);
			startActivity(intent);

		}
	}

	public void addBulletinOpenFile(View view) {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		if (resultCode == RESULT_OK) {
			Thread threadBitmapWork = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Uri uriImagePath = data.getData();
						bmpImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImagePath);
						Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmpImage, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIHT, false);
						ivImage.setImageBitmap(scaledBitmap);

					} catch (Exception e) {
						Log.e(Constants.LOG_TAG, "Can`t get image from file: " + e.toString());
					}
				}
			});
			threadBitmapWork.start();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_addbulletin, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		Intent intent;
		switch (item.getItemId()) {
			case R.id.menuaddbulletinAdd:
				post();
				intent = new Intent(getApplicationContext(), BBGridActivity.class);
				startActivity(intent);
				ret = true;
				break;
			case R.id.menuaddbulletinAddCategory:
				intent = new Intent(getApplicationContext(), CategoryAddActivity.class);
				startActivityForResult(intent, 0);
				ret = true;
				break;
			case R.id.menuaddbulletinAddCity:
				intent = new Intent(getApplicationContext(), CityAddActivity.class);
				startActivityForResult(intent, 0);
				ret = true;
				break;
			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}


}
