package com.senkatel.bereznikov.bulletinboard.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.BBGridActivity;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletin;
import com.senkatel.bereznikov.bulletinboard.bulletinboard.Bulletins;
import com.senkatel.bereznikov.bulletinboard.util.Constants;

/**
 * Created by Bereznik on 28.08.2014.
 */
public class OkDismissDialog extends DialogFragment implements DialogInterface.OnClickListener{
	private Bulletin bulletin;
	Context context;

	public OkDismissDialog(Bulletin bulletin, Context context) {
		this.bulletin = bulletin;
		this.context = context;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.isDeleteMessage)).setPositiveButton(getActivity().getString(R.string.isdelete), this)
				.setNegativeButton(getActivity().getString(R.string.isCancel), this)
				.setMessage(getActivity().getString(R.string.isDeleteMessage));
		return adb.create();
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case Dialog.BUTTON_POSITIVE:
				Bulletins.deleteBulletin(bulletin);
				context.startActivity(new Intent(getActivity(), BBGridActivity.class));
				break;
		}
	}

}
