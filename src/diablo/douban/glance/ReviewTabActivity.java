package diablo.douban.glance;

import diablo.douban.R;
import diablo.douban.R.drawable;
import diablo.douban.R.layout;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class ReviewTabActivity extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glance_reviewtab);

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent = new Intent(this, BookReview.class);
		spec = tabHost.newTabSpec("book").setIndicator("最新书评",
				res.getDrawable(R.drawable.ic_tab_artists_grey)).setContent(
				intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this, MovieReview.class);
		spec = tabHost.newTabSpec("movie").setIndicator("最新影评",
				res.getDrawable(R.drawable.ic_tab_artists_grey)).setContent(
				intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this, MusicReview.class);
		spec = tabHost.newTabSpec("music").setIndicator("最新乐评",
				res.getDrawable(R.drawable.ic_tab_artists_grey)).setContent(
				intent);
		tabHost.addTab(spec);
	}
}
