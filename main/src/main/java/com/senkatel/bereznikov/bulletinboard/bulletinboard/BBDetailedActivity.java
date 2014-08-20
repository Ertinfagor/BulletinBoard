package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;
import com.senkatel.bereznikov.bulletinboard.util.ParseJson;

import java.lang.ref.WeakReference;

/**
 * Created by Bereznik on 17.08.2014.
 */
public class BBDetailedActivity extends Activity {
	private ImageView image;
	private EditText title;
	private EditText text;
	private EditText contact_uid;
	private EditText price;
	private Spinner spnCity;

	private Bitmap bitmap= null;

	private ArrayAdapter<String>citiesAdapter;

	private boolean isEditable = false;

	private Bulletin bulletin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbdetailed);





		citiesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout,Cities.getCitiesList());
		citiesAdapter.setDropDownViewResource( R.layout.spinner_layout);

		title = (EditText)findViewById(R.id.edBBDetailedTitle);
		text = (EditText)findViewById(R.id.edBBDetailedText);
		contact_uid = (EditText)findViewById(R.id.edBBDetailedContact);
		price = (EditText)findViewById(R.id.edBBDetailedPrice);
		spnCity = (Spinner)findViewById(R.id.spnBBDetailedCity);
		image =(ImageView)findViewById(R.id.ivBBGridActivityDetailed);

		//sync tesa = new sync();
		//tesa.execute();

		spnCity.setAdapter(citiesAdapter);
		spnCity.setPrompt("Title");
		spnCity.setSelection(0);

		if (getIntent().hasExtra("bulletin")){
			Bundle extras = getIntent().getExtras();
			isEditable = true;
			bulletin = extras.getParcelable("bulletin");
			Log.v(Constants.LOG_TAG,"Image loading started");
			BitmapWorkerTaskDetailed loadImage = new BitmapWorkerTaskDetailed(image);
			loadImage.execute(bulletin.getId());

			title.setText(bulletin.getTitle());
			text.setText(bulletin.getText());
			contact_uid.setText(bulletin.getContact_uid());
			spnCity.setSelection(citiesAdapter.getPosition(Cities.getName((bulletin.getCity_id()))));
			price.setText(Float.toString(bulletin.getPrice()));

			title.setEnabled(false);
			text.setEnabled(false);
			spnCity.setEnabled(false);
			contact_uid.setEnabled(false);
			price.setEnabled(false);

			if (bulletin.getContact_uid().equals(Contact.getUid())){

				title.setEnabled(true);
				text.setEnabled(true);
				spnCity.setEnabled(true);
				contact_uid.setEnabled(true);
				price.setEnabled(true);
			}
		}else {



		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().hasExtra("city")) {
			spnCity.setSelection(citiesAdapter.getPosition(Cities.getName(getIntent().getIntExtra("city", -1))));


			Log.v(Constants.LOG_TAG, "Filter city " + Bulletins.getFilter());
		}/* else if (getIntent().hasExtra("category")) {
			getIntent().getIntExtra("category", -1));
			Log.v(Constants.LOG_TAG, "Filter category " + Bulletins.getFilter());
		}*/
	}

	public void onBBDetailedOk(View view){

		if( title.getText().toString().trim().equals(""))
		{
			title.setError( "Укажите заголовок" );
		}else if ( text.getText().toString().trim().equals(""))
		{
			text.setError( "Укажите детальную информацию" );
		}else if ( price.getText().toString().trim().equals(""))
		{
			price.setError("Укажите цену");
		}else {
			Bulletin newBulletin =new Bulletin();

			newBulletin.setTitle(title.getText().toString());
			newBulletin.setText(text.getText().toString());
			newBulletin.setCity_id(Integer.valueOf(Cities.getId((String) spnCity.getSelectedItem())));
			newBulletin.setContact_uid(Contact.getUid());
			newBulletin.setPrice(Float.valueOf(price.getText().toString()));

			if (isEditable){
				newBulletin.setId(bulletin.getId());
					Bulletins.putBulletin(newBulletin);

			}else {
				Bulletins.postBulletin(newBulletin);

			}

			Intent intent = new Intent(this, BBGridActivity.class);
			startActivity(intent);

		}
	}
	public void onBBDetailedDelete(View view){

		Bulletins.deleteBulletin(bulletin);
		Intent intent = new Intent(this, BBGridActivity.class);
		startActivity(intent);
	}

	class BitmapWorkerTaskDetailed extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int id = 0;

		public BitmapWorkerTaskDetailed(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
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
					Log.v(Constants.LOG_TAG,"image setting");
					imageView.setImageBitmap(bitmap);
				}
			}

		}

	}
}
