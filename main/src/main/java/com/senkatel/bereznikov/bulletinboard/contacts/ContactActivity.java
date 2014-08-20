package com.senkatel.bereznikov.bulletinboard.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.main.R;

/**
 * Created by Bereznik on 16.08.2014.
 */
public class ContactActivity extends Activity {
	private EditText name;
	private EditText lastname;
	private EditText phone;
	private EditText email;
	private Button btnOk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		name = (EditText)findViewById(R.id.edContactName);
		lastname = (EditText)findViewById(R.id.edContactLastName);
		phone = (EditText)findViewById(R.id.edContactPhone);
		email = (EditText)findViewById(R.id.edContactEmail);
		btnOk = (Button)findViewById(R.id.btnContactOk);

		name.setText(Contact.getName());
		lastname.setText(Contact.getLastName());
		phone.setText(Contact.getPhone());
		email.setText(Contact.getEmail());

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Contact.isExist()){
			btnOk.setText("Редактировать запись");
		}else {
			btnOk.setText("Создать запись");
		}
	}

	public void onContactClick(View view){
		if( name.getText().toString().trim().equals(""))
		{
			name.setError( "Укажите имя!" );
		}else if ( lastname.getText().toString().trim().equals(""))
		{
			lastname.setError( "Укажите фамилию!" );
		}else if ( phone.getText().toString().trim().equals(""))
		{
			phone.setError( "Укажите телефон!" );
		}else if ( email.getText().toString().trim().equals(""))
		{
			email.setError( "Укажите почту!" );
		}else {

			Contact.setName(name.getText().toString());
			Contact.setLastName(lastname.getText().toString());
			Contact.setPhone(phone.getText().toString());
			Contact.setEmail(email.getText().toString());
			Contact.save();
			Intent intent = new Intent(this, BBGridActivity.class);
			startActivity(intent);

		}

	}


}
