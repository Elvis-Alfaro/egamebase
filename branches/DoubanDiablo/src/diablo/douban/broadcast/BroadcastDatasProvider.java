package diablo.douban.broadcast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ListActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAlbum;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.accessor.pojo.DoubanPhoto;
import diablo.douban.common.HomepageActivity;
import diablo.douban.common.IDoubanDataProvider;
import diablo.douban.common.LoaderImageView;

public class BroadcastDatasProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private ListActivity activity;
	//private SayingAdapter.OnReplyClickListener listener;
	private String uid;
	public BroadcastDatasProvider(DoubanAccessor douban, ListActivity activity, String uid) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
		this.uid = uid;
		//Log.i("DoubanDiablo", "in BroadcastDatasProvider: " + (listener == null));
	}
	
	private int length = 15;
	public ListAdapter getDatas(int start) {
		List<DoubanBroadcast> list = douban.getBroadcast("broadcast", uid, start, length);			
		
		for(DoubanBroadcast b : list){
			if(b.getUser()!=null && iconList.contains(b.getUser().getIcon())){
				iconList.add(b.getUser().getIcon());				
			}
			
			if(b.getCategory().equals("photo")){
				Matcher match = Pattern.compile(".*\\/photo\\/(\\d+)\\/.*").matcher(b.getContent());			
				if(match.find()){
					String pid = match.group(1);
					DoubanPhoto photo = douban.getPhoto(pid);
					b.getMap().put("image", photo.getIcon());
					b.getMap().put("object", photo);
				}
			}
			
			if(b.getMap().get("category")!=null){
				if(b.getMap().get("category").equals("photo_album")){
					Matcher match = Pattern.compile(".*\\/photos\\/album\\/(\\d+)\\/.*").matcher(b.getContent());			
					if(match.find()){
						String pid = match.group(1);
						DoubanAlbum album = douban.getAlbumDetail(pid);
						b.getMap().put("image", album.getCover_thumb());
						b.getMap().put("object", album);
					}
				}else if(b.getMap().get("category").equals("photo")){
					Matcher match = Pattern.compile(".*\\/photo\\/(\\d+)\\/.*").matcher(b.getContent());			
					if(match.find()){
						String pid = match.group(1);					
						DoubanPhoto photo = douban.getPhoto(pid);
						b.getMap().put("image", photo.getIcon());
						b.getMap().put("object", photo);
					}
				}
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
				((HomepageActivity)activity).removeExtraView();
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

	public String getTitle(){
		if(douban.getMe().getUid().equals(uid)){
			return "我的友邻广播";
		}else{
			return douban.getMe().getTitle() + "的友邻广播";
		}
	}
	
	public String getPaginatorText() {
		return "友邻广播";
	}

}
