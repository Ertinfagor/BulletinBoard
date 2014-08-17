package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.categories.Categories;
import com.senkatel.bereznikov.bulletinboard.cities.Cities;
import com.senkatel.bereznikov.bulletinboard.contacts.Contact;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;

/**
 * Created by Bereznik on 17.08.2014.
 */
public class BBDetailedActivity extends Activity {
	private EditText title;
	private EditText text;
	private EditText city_id;
	private EditText contact_uid;
	private EditText price;
	private Button buttonOK;
	private Button buttonDelete;

	private boolean isEditable = false;

	private Bulletin bulletin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbdetailed);

		title = (EditText)findViewById(R.id.edBBDetailedTitle);
		text = (EditText)findViewById(R.id.edBBDetailedText);
		city_id = (EditText)findViewById(R.id.edBBDetailedCity);
		contact_uid = (EditText)findViewById(R.id.edBBDetailedContact);
		price = (EditText)findViewById(R.id.edBBDetailedPrice);
		buttonOK = (Button)findViewById(R.id.btnBBDetailedOk);
		buttonDelete = (Button)findViewById(R.id.btnBBDetailedDelete);

		buttonOK.setVisibility(View.INVISIBLE);
		buttonDelete.setVisibility(View.INVISIBLE);

		if (getIntent().hasExtra("bulletin")){
			Bundle extras = getIntent().getExtras();
			isEditable = true;
			bulletin = extras.getParcelable("bulletin");

			title.setText(bulletin.getTitle());
			text.setText(bulletin.getText());
			city_id.setText(Integer.toString(bulletin.getCity_id()));
			contact_uid.setText(bulletin.getContact_uid());
			price.setText(Float.toString(bulletin.getPrice()));

			title.setEnabled(false);
			text.setEnabled(false);
			city_id.setEnabled(false);
			contact_uid.setEnabled(false);
			price.setEnabled(false);

			if (bulletin.getContact_uid().equals(Contact.getUid())){
				buttonOK.setVisibility(View.VISIBLE);
				buttonOK.setText("Редактировать");
				buttonDelete.setVisibility(View.VISIBLE);
				buttonDelete.setText("Удалить");
				title.setEnabled(true);
				text.setEnabled(true);
				city_id.setEnabled(true);
				contact_uid.setEnabled(true);
				price.setEnabled(true);
			}
		}else {
			buttonOK.setVisibility(View.VISIBLE);
			buttonOK.setText("Создать");


		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().hasExtra("city")) {
			city_id.setText(Cities.getName(getIntent().getIntExtra("city", -1)));

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
		}else if ( city_id.getText().toString().trim().equals(""))
		{
			city_id.setError("Укажите город");
		}else if ( price.getText().toString().trim().equals(""))
		{
			price.setError("Укажите цену");
		}else {
			Bulletin newBulletin =new Bulletin();

			newBulletin.setTitle(title.getText().toString());
			newBulletin.setText(text.getText().toString());
			newBulletin.setCity_id(Integer.valueOf(city_id.getText().toString()));
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
}
