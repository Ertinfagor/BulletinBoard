package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.senkatel.bereznikov.bulletinboard.contacts.ContactActivity;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;

import java.util.ArrayList;
import java.util.List;


/**
 * Class AddBulletinActivity
 * Implements interface of Add/Edit Bulletin
 * Implements ActionBar
 * Implements Load image on separate thread
 * Implements Get image from Storage and put to temp Images var
 */
@SuppressWarnings("ALL")
public class AddBulletinActivity extends Activity {

	private ImageView ivImage;
	private EditText edTitle;
	private EditText edText;
	private EditText edPrice;
	private Spinner spnCity;
	private Spinner spnCategories;
	private TextView tvCategories;
	private CheckBox cbState;

	private boolean newSpnCategories = true; //used for obtain is first use of spiner ocured (if first then no changes to categories id)

	List<Integer> categoriesIds = new ArrayList<Integer>();

	private ArrayAdapter<String> adapterCity;
	private ArrayAdapter<String> adapterCategories;

	private Bulletin bulletin = new Bulletin();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_bulletin);

		adapterCity = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, Cities.getCitiesList());
		adapterCategories = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, Categories.getListCategoriesNames());

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

		if (!Contact.init(this)) {
			Intent intent = new Intent(this, ContactActivity.class);
			startActivity(intent);
		}


		if (getIntent().hasExtra("bulletin")) {
			Bundle extras = getIntent().getExtras();

			bulletin = extras.getParcelable("bulletin");

			edTitle.setText(bulletin.getsTitle());
			edText.setText(bulletin.getsText());
			edPrice.setText(Float.toString(bulletin.getfPrice()));
			cbState.setEnabled(bulletin.isbState());
			spnCity.setSelection(adapterCity.getPosition(Cities.getName((bulletin.getIntCity_id()))));
			String categories = "";
			for (int id : bulletin.getListCategories()) {
				categories += Categories.getName(id);
				categories += " ";
				categoriesIds.add(id);
			}
			tvCategories.setText(categories);
		}

		spnCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				String categoriesString = tvCategories.getText().toString();
				if (!newSpnCategories) {
					if (!categoriesIds.contains(Categories.getId(adapterCategories.getItem(position)))) {
						categoriesIds.add(Categories.getId(adapterCategories.getItem(position)));
						categoriesString += Categories.getName(Categories.getId(adapterCategories.getItem(position)));
						categoriesString += " ";

						tvCategories.setText(categoriesString);
					}
				}
				newSpnCategories = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public void onAddBulletinClear(View view) {
		tvCategories.setText("");
		categoriesIds.clear();

	}

	private void post() {
		boolean mayPost = true;
		if (edTitle.getText().toString().trim().equals("")) {
			mayPost = false;
			edTitle.setError(getString(R.string.needSetTitle));
			Toast.makeText(getApplicationContext(),"Укажите заголовок",Toast.LENGTH_LONG).show();
		} else if (edText.getText().toString().trim().equals("")) {
			mayPost = false;
			edText.setError(getString(R.string.needSetDetail));
			Toast.makeText(getApplicationContext(),"Укажите детальное описание",Toast.LENGTH_LONG).show();

		} else if (edPrice.getText().toString().trim().equals("")) {
			Toast.makeText(getApplicationContext(),"Укажите цену",Toast.LENGTH_LONG).show();

			mayPost = false;
			edPrice.setError(getString(R.string.needSetCost));
		} else {
			Bulletin newBulletin = new Bulletin();

			newBulletin.setsTitle(edTitle.getText().toString());
			newBulletin.setsText(edText.getText().toString());
			newBulletin.setIntCity_id(Cities.getId((String) spnCity.getSelectedItem()));
			newBulletin.setListCategories(categoriesIds);
			newBulletin.setIntContact_uid(Contact.getUid());
			newBulletin.setfPrice(Float.valueOf(edPrice.getText().toString()));
			newBulletin.setbState(cbState.isChecked());
			if (mayPost) {
				try {
					boolean bResult = false;
					if (bulletin.getIntBulletinId() != -1) {
						newBulletin.setIntBulletinId(bulletin.getIntBulletinId());
						bResult = Bulletins.putBulletin(newBulletin);

					} else {
						bResult = Bulletins.postBulletin(newBulletin);

					}

					if (bResult) {
						Intent intent = new Intent(this, BBGridActivity.class);
						startActivity(intent);
					}else {
						Toast.makeText(getApplicationContext(),"Ошибка. Объявление не было загружено",Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					Log.e(Constants.LOG_TAG, "Can`t Load Category: " + e.toString());
					Toast.makeText(getApplicationContext(),"Ошибка. Объявление не было загружено",Toast.LENGTH_LONG).show();
				}

			}
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
						Images.setTempByteArrayBitmap(uriImagePath, getApplicationContext());
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ivImage.setImageBitmap(Images.getTempBitmap());
							}
						});


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
		getMenuInflater().inflate(R.menu.menu_add_bulletin, menu);
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
