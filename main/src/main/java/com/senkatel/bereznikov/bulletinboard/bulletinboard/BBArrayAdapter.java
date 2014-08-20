package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.senkatel.bereznikov.bulletinboard.main.R;
import com.senkatel.bereznikov.bulletinboard.util.Constants;
import com.senkatel.bereznikov.bulletinboard.util.Images;

import java.lang.ref.WeakReference;


public class BBArrayAdapter extends ArrayAdapter<Bulletin> {
	private final Activity actMainContext;

	public BBArrayAdapter(Context cntMainContext, int textViewResourceId) {
		super(cntMainContext, textViewResourceId, Bulletins.getAll());
		this.actMainContext = (Activity) cntMainContext;



	}

	static class ViewHolder {
		public TextView tvGridLayoutTitle;
		public TextView tvGridLayoutDescription;
		public ImageView ivImage;

	}

	@Override
	public View getView(int position, View vConvert, ViewGroup parent) {
		ViewHolder vhHolder;
		notifyDataSetChanged();
		View vRow = vConvert;

		if (vRow == null) {
			LayoutInflater inflater = actMainContext.getLayoutInflater();
			vRow = inflater.inflate(R.layout.grid_layout, null, true);
			vhHolder = new ViewHolder();
			vhHolder.tvGridLayoutTitle = (TextView) vRow.findViewById(R.id.txtGridLayoutTitle);
			vhHolder.tvGridLayoutDescription = (TextView) vRow.findViewById(R.id.txtGridLayoutDescription);
			vhHolder.ivImage = (ImageView) vRow.findViewById(R.id.ivBBGridActivity);

			vRow.setTag(vhHolder);
		} else {
			vhHolder = (ViewHolder) vRow.getTag();
		}
		vhHolder.tvGridLayoutTitle.setText(Bulletins.get(position).getTitle());
		vhHolder.tvGridLayoutDescription.setText(Bulletins.get(position).getText());
		new BitmapWorkerTask(vhHolder.ivImage).execute(Bulletins.get(position).getId());

		return vRow;
	}


	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> ivrImageReference;
		private int bulletinId = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			ivrImageReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			bulletinId = params[0];
			return Images.loadImage(bulletinId, Constants.IMAGE_WIDTH,Constants.IMAGE_HEIHT);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (ivrImageReference != null && bitmap != null) {
				final ImageView imageView = ivrImageReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}

		}
	}

}
