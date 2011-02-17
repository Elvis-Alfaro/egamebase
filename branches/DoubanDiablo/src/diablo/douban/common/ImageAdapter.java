package diablo.douban.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import diablo.douban.R;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    public static Integer[] mImageIds = {
            R.drawable.book,
            R.drawable.book,
            R.drawable.book,
            R.drawable.book,
            R.drawable.book,
            R.drawable.book,
            R.drawable.book,
    };
    public static int currentSelection = -1;
    public static Integer[] mImageIdsDisabled = {
        R.drawable.book_disabled,
        R.drawable.book_disabled,
        R.drawable.book_disabled,
        R.drawable.book_disabled,
        R.drawable.book_disabled,
        R.drawable.book_disabled,
        R.drawable.book_disabled,
    };

    public ImageAdapter(Context c) {
        mContext = c;       
        TypedArray a = c.obtainStyledAttributes(R.styleable.HelloGallery);
        mGalleryItemBackground = a.getResourceId(
                R.styleable.HelloGallery_android_galleryItemBackground, 0);
        a.recycle();
    }

    public int getCount() {
    	return Integer.MAX_VALUE;
    }

    public Object getItem(int position) {
    	if (position >= mImageIds.length) {
            position = position % mImageIds.length;
        }
        return position;
    }

    public long getItemId(int position) {
    	if (position >= mImageIds.length) {
            position = position % mImageIds.length;
        }
        return position;
    }

    public int checkPosition(int position) {
        if (position >= mImageIds.length) {
            position = position % mImageIds.length;
        }
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	position = position % mImageIds.length;
    	if (position < 0)
    	    position = position + mImageIds.length;
        ImageView i = new ImageView(mContext);
        if(currentSelection == position){
        	i.setImageResource(mImageIds[position]);
        }else{
        	i.setImageResource(mImageIdsDisabled[position]);
        }
        i.setLayoutParams(new Gallery.LayoutParams(80, 80));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        //i.setBackgroundResource(mGalleryItemBackground);
        return i;
        
    }
    
   
}