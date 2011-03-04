package diablo.douban.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.common.IDoubanDataProvider;
import diablo.douban.common.LoaderImageView;
import diablo.douban.relationship.DoubanUserAdapter;

public class SearchDatasProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private Activity activity;
	public static final String[] searchClassify = new String[]{"用户", "电影", "书籍", "音乐"};
	public static String keyword;	
	public static int searchType;
	
	private int length = 15;
	
	private Handler handler;
	public SearchDatasProvider(DoubanAccessor douban, Activity activity, Handler handler) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
		this.handler = handler;
	}
	
	public ListAdapter getDatas(int start) {
		ListAdapter adapter = null;
		switch(searchType){
    	case 0:
    		adapter = searchPeople(start, length);
    		break;
    	default:
    		break;
    	}
		return adapter;
	}

	private ListAdapter searchPeople(int start, int length){
		if(keyword == null || keyword.trim().equals("")){
			return new ArrayAdapter<String>(activity, R.layout.glance_list_item,
					new ArrayList<String>());
		}
		List<DoubanUser> list = DoubanAccessor.getInstance().searchUser(keyword, start, length);
		Log.i("DoubanDiablo", "onProgressLoadData: " + list.size());
		List<String> iconList = new ArrayList<String>();
		for(DoubanUser b : list){
			if(iconList.contains(b.getIcon())){
				iconList.add(b.getIcon());				
			}
		}
		LoaderImageView.loadDatasIntoMap(iconList);
		
		return new DoubanUserAdapter(activity, list);		
	}
	
	public View getFootView() {		
		RelativeLayout view = new RelativeLayout(activity);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		
		
		final EditText keyword = new EditText(activity);
		keyword.setLines(1);
		keyword.setEms(19);
		keyword.setTextSize(12);
		keyword.setId(997);
			
		
	
		view.addView(keyword, lp);
		
		Button submit = new Button(activity);
		submit.setHeight(keyword.getHeight());
		
		submit.setText("搜索"); 
		
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Message msg = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("keyword", keyword.getText().toString());
				msg.setData(b);
				handler.sendMessage(msg);
			}
		});
		
		lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // Verbose!
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.RIGHT_OF, 997);
		view.addView(submit, lp);
		return view;
	}

	public View getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPaginatorText() {
		if(keyword == null || keyword.trim().equals("")){
			return "豆瓣搜索(搜用户/影/音/书)";
		}else{
			return searchClassify[searchType] + "搜索";
		}
	}

}
