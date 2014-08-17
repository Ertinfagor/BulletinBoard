package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;


public class BBArrayAdapter extends ArrayAdapter<Bulletin> {
	private final Activity context;

	public BBArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId, Bulletins.getAll());
		this.context = (Activity)context;



	}

	static class ViewHolder {
		public TextView txtGridLayoutTitle;
		public TextView txtGridLayoutDescription;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		notifyDataSetChanged();
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.grid_layout, null, true);
			holder = new ViewHolder();
			holder.txtGridLayoutTitle = (TextView) rowView.findViewById(R.id.txtGridLayoutTitle);
			holder.txtGridLayoutDescription = (TextView) rowView.findViewById(R.id.txtGridLayoutDescription);

			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}
		holder.txtGridLayoutTitle.setText(Bulletins.get(position).getTitle());
		holder.txtGridLayoutDescription.setText(Bulletins.get(position).getText());


		return rowView;
	}

}
