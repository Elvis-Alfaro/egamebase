package diablo.douban.common;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import diablo.douban.LoginActivity;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.broadcast.SayingActivity;
import diablo.douban.broadcast.SayingAdapter;
import diablo.douban.doumail.DoumailAdapter;
import diablo.douban.relationship.ContactsActivity;
import diablo.douban.relationship.DoubanUserAdapter;

public class HomepageActivity extends ListActivity {
	public static final int SIZE_PER_PAGE = 20;
	public static final int PROGRESS_DIALOG = 0;
	protected Activity activity;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	SharedPreferences.Editor editor;
	View headView;

	private TextView paginatorTitle;
	private Button prePage, nextPage;
	private int start = 1, length = SIZE_PER_PAGE;
	
	private ListAdapter adapter;
	
	private int type = 0;
	
	private String paginatorTitleText;
	
	private void resetPage(){
		start = 1;
		length = SIZE_PER_PAGE;
	}
	
	private boolean inbox = true;
	
	private static DoubanAccessor douban = DoubanAccessor.getInstance();
	protected void onProgressLoadData(){
		douban.totalResults = "0";
		List<String> iconList = new ArrayList<String>();
		switch (type) {
		case 0: // home page
			List<DoubanBroadcast> list = douban.getBroadcast("broadcast", DoubanAuthData.getCurrent().getUserid(), start, length);			
			
			for(DoubanBroadcast b : list){
				if(b.getUser()!=null && iconList.contains(b.getUser().getIcon())){
					iconList.add(b.getUser().getIcon());				
				}
			}
			LoaderImageView.loadDatasIntoMap(iconList);
			paginatorTitleText = "友邻广播";
			adapter =  new SayingAdapter(activity, list);
			break;
		case 1:	// friend	
			List<DoubanUser> friendList = douban.getPeopleFriends(null, start, length);
			for(DoubanUser b : friendList){
				if(b.getIcon()!=null){
					LoaderImageView.loadDataIntoMap(b.getIcon());
				}
			}
			paginatorTitleText = "我关注的人";
			adapter =  new DoubanUserAdapter(activity, friendList);	
			break;
		case 2:	// movie			
			break;
		case 3:	// book			
			break;
		case 4:	// music			
			break;
		case 5:	// doumail
			List<Doumail> doumailList = null;			
			if(inbox){
				paginatorTitleText = "收件箱";
				doumailList = douban.getDoumailList("INBOX", false, start, length);
			}else{
				paginatorTitleText = "发件箱";
				doumailList = douban.getDoumailList("OUTBOX", false, start, length);
			}
			
			for(Doumail b : doumailList){
				if(b.getFrom()!=null && iconList.contains(b.getFrom().getIcon())){
					iconList.add(b.getFrom().getIcon());				
				}
			}
			LoaderImageView.loadDatasIntoMap(iconList);
			
			adapter =  new DoumailAdapter(activity, doumailList, inbox);	
			break;
		case 6:	// search			
			break;
		}
	}

	protected void onProgressComplete(){
		setListAdapter(adapter);		
		
		if(start == 1){
			prePage.setEnabled(false);
		}else{
			prePage.setEnabled(true);
		}
		
		int end = 0, totalResult = Integer.parseInt(DoubanAccessor.getInstance().totalResults);
		end = start + length;
		if(totalResult != 0){			
			end = end > totalResult ? totalResult : end;
			paginatorTitleText += "(" + start + "-" + end + ", 共" + totalResult + "条记录)"; 
		}else{
			
			paginatorTitleText += "(" + (adapter.getCount() == 0 ? 0 :start) + "-" + end + ")"; 
		}
		
		if(end == totalResult){
			nextPage.setEnabled(false);
		}else{
			nextPage.setEnabled(true);
		}
		paginatorTitle.setText(paginatorTitleText);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.listview);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity = this;
		headView = HeadViewInflateHelper.inflateMe(this, DoubanAuthData
				.getCurrent().getUsername(), DoubanAuthData.getCurrent()
				.getIcon());
		this.getListView().addHeaderView(headView);

		SharedPreferences sp = getSharedPreferences("token",
				MODE_WORLD_WRITEABLE);
		editor = sp.edit();

		final Gallery g = (Gallery) findViewById(R.id.gallery);
		final HeadMenuAdapter adapter = new HeadMenuAdapter(this);
		g.setAdapter(adapter);
		g.setSelection(Integer.MAX_VALUE / 2);

		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				if (position >= HeadMenuAdapter.mImageIds.length) {
					position = position % HeadMenuAdapter.mImageIds.length;
				}
				HeadMenuAdapter.currentSelection = position;
				adapter.notifyDataSetChanged();
				// //((ImageView)v).setImageResource(ImageAdapter.mImageIds[position]);
				// Toast.makeText(AbstractProgressListActivity.this, "" +
				// position, Toast.LENGTH_SHORT).show();

				/*
				 * R.drawable.m_homepage, R.drawable.m_friend,
				 * R.drawable.m_movie, R.drawable.m_book, R.drawable.m_music,
				 * R.drawable.m_doumail, R.drawable.m_search,
				 */
				type = position;
				resetPage();
				showDialog(PROGRESS_DIALOG);
				
			}
		});
		
		
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
		showDialog(PROGRESS_DIALOG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		SubMenu usersMenu = menu.getItem(3).getSubMenu();

		for (DoubanAuthData dat : DoubanAuthData.getAuthData()) {
			MenuItem item = usersMenu.add(5, dat.getId(), 0, dat.getUsername());
			if (dat.getUserid().equals(DoubanAuthData.getCurrent().getUserid())) {
				item.setChecked(true);
			}
		}
		usersMenu.add(1, 0, 0, R.string.add_account);
		usersMenu.setGroupCheckable(5, true, true);
		// usersMenu.getItem(0).setChecked(true);

		return true;
	}

	private void reloadHeadView(DoubanAuthData cur) {
		LoaderImageView img = (LoaderImageView) headView
				.findViewById(R.id.thumbnailMe);
		img.setImageDrawable(cur.getIcon());
		TextView welcome = (TextView) headView.findViewById(R.id.welcome);
		welcome.setText("欢迎你，" + cur.getUsername());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		for (DoubanAuthData dat : DoubanAuthData.getAuthData()) {
			if (item.getItemId() == dat.getId()) {
				item.setChecked(true);
				editor.putString("currentUserid", dat.getUserid());
				editor.commit();
				// Intent intent = new Intent(this, DoubanDiablo.class);
				// DoubanDiablo.curAuthDat = dat;
				DoubanAuthData.setCurrent(dat);
				DoubanAccessor.init(dat.getToken(), dat.getSecret());
				reloadHeadView(dat);

				showDialog(PROGRESS_DIALOG);
				// startActivity(intent);
				return true;
			}
		}
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			showDialog(PROGRESS_DIALOG);
			return true;
		case R.id.menu_changeUser:
			return true;
		case R.id.menu_my_contacts:
			Intent intent = new Intent(this, ContactsActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_broadcast:
			intent = new Intent(this, SayingActivity.class);
			startActivity(intent);
			return true;
		case 0:
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			progressDialog.setProgress(total);
			if (total >= 100) {
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);

				onProgressComplete();
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");

			return progressDialog;
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		progressThread = new ProgressThread(handler);
		progressThread.start();

	}

	class ProgressThread extends Thread {
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

			// get data
			onProgressLoadData();

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
