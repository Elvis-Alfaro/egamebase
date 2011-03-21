package diablo.douban.album;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import diablo.douban.R;
import diablo.douban.accessor.pojo.DoubanPhoto;
import diablo.douban.common.LoaderImageView;

public class PhotoDetailActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_details);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		DoubanPhoto photo = (DoubanPhoto)getIntent().getSerializableExtra("photo");
		LoaderImageView image = (LoaderImageView)findViewById(R.id.image);
		//Log.i("DoubanDiablo", photo.toString());
		image.setImageDrawable(photo.getImage(), false);
	}
}
