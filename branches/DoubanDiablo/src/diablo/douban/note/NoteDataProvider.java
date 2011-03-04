package diablo.douban.note;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanNote;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.common.IDoubanDataProvider;

public class NoteDataProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private ListActivity activity;
	private DoubanUser user;
	//private SayingAdapter.OnReplyClickListener listener;
	private int length = 5;
	
	public NoteDataProvider(DoubanAccessor douban, ListActivity activity, DoubanUser user) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
		this.user = user;
		//Log.i("DoubanDiablo", "in BroadcastDatasProvider: " + (listener == null));
	}
	
	@Override
	public ListAdapter getDatas(int start) {
		List<DoubanNote> list = douban.getNotes(user, start, length);
		for(DoubanNote n : list){
			Log.i("DoubanDiablo", "~~~" + n.getPrivacy());
		}
		return new NotesAdapter(activity, list);
	}

	@Override
	public View getFootView() {
		RelativeLayout view = new RelativeLayout(activity);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // Verbose!
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		final Button btn1 = new Button(activity);
		btn1.setId(9999);
		btn1.setBackgroundResource(R.drawable.orange_btn);
		btn1.setText("写日志");
		btn1.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				//((HomepageActivity)activity).removeExtraView();					
				Intent intent = new Intent(activity, WriteNoteActivity.class);
				activity.startActivity(intent);
			}
		});
		view.addView(btn1, lp);
		
		return view;
	}

	@Override
	public View getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPaginatorText() {
		// TODO Auto-generated method stub
		return "日志列表";
	}

}
