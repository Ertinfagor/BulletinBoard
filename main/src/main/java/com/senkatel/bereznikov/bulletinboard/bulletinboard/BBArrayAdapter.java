package com.senkatel.bereznikov.bulletinboard.bulletinboard;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

/**
 * Class BBArrayAdapter
 * Implements ArrayAdapter for main GridView
 * Implements Load image with concurrency on separate thread
 */
@SuppressWarnings("ALL")
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
		vhHolder.tvGridLayoutTitle.setText(Bulletins.get(position).getsTitle());
		vhHolder.tvGridLayoutDescription.setText(Bulletins.get(position).getsText());

		if (cancelPotentialWork(Bulletins.get(position).getIntBulletinId(), vhHolder.ivImage)) {
			final BitmapWorkerTask loadImageTask = new BitmapWorkerTask(vhHolder.ivImage);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(getContext().getResources(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.nonimage), loadImageTask);
			vhHolder.ivImage.setImageDrawable(asyncDrawable);
			loadImageTask.execute(Bulletins.get(position).getIntBulletinId());
		}


		return vRow;
	}

/*Methods and classes for load image on separate thread with concurrency article(http://developer.android.com/training/displaying-bitmaps/process-bitmap.html)*/

	/**
	 * Load Image by id in separate thread
	 */
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> wrImageViewReference;
		private int bulletinId = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			wrImageViewReference = new WeakReference<ImageView>(imageView);

		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			bulletinId = params[0];
			return Images.loadImage(bulletinId, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIHT);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (wrImageViewReference != null && bitmap != null) {
				final ImageView imageView = wrImageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	/**
	 * Dedicated Drawable subclass to store a reference back to the worker task
	 */
	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	/**
	 * Checks if another running task is already associated with the ImageView.
	 *
	 * @param data      id of Bulletin
	 * @param imageView associated ImageView
	 * @return is work canceling
	 */
	public static boolean cancelPotentialWork(int data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final int bitmapData = bitmapWorkerTask.bulletinId;
			// If bitmapData is not yet set or it differs from the new data
			if (bitmapData == 0 || bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}

	/**
	 * Method is used to retrieve the task associated with a particular ImageView:
	 *
	 * @param imageView target ImageView
	 * @return task that`s associated
	 */
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

}
