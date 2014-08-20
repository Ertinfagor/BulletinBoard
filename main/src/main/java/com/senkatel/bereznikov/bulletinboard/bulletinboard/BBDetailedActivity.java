package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;

import java.lang.ref.WeakReference;


public class BBDetailedActivity extends Activity {
	private ImageView ivImage;
	private TextView tvTitle;
	private TextView tvText;
	private TextView tvCity;
	private TextView tvCategories;
	private TextView tvContactName;
	private TextView tvContactPhone;
	private TextView tvContactEmail;
	private TextView tvDate;
	private TextView tvPrice;
	private TextView tvState;

	MenuItem meEdit;
	MenuItem meDelete;

	private Bulletin bulletin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbdetailed);


		ivImage = (ImageView) findViewById(R.id.ivBBGridActivityDetailed);

		tvTitle = (TextView) findViewById(R.id.edBBDetailedTitle);
		tvText = (TextView) findViewById(R.id.edBBDetailedText);
		tvCity = (TextView) findViewById(R.id.edBBDetailedCity);
		tvCategories = (TextView) findViewById(R.id.edBBDetailedCategories);
		tvContactName = (TextView) findViewById(R.id.edBBDetailedContactName);
		tvContactPhone = (TextView) findViewById(R.id.edBBDetailedContactPhone);
		tvContactEmail = (TextView) findViewById(R.id.edBBDetailedContactEmail);
		tvDate = (TextView) findViewById(R.id.edBBDetailedDate);
		tvPrice = (TextView) findViewById(R.id.edBBDetailedPrice);
		tvState = (TextView) findViewById(R.id.edBBDetailedState);


		if (getIntent().hasExtra("bulletin")) {
			Bundle extras = getIntent().getExtras();

			bulletin = extras.getParcelable("bulletin");

			BitmapWorkerTaskDetailed loadImage = new BitmapWorkerTaskDetailed(ivImage);
			loadImage.execute(bulletin.getId());

			tvTitle.setText(bulletin.getTitle());
			tvText.setText(bulletin.getText());
			tvCity.setText(Cities.getName(bulletin.getCity_id()));
			String categories = "";
			for (int i : bulletin.getCategories()) {
				categories += Categories.getName(i) + " ";
			}
			tvCategories.setText(categories);

			tvContactName.setText(bulletin.getContact().getName() + " " + bulletin.getContact().getLastName());
			tvContactPhone.setText(bulletin.getContact().getPhone());
			tvContactEmail.setText(bulletin.getContact().getEmail());

			tvDate.setText(bulletin.getDate().toString());
			tvPrice.setText(String.valueOf(bulletin.getPrice()));
			if (bulletin.isState()) {
				tvState.setText("Новый");
			} else {
				tvState.setText("Подержанный");
			}


		}
	}

	@Override
	protected void onResume() {
		super.onResume();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_detailedview, menu);
		meEdit = menu.findItem(R.id.menuDetailedViewEdit);
		meDelete = menu.findItem(R.id.menuDetailedViewDelete);


		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/*Buttons allow only if user is create item*/
		if (bulletin.getContact_uid().equals(Contact.getUid())) {
			meEdit.setVisible(true);
			meDelete.setVisible(true);
		} else {
			meEdit.setVisible(false);
			meDelete.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret;
		Intent intent;
		switch (item.getItemId()) {
			case R.id.menuDetailedViewEdit:
				intent = new Intent(this, AddBulletinActivity.class);
				intent.putExtra("bulletin", bulletin);
				startActivity(intent);
				ret = true;
				break;
			case R.id.menuDetailedViewDelete:
				Bulletins.deleteBulletin(bulletin);
				finish();
				ret = true;
				break;

			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}

	class BitmapWorkerTaskDetailed extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int id = 0;

		public BitmapWorkerTaskDetailed(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode ivImage in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			id = params[0];
			Log.v(Constants.LOG_TAG, "do in background started");
			return Images.loadImage(id, 100, 100);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					Log.v(Constants.LOG_TAG, "ivImage setting");
					imageView.setImageBitmap(bitmap);
				}
			}

		}

	}
}
