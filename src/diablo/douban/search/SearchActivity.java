package diablo.douban.search;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.broadcast.AbstractProgressListActivity;
import diablo.douban.common.LoaderImageView;
import diablo.douban.relationship.DoubanUserAdapter;

public class SearchActivity extends AbstractProgressListActivity{
	private DoubanUserAdapter adapter;
	private TextView paginatorTitle;
	private Button prePage, nextPage;
	private int start = 1, length = 20;
	
	private static final String[] searchClassify = new String[]{"用户", "电影", "书籍", "音乐"};
	EditText keyword;
	private int searchType;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		View view = LayoutInflater.from(this).inflate(R.layout.saying_form, null);
		keyword = (EditText)view.findViewById(R.id.sayingContent);
		
		Button submit = (Button)view.findViewById(R.id.iSay);	
		submit.setText("搜索");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择搜索类别");
		builder.setSingleChoiceItems(searchClassify, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		    	searchType = item;
		    	showDialog(PROGRESS_DIALOG);
		    	
		        dialog.dismiss();
		    }
		});
		final AlertDialog alert  = builder.create();
		
		
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				alert.show();
			}
		});
		getListView().addHeaderView(view);
		
		view = LayoutInflater.from(this).inflate(R.layout.header_paginator, null);
		paginatorTitle = ((TextView)view.findViewById(R.id.paginatorTitle));
		paginatorTitle.setText("请输入搜索关键字");		
		
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
		//showDialog(PROGRESS_DIALOG);
	}

	@Override
	protected void onProgressComplete() {
		setListAdapter(adapter);	
		paginatorTitle.setText("搜索结果");
		int end = start + length;
		int totalResult = Integer.parseInt(DoubanAccessor.getInstance().totalResults);
		end = end > totalResult ? totalResult : end;
		String pageInfo = "(" + start + "-" + end +",共" + DoubanAccessor.getInstance().totalResults + "条记录.)";
		paginatorTitle.setText(paginatorTitle.getText() + pageInfo);
		if(start == 1){
			prePage.setEnabled(false);
		}else{
			prePage.setEnabled(true);
		}
		if(end == totalResult){
			nextPage.setEnabled(false);
		}else{
			nextPage.setEnabled(true);
		}
	}

	@Override
	protected void onProgressLoadData() {
		switch(searchType){
    	case 0:
    		searchPeople();
    		break;
    	default:
    		break;
    	}
		
	}
	
	private void searchPeople(){
		List<DoubanUser> list = DoubanAccessor.getInstance().searchUser(keyword.getText().toString(), start, length);
		Log.i("DoubanDiablo", "onProgressLoadData: " + list.size());
		List<String> iconList = new ArrayList<String>();
		for(DoubanUser b : list){
			if(iconList.contains(b.getIcon())){
				iconList.add(b.getIcon());				
			}
		}
		LoaderImageView.loadDatasIntoMap(iconList);
		
		adapter =  new DoubanUserAdapter(activity, list);		
	}

}
