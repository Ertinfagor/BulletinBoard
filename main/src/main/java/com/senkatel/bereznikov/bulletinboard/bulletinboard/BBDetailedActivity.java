package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.OkDismissDialog;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;

import java.lang.ref.WeakReference;

/**
 * Class BBDetailedActivity
 * Implements interface of Detailed view Bulletin
 * If userId is the same as Creator of viewed bulletin additional buttons Edit/Delete set visible
 * Implements Load image on separate thread
 */
@SuppressWarnings("ALL")
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

	MenuItem miEdit;
	MenuItem miDelete;

	private Bulletin bulletin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bb_detailed);


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
			loadImage.execute(bulletin.getIntBulletinId());

			tvTitle.setText(bulletin.getsTitle());
			tvText.setText(bulletin.getsText());
			tvCity.setText(Cities.getName(bulletin.getIntCity_id()));
			String categories = "";
			for (int i : bulletin.getListCategories()) {
				categories += Categories.getName(i) + " ";
			}
			tvCategories.setText(categories);

			tvContactName.setText(bulletin.getBcBulletinContact().getName() + " " + bulletin.getBcBulletinContact().getLastName());
			tvContactPhone.setText(bulletin.getBcBulletinContact().getPhone());
			tvContactEmail.setText(bulletin.getBcBulletinContact().getEmail());

			tvDate.setText(bulletin.getdBulletinDate().toString());
			tvPrice.setText(String.valueOf(bulletin.getfPrice()));
			if (bulletin.isbState()) {
				tvState.setText(getString(R.string.itemUsed));
			} else {
				tvState.setText(getString(R.string.itemNew));
			}

			Contact.init(getApplicationContext());


		}
	}

	@Override
	protected void onResume() {
		super.onResume();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu meActionBar) {
		getMenuInflater().inflate(R.menu.menu_detailed_view, meActionBar);
		miEdit = meActionBar.findItem(R.id.menuDetailedViewEdit);
		miDelete = meActionBar.findItem(R.id.menuDetailedViewDelete);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/*Buttons allow only if user is create item*/
		if (bulletin.getIntContact_uid().equals(Contact.getUid())) {
			miEdit.setVisible(true);
			miDelete.setVisible(true);
		} else {
			miEdit.setVisible(false);
			miDelete.setVisible(false);
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
				DialogFragment dig1 = new OkDismissDialog(bulletin,this);
				dig1.show(getFragmentManager(),"dig1");
				ret = true;
				break;

			default:
				ret = super.onOptionsItemSelected(item);

		}
		return ret;
	}

	/**
	 * Inner Class that load image to temp Image var
	 */
	class BitmapWorkerTaskDetailed extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> wrImageViewReference;
		private int bulletinId = 0;

		public BitmapWorkerTaskDetailed(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			wrImageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode ivImage in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			bulletinId = params[0];
			return Images.loadImage(bulletinId, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIHT);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (wrImageViewReference != null && bitmap != null) {
				final ImageView imageView = wrImageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}

		}

	}
}
