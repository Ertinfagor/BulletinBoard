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
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bereznik on 19.08.2014.
 */
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

	private Bitmap bitmap = null;

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
			//spnCategories.setSelection(adapterCategories.getPosition(Categories.getName((bulletin.getCategories()))));
		}

		spnCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String categoriesString = "";
				if (!categoriesIds.contains(Categories.getId(adapterCategories.getItem(position)))) {
					categoriesIds.add(Categories.getId(adapterCategories.getItem(position)));
					for (Integer categorieId : categoriesIds) {
						categoriesString += ", ";
						categoriesString += Categories.getName(categorieId);
					}
					tvCategories.setText(categoriesString);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void post() {

		if (edTitle.getText().toString().trim().equals("")) {
			edTitle.setError("Укажите заголовок");
		} else if (edText.getText().toString().trim().equals("")) {
			edText.setError("Укажите детальную информацию");
		} else if (edPrice.getText().toString().trim().equals("")) {
			edPrice.setError("Укажите цену");
		} else {
			Bulletin newBulletin = new Bulletin();

			newBulletin.setTitle(edTitle.getText().toString());
			newBulletin.setText(edText.getText().toString());
			newBulletin.setCity_id(Integer.valueOf(Cities.getId((String) spnCity.getSelectedItem())));
			newBulletin.setCategories(categoriesIds);
			newBulletin.setContact_uid(Contact.getUid());
			newBulletin.setPrice(Float.valueOf(edPrice.getText().toString()));
			newBulletin.setState(cbState.isChecked());
			newBulletin.setImage(bitmap);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			try {
				Uri selectedimg = data.getData();
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
				ivImage.setImageBitmap(bitmap);
				ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
			} catch (Exception e) {
				Log.e(Constants.LOG_TAG, "Can`t get image from file: " + e.toString());
			}
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
				ret = true;
				break;
			case R.id.menuaddbulletinAddCity:
				ret = true;
				break;
			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}


}
