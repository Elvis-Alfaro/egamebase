package diablo.douban.note;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.view.View;
import android.widget.ListAdapter;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.accessor.pojo.DoubanNote;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.broadcast.SayingAdapter;
import diablo.douban.common.IDoubanDataProvider;
import diablo.douban.common.LoaderImageView;

public class NoteDataProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private ListActivity activity;
	private DoubanUser user;
	//private SayingAdapter.OnReplyClickListener listener;
	private int length = 15;
	
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
		return new NotesAdapter(activity, list);
	}

	@Override
	public View getFootView() {
		// TODO Auto-generated method stub
		return null;
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
