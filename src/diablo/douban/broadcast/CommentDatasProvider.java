package diablo.douban.broadcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.common.IDoubanDataProvider;

public class CommentDatasProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private ListActivity activity;
	private DoubanBroadcast curBd;
	
	private int length = 15;
	
	public CommentDatasProvider(DoubanAccessor douban, ListActivity activity, DoubanBroadcast curBd) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
		this.curBd = curBd;
	}
	
	
	public ListAdapter getDatas(int start) {
		List<Map<String, Object>> dat = new ArrayList<Map<String, Object>>();
		if(curBd.getCategory().equals("recommendation")){
			List<DoubanBroadcast> rlist = DoubanAccessor.getInstance().getBroadcast("RECOMMENDATION", curBd.getUser().getId(), 1, 10);
			curBd = DoubanAccessor.getInstance().getRecommendation(curBd, rlist);			
			if(curBd == null){				
				Toast.makeText(activity, 
						"没找到对应推荐的评论！这个不能怪大菠萝！要怪就怪不负责任的豆瓣API的设计人员！！" +
						"原因是API里同一条“广播”和对应的“推荐”的ID竟然是不同的！！" +
						"害你们增加了流量，我增加了代码量，而且还可能会出现这种找不到评论的错误！", 1).show();				
			}
		}
		
		if(curBd != null){
			List<DoubanBroadcast> list = DoubanAccessor.getInstance().getComments(curBd.getId(), start, length);
			
			for (DoubanBroadcast d : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("user", d.getUser().getTitle() + ":  ");
				map.put("content", d.getContent());
				dat.add(map);
			}
			
		}
		return new SimpleAdapter(activity, dat, R.layout.comments_list_item,
				new String[] { "user", "content" }, new int[] { R.id.user,
						R.id.content });
	}

	
	public View getFootView() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public View getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getPaginatorText() {
		// TODO Auto-generated method stub
		return "广播回复";
	}


	public String getTitle() {
		// TODO Auto-generated method stub
		return getPaginatorText();
	}

}
