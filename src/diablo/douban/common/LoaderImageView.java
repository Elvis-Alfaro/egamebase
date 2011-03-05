package diablo.douban.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import diablo.douban.DoubanDiablo;
import diablo.douban.R;
import diablo.douban.accessor.DiabloDatabase;

/**
 * Free for anyone to use, just say thanks and share <img
 * src="http://www.anddev.org/images/smilies/smile.png" alt=":-)" title="Smile"
 * />
 * 
 * @author Blundell
 * 
 */
public class LoaderImageView extends LinearLayout {

	private static final int COMPLETE = 0;
	private static final int FAILED = 1;

	private Context mContext;
	private Drawable mDrawable;
	private ProgressBar mSpinner;
	private ImageView mImage;

	private static DiabloDatabase mDatabase;
	private static Map<String, byte[]> imageMap = new HashMap<String, byte[]>();

	public static void loadDataIntoMap(String url) {
		if (imageMap.get(url) != null) {
			return;
		} else {
			if (mDatabase != null) {
				byte[] b = mDatabase.queryImage(url);
				if (b != null) {
					imageMap.put(url, b);
				}
			}
		}
	}
	
	public static void loadDatasIntoMap(List<String> urls){
		List<String> list = new ArrayList<String>();
		for(String url : urls){
			if(!imageMap.containsKey(url)){
				list.add(url);
			}
		}
		if(mDatabase != null && list.size() > 0){
			Map<String, byte[]> map = mDatabase.queryImages(list.toArray(new String[]{}));
			imageMap.putAll(map);
		}
	}
	
	public static void closeDatabase(){
		if(mDatabase != null){
			mDatabase.close();
		}
	}

	/**
	 * This is used when creating the view in XML To have an image load in XML
	 * use the tag
	 * 'image="http://developer.android.com/images/dialog_buttons.png"'
	 * Replacing the url with your desired image Once you have instantiated the
	 * XML view you can call setImageDrawable(url) to change the image
	 * 
	 * @param context
	 * @param attrSet
	 */
	public LoaderImageView(final Context context, final AttributeSet attrSet) {
		super(context, attrSet);
		final String url = attrSet.getAttributeValue(null, "image");
		if (url != null) {
			instantiate(context, url);
		} else {
			instantiate(context, null);
		}		
	}

	/**
	 * This is used when creating the view programatically Once you have
	 * instantiated the view you can call setImageDrawable(url) to change the
	 * image
	 * 
	 * @param context
	 *            the Activity context
	 * @param imageUrl
	 *            the Image URL you wish to load
	 */
	public LoaderImageView(final Context context, final String imageUrl) {
		super(context);
		instantiate(context, imageUrl);
	}

	/**
	 * First time loading of the LoaderImageView Sets up the LayoutParams of the
	 * view, you can change these to get the required effects you want
	 */
	private void instantiate(final Context context, final String imageUrl) {
		mContext = context;
		if (mDatabase == null) {
			mDatabase = DoubanDiablo.database;
		}
		mImage = new ImageView(mContext);
		mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));	
		mSpinner = new ProgressBar(mContext);
		mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		mSpinner.setIndeterminate(true);

		addView(mSpinner);
		addView(mImage);

		if (imageUrl != null) {
			setImageDrawable(imageUrl);
		}
	}

	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest
	 * 
	 * @param imageUrl
	 *            the url of the image you wish to load
	 */
	public void setImageDrawable(final String imageUrl) {
		mDrawable = null;
		mSpinner.setVisibility(View.VISIBLE);
		mImage.setVisibility(View.GONE);
		new Thread() {
			public void run() {
				try {
					mDrawable = getDrawableFromUrl(imageUrl);
					imageLoadedHandler.sendEmptyMessage(COMPLETE);
				} catch (MalformedURLException e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				} catch (IOException e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				}
			};
		}.start();
	}

	/**
	 * Callback that is received once the image has been downloaded
	 */
	private final Handler imageLoadedHandler = new Handler(new Callback() {
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case COMPLETE:
				mImage.setImageDrawable(mDrawable);
				mImage.setVisibility(View.VISIBLE);
				mSpinner.setVisibility(View.GONE);
				break;
			case FAILED:
				mImage.setImageResource(R.drawable.dou48);
				mImage.setVisibility(View.VISIBLE);
				mSpinner.setVisibility(View.GONE);
				break;
			default:
				// Could change image here to a 'failed' image
				// otherwise will just keep on spinning
				break;
			}
			return true;
		}
	});

	/**
	 * Pass in an image url to get a drawable object
	 * 
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static Drawable getDrawableFromUrl(final String url)
			throws IOException, MalformedURLException {
		if (imageMap.containsKey(url)) {
			byte[] b = imageMap.get(url);
			Bitmap bm = BitmapFactory.decodeByteArray(b, 0, b.length);
			return new BitmapDrawable(bm);
		} else {
			BitmapDrawable drawable = (BitmapDrawable) Drawable
					.createFromStream(((java.io.InputStream) new java.net.URL(
							url).getContent()), "name");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			drawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
			byte[] array = out.toByteArray();
			mDatabase.insertImage(url, array);
			if (!imageMap.containsKey(url)) {
				imageMap.put(url, array);
			}
			return drawable;
		}
	}

}
