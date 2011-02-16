package diablo.douban.relationship;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import diablo.douban.DoubanDiablo;
import diablo.douban.LoginActivity;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.common.LoaderImageView;



public class ContactsActivity extends ListActivity {
	static final int PROGRESS_DIALOG = 0;
	DoubanAccessor douban;
	ListActivity activity;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	
	SharedPreferences.Editor editor;
	
	ContactAdapter adapter;
	TextView paginatorTitle;
	Button prePage, nextPage;
	//List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		douban = DoubanAccessor.getInstance();
		activity = this;
		
		editor = getSharedPreferences("token", MODE_WORLD_WRITEABLE).edit();


		View view = LayoutInflater.from(this).inflate(R.layout.header_me, null);
		LoaderImageView me = (LoaderImageView)view.findViewById(R.id.thumbnailMe);
		me.setImageDrawable(douban.getMe().getIcon());
		TextView welcome = (TextView)view.findViewById(R.id.welcome);
		welcome.setText(welcome.getText() + douban.getMe().getTitle());
		
		getListView().addHeaderView(view);
		
		
		view = LayoutInflater.from(this).inflate(R.layout.header_paginator, null);
		paginatorTitle = ((TextView)view.findViewById(R.id.paginatorTitle));
		paginatorTitle.setText(R.string.my_contacts);
		getListView().addFooterView(view);
	
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
		
		ContactAdapter adapter = new ContactAdapter(this, new ArrayList<DoubanUser>());
		setListAdapter(adapter);	
		showDialog(PROGRESS_DIALOG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    SubMenu usersMenu = menu.getItem(3).getSubMenu();
	    
	    for(DoubanAuthData dat : DoubanAuthData.getAuthData()){
	    	MenuItem item = usersMenu.add(5, dat.getId(), 0, dat.getUsername());
	    	if(dat.getUserid().equals(DoubanAuthData.getCurrent().getUserid())){
	    		item.setChecked(true);
	    	}
	    }
	    usersMenu.add(1, 0, 0, R.string.add_account);
	    usersMenu.setGroupCheckable(5, true, true);
	    //usersMenu.getItem(0).setChecked(true);
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Log.i("DoubanDiablo", "~~~" + item.getItemId());
		for(DoubanAuthData dat : DoubanAuthData.getAuthData()){
			if(item.getItemId() == dat.getId() && !dat.getUserid().equals(DoubanAuthData.getCurrent().getUserid())){
				editor.putString("currentUserid", dat.getUserid());
				editor.commit();
				Intent intent = new Intent(this, DoubanDiablo.class);
				startActivity(intent);
				return true;
			}
		}
	    switch (item.getItemId()) {
	    case R.id.menu_refresh:
	    	showDialog(PROGRESS_DIALOG);
	        return true;
	    case R.id.menu_changeUser:	        
	        return true;
	    case 0:
	    	Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void deleteToken(){
		
		editor.remove("currentUserid");		
		editor.commit();
		
		Intent intent = new Intent(this, DoubanDiablo.class);
		startActivity(intent);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(ContactsActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			
			return progressDialog;
		default:
			return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog){
		super.onPrepareDialog(id, dialog);
		progressThread = new ProgressThread(handler);
		progressThread.start();
		
	}
	
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			progressDialog.setProgress(total);
			if (total >= 100) {
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
				
				setListAdapter(adapter);	
				paginatorTitle.setText(R.string.my_contacts);
				int end = start + length;
				int totalResult = Integer.parseInt(douban.totalResults);
				end = end > totalResult ? totalResult : end;
				String pageInfo = "(" + start + "-" + end +",π≤" + douban.totalResults + "»À.)";
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
				/*ListView lv = getListView();
				lv.setTextFilterEnabled(true);

				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// When clicked, show a toast with the TextView text
						//Toast.makeText(getApplicationContext(),
						//		((TextView) view).getText(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(AbstractReviewActivity.this, ItemContentActivity.class);
						intent.putExtra("content", contents.get(position));
						//intent.putExtra("title", reviews.get(position));
						startActivity(intent);
					}
				});*/
			}
		}
	};
	/*
	private List<Map<String, Object>> getData(int start, int length) {
		List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

		List<DoubanUser> list = douban.getPeopleFriends(null, start, length);
		for (DoubanUser user : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map = new HashMap<String, Object>();
			map.put("title", user.getTitle());
			if(user.getLocation()!=null){
				map.put("info", "(" + user.getLocation() + ")");
			}else{
				map.put("info", "");
			}
			map.put("img", user.getIcon());
			mData.add(map);
		}
		return mData;
	}*/
	int start = 1,  length = 20;
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			mState = STATE_RUNNING;
			total = 0;
				
			//get data
			List<DoubanUser> list = douban.getPeopleFriends(null, start, length);
			for(DoubanUser b : list){
				if(b.getIcon()!=null){
					LoaderImageView.loadDataIntoMap(b.getIcon());
				}
			}
			adapter =  new ContactAdapter(activity, list);				
			
			total = 100;
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", total);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
		
		public void setState(int state) {
			mState = state;
		}
	}
}
