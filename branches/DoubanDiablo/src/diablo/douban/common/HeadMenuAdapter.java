package diablo.douban.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import diablo.douban.R;

public class HeadMenuAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    public static Integer[] mImageIds = {
    		R.drawable.m_homepage,                        
            R.drawable.m_friend,
            R.drawable.m_movie,
            R.drawable.m_book,            
            R.drawable.m_music,
            R.drawable.m_doumail,
            R.drawable.m_search,
    };
    public static int currentSelection = 0;
    public static Integer[] mImageIdsDisabled = {    	
    	R.drawable.m_homepage_disabled,                        
        R.drawable.m_friend_disabled,
        R.drawable.m_movie_disabled,
        R.drawable.m_book_disabled,        
        R.drawable.m_music_disabled,
        R.drawable.m_doumail_disabled,
        R.drawable.m_search_disabled,
    };

    public HeadMenuAdapter(Context c) {
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
        i.setLayoutParams(new Gallery.LayoutParams(60, 40));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        //i.setBackgroundResource(mGalleryItemBackground);
        return i;
        
    }
    
   
}