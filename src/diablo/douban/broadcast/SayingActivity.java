package diablo.douban.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import diablo.douban.DoubanDiablo;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.common.AbstractProgressListActivity;
import diablo.douban.common.HeadViewInflateHelper;
import diablo.douban.common.LoaderImageView;

public class SayingActivity extends AbstractProgressListActivity{	
	private SayingAdapter adapter;
	private TextView paginatorTitle;
	private Button prePage, nextPage;
	private int start = 1, length = 20;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//List<DoubanBroadcast> l = douban.getMyBroadcase(1, 10);	
		//this.setContentView(R.layout.listview);
		//getListView().addHeaderView(HeadViewInflateHelper.inflateMe(this, DoubanAuthData.getCurrent().getUsername(), DoubanAuthData.getCurrent().getIcon()));
		
		View view = LayoutInflater.from(this).inflate(R.layout.saying_form, null);
		final EditText content = (EditText)view.findViewById(R.id.sayingContent);
		
		Button submit = (Button)view.findViewById(R.id.iSay);	
		
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DoubanAccessor.getInstance().postSaying(content.getText().toString());
				content.setText("");
				showDialog(PROGRESS_DIALOG);
			}
		});
		getListView().addHeaderView(view);
		
		view = LayoutInflater.from(this).inflate(R.layout.header_paginator, null);
		paginatorTitle = ((TextView)view.findViewById(R.id.paginatorTitle));
		paginatorTitle.setText(R.string.broadcast);		
		
		prePage = (Button)view.findViewById(R.id.prePage);
		prePage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				start -= length; 	
				start = start < 1 ? 1 : start;
				showDialog(PROGRESS_DIALOG);
			}
		});
		
		nextPage = (Button)view.findViewById(R.id.nextPage);
		nextPage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				start += length;
				
				showDialog(PROGRESS_DIALOG);
			}
		});
		getListView().addFooterView(view);
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.glance_list_item,
				new ArrayList<String>()));
		showDialog(PROGRESS_DIALOG);
	}
	
	
	
	@Override
	protected void onProgressComplete() {	
		setListAdapter(adapter);	
		paginatorTitle.setText(R.string.broadcast);
		int end = start + length;
		//int totalResult = Integer.parseInt(douban.totalResults);
		//end = end > totalResult ? totalResult : end;
		String pageInfo = "(" + start + "-" + end +")";
		paginatorTitle.setText(paginatorTitle.getText() + pageInfo);
		if(start == 1){
			prePage.setEnabled(false);
		}else{
			prePage.setEnabled(true);
		}
	}


	@Override
	protected void onProgressLoadData() {
		
		List<DoubanBroadcast> list = DoubanAccessor.getInstance().getBroadcast("broadcast", DoubanAuthData.getCurrent().getUserid(), start, length);
		Log.i("DoubanDiablo", "onProgressLoadData: " + list.size());
		List<String> iconList = new ArrayList<String>();
		for(DoubanBroadcast b : list){
			if(b.getUser()!=null && iconList.contains(b.getUser().getIcon())){
				iconList.add(b.getUser().getIcon());				
			}
		}
		LoaderImageView.loadDatasIntoMap(iconList);
		
		adapter =  new SayingAdapter(activity, list);		
	}
}
