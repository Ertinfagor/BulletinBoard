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
 * Class ContactActivity
 * Implements Interface of ADD/Edit contacts
 */
public class ContactActivity extends Activity {
	private EditText etName;
	private EditText etLastname;
	private EditText etPhone;
	private EditText etEmail;
	private Button btnOk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		etName = (EditText)findViewById(R.id.edContactName);
		etLastname = (EditText)findViewById(R.id.edContactLastName);
		etPhone = (EditText)findViewById(R.id.edContactPhone);
		etEmail = (EditText)findViewById(R.id.edContactEmail);
		btnOk = (Button)findViewById(R.id.btnContactOk);

		etName.setText(Contact.getName());
		etLastname.setText(Contact.getLastName());
		etPhone.setText(Contact.getPhone());
		etEmail.setText(Contact.getEmail());

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
		if( etName.getText().toString().trim().equals(""))
		{
			etName.setError( "Укажите имя!" );
		}else if ( etLastname.getText().toString().trim().equals(""))
		{
			etLastname.setError( "Укажите фамилию!" );
		}else if ( etPhone.getText().toString().trim().equals(""))
		{
			etPhone.setError( "Укажите телефон!" );
		}else if ( etEmail.getText().toString().trim().equals(""))
		{
			etEmail.setError( "Укажите почту!" );
		}else {

			Contact.setName(etName.getText().toString());
			Contact.setLastName(etLastname.getText().toString());
			Contact.setPhone(etPhone.getText().toString());
			Contact.setEmail(etEmail.getText().toString());
			Contact.save();
			Intent intent = new Intent(this, BBGridActivity.class);
			startActivity(intent);

		}

	}


}
