package diablo.douban.doumail;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.broadcast.AbstractProgressListActivity;
import diablo.douban.common.LoaderImageView;

public class DoumailListActivity extends AbstractProgressListActivity{
	private DoumailAdapter adapter;
	private TextView paginatorTitle;
	private Button prePage, nextPage;
	private int start = 1, length = 20;
	
	private boolean inbox = true;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		
		//List<DoubanBroadcast> l = douban.getMyBroadcase(1, 10);	
		//this.setContentView(R.layout.listview);
		//getListView().addHeaderView(HeadViewInflateHelper.inflateMe(this, DoubanAuthData.getCurrent().getUsername(), DoubanAuthData.getCurrent().getIcon()));

		View view = LayoutInflater.from(this).inflate(R.layout.header_paginator, null);
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
	
	int menuItemId;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if(menu != null){
			MenuItem item = menu.add(inbox ? "发件箱" : "收件箱");	
			menuItemId = item.getItemId();
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == menuItemId){
			item.setTitle(inbox ? "收件箱": "发件箱");
			inbox = !inbox;
			showDialog(PROGRESS_DIALOG);
			return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	protected void onProgressComplete() {
		setListAdapter(adapter);
		if(inbox){
			this.setTitle("收件箱");
			paginatorTitle.setText("收件箱");
		}else{
			this.setTitle("发件箱");
			paginatorTitle.setText("发件箱");
		}
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
		List<Doumail> list = null;
		DoubanAccessor douban = DoubanAccessor.getInstance();
		if(inbox){
			list = douban.getDoumailList("INBOX", false, start, length);
		}else{
			list = douban.getDoumailList("OUTBOX", false, start, length);
		}
		List<String> iconList = new ArrayList<String>();
		for(Doumail b : list){
			if(b.getFrom()!=null && iconList.contains(b.getFrom().getIcon())){
				iconList.add(b.getFrom().getIcon());				
			}
		}
		LoaderImageView.loadDatasIntoMap(iconList);
		
		adapter =  new DoumailAdapter(activity, list, inbox);	
	}

}
