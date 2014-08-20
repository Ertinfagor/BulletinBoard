package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;

import java.lang.ref.WeakReference;

/**
 * Created by Bereznik on 17.08.2014.
 */
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


	private Bitmap bitmap= null;





	private Bulletin bulletin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbdetailed);





		ivImage =(ImageView)findViewById(R.id.ivBBGridActivityDetailed);

		tvTitle = (TextView)findViewById(R.id.edBBDetailedTitle);
		tvText = (TextView)findViewById(R.id.edBBDetailedText);
		tvCity = (TextView)findViewById(R.id.edBBDetailedCity);
		tvCategories = (TextView)findViewById(R.id.edBBDetailedCategories);
		tvContactName = (TextView)findViewById(R.id.edBBDetailedContactName);
		tvContactPhone = (TextView)findViewById(R.id.edBBDetailedContactPhone);
		tvContactEmail = (TextView)findViewById(R.id.edBBDetailedContactEmail);
		tvDate = (TextView)findViewById(R.id.edBBDetailedDate);
		tvPrice = (TextView)findViewById(R.id.edBBDetailedPrice);
		tvState = (TextView)findViewById(R.id.edBBDetailedState);






		if (getIntent().hasExtra("bulletin")){
			Bundle extras = getIntent().getExtras();

			bulletin = extras.getParcelable("bulletin");

			BitmapWorkerTaskDetailed loadImage = new BitmapWorkerTaskDetailed(ivImage);
			loadImage.execute(bulletin.getId());

			tvTitle.setText(bulletin.getTitle());
			tvText.setText(bulletin.getText());
			tvCity.setText(Cities.getName(bulletin.getCity_id()));
			String categories = "";
			for (int i : bulletin.getCategories()){
				categories += Categories.getName(i)+ " ";
			}
			tvCategories.setText(categories);

			tvContactName.setText(bulletin.getContact().getName() + " " + bulletin.getContact().getLastName() );
			tvContactPhone.setText(bulletin.getContact().getPhone());
			tvContactEmail.setText(bulletin.getContact().getEmail());

			tvDate.setText(bulletin.getDate().toString());
			tvPrice.setText(String.valueOf(bulletin.getPrice()));
			if (bulletin.isState()){
				tvState.setText("Новый");
			}else {
				tvState.setText("Подержанный");
			}


			if (bulletin.getContact_uid().equals(Contact.getUid())){



			}
		}else {



		}
	}

	@Override
	protected void onResume() {
		super.onResume();

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
			Log.v(Constants.LOG_TAG,"do in background started");
			return Images.loadImage(id,100,100);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					Log.v(Constants.LOG_TAG,"ivImage setting");
					imageView.setImageBitmap(bitmap);
				}
			}

		}

	}
}
