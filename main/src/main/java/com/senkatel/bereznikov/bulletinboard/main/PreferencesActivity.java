package com.senkatel.bereznikov.bulletinboard.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.senkatel.bereznikov.bulletinboard.contacts.ContactActivity;

/**
 * Class PreferencesActivity
 * Implements Interface for edit preferences
 */
public class PreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prefernces);

	}

	public void onPreferencesSetContactsClick(View view) {
		Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
		startActivity(intent);
	}
}
