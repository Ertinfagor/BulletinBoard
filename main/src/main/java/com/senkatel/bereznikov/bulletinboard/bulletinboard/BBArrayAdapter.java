package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;
import com.senkatel.bereznikov.bulletinboard.util.MainSync;

import java.lang.ref.WeakReference;


public class BBArrayAdapter extends ArrayAdapter<Bulletin> {
	private final Activity context;

	public BBArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId, Bulletins.getAll());
		this.context = (Activity)context;



	}

	static class ViewHolder {
		public TextView txtGridLayoutTitle;
		public TextView txtGridLayoutDescription;
		public ImageView imageView;

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
			holder.imageView = (ImageView)rowView.findViewById(R.id.ivBBGridActivity);

			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}
		holder.txtGridLayoutTitle.setText(Bulletins.get(position).getTitle());
		holder.txtGridLayoutDescription.setText(Bulletins.get(position).getText());
		new BitmapWorkerTask(holder.imageView).execute(Bulletins.get(position).getId());

		return rowView;
	}


	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int id = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			id = params[0];
			return Images.loadImage(id,100,100);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}

		}
	}

}
