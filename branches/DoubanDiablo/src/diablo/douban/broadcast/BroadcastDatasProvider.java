package diablo.douban.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.common.IDoubanDataProvider;
import diablo.douban.common.LoaderImageView;

public class BroadcastDatasProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private ListActivity activity;
	//private SayingAdapter.OnReplyClickListener listener;
	
	public BroadcastDatasProvider(DoubanAccessor douban, ListActivity activity) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
		//Log.i("DoubanDiablo", "in BroadcastDatasProvider: " + (listener == null));
	}
	
	
	public ListAdapter getDatas(int start, int length) {
		List<DoubanBroadcast> list = douban.getBroadcast("broadcast", DoubanAuthData.getCurrent().getUserid(), start, length);			
		
		for(DoubanBroadcast b : list){
			if(b.getUser()!=null && iconList.contains(b.getUser().getIcon())){
				iconList.add(b.getUser().getIcon());				
			}
		}
		LoaderImageView.loadDatasIntoMap(iconList);
		
		return new SayingAdapter(activity, list);
	}

	/*
	 * <EditText android:id="@+id/sayingContent" 
        android:lines="1"
        android:layout_marginTop="2dip"
        android:layout_width="wrap_content"
      	android:ems="12"
        android:layout_height="wrap_content" 
        android:singleLine="false"
        android:gravity="top"
        />
   		
    <Button android:id="@+id/iSay"
        android:layout_width="wrap_content" 
        android:layout_height="wrap_content" 
        android:layout_gravity="center_vertical"
        android:background="@drawable/android_btn2"
        android:text="@string/I_say" />
        
        (non-Javadoc)
	 * @see diablo.douban.common.IDoubanDataProvider#getFootView()
	 */
	
	public View getFootView() {
		
		RelativeLayout view = new RelativeLayout(activity);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		
		final EditText content = new EditText(activity);
		content.setLines(1);
		content.setEms(19);
		content.setTextSize(12);
		content.setId(998);
	
		view.addView(content, lp);
		
		Button submit = new Button(activity);
		submit.setHeight(content.getHeight());
		
		submit.setText(R.string.I_say); 
		
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				douban.postSaying(content.getText().toString());
				content.setText("");
				activity.showDialog(0);
			}
		});
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // Verbose!
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.RIGHT_OF, 998);
		view.addView(submit, lp);
		return view;
	}

	
	public View getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getPaginatorText() {
		return "ÓÑÁÚ¹ã²¥";
	}

}
