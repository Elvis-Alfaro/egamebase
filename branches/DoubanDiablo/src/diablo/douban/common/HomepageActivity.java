package diablo.douban.common;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import diablo.douban.DoubanDiablo;
import diablo.douban.LoginActivity;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.broadcast.BroadcastDatasProvider;
import diablo.douban.broadcast.SayingActivity;
import diablo.douban.doumail.DoumailDatasProvider;
import diablo.douban.note.NoteDataProvider;
import diablo.douban.relationship.ContactDatasProvider;
import diablo.douban.relationship.ContactsActivity;
import diablo.douban.search.SearchDatasProvider;


public class HomepageActivity extends ListActivity {
	/*
	 * R.drawable.m_homepage,                        
            R.drawable.m_friend,
            R.drawable.m_note,
            R.drawable.m_movie,
            R.drawable.m_book,            
            R.drawable.m_music,
            R.drawable.m_activity,
            R.drawable.m_search,
            R.drawable.m_doumail,
	 */
	public enum MenuType{
		broadcast(0),
		friend(1),
		note(2),
		movie(3),
		music(4),
		book(5),
		activity(6),
		search(7),
		doumail(8),
		other(10);
		MenuType(int index){
			this.index = index;
		}
		public int getIndex(){
			return index;
		}
		private final int index;
	}
	public static final int PROGRESS_DIALOG = 0;
	protected Activity activity;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	SharedPreferences.Editor editor;
	View headView;

	private TextView paginatorTitle;
	private Button prePage, nextPage;
	private static int start = 1;
	private static int length;
	private ListAdapter adapter;

	private static MenuType type = MenuType.broadcast;

	private String paginatorTitleText;

	private static DoubanAccessor douban = DoubanAccessor.getInstance();
	private IDoubanDataProvider dataProvider;

	private View extraHeadView, extraFootView;
	private ViewGroup.MarginLayoutParams mlp;
	private int marginPixelBottom = 0;

	private static boolean refreshPage = true;
	
	public static DoubanUser current = douban.getMe();
	
	public static void reloadData(DoubanUser user, MenuType t){
		current = user;
		type = t;
		resetPage();
		HeadMenuAdapter.currentSelection = t.getIndex();
		headMenu.setSelection(Integer.MAX_VALUE / 2 + t.getIndex());
		
		refreshPage = true;
	}
	
	protected void onProgressLoadData() {
		refreshPage = true;
		douban.totalResults = "0";
		marginPixelBottom = 0;
	
		switch (type) {
		case broadcast: // home page
			length = 15;
			dataProvider = new BroadcastDatasProvider(douban, this, current.getUid());
			marginPixelBottom = 40;
			break;
		case friend: // friend
			length = 15;
			refreshPage = false;
			dataProvider = new ContactDatasProvider(douban, this, current);			
			break;
		case note: // note
			length = 5;
			//refreshPage = false;
			dataProvider = new NoteDataProvider(douban, this, current);
			break;
		case movie: // movie
			break;
		case book: // book
			break;
		case music: // music
			break;
		case doumail: // doumail
			length = 15;
			dataProvider = new DoumailDatasProvider(douban, this);
			marginPixelBottom = 25;
			break;
		case search: // search
			length = 15;
			dataProvider = new SearchDatasProvider(douban, this, searchDialog);
			marginPixelBottom = 40;
			break;
		case activity:
			break;
		case other: // comments
			break;
		}

		if (dataProvider != null) {
			paginatorTitleText = dataProvider.getPaginatorText();
			adapter = dataProvider.getDatas(start);

			this.extraHeadView = dataProvider.getHeaderView();
			this.extraFootView = dataProvider.getFootView();

		}
	}

	private static void resetPage() {
		start = 1;
	}

	protected void onProgressComplete() {
		setListAdapter(adapter);
		currentPage.setText("当前位置：" + dataProvider.getTitle());
		if (extraHeadView != null) {
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			addContentView(extraHeadView, lp);
		}
		if (extraFootView != null) {
			Log.i(TAG, "addContentView");
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			addContentView(extraFootView, lp);
			// extraFootView.m
			
			
		}
		mlp.setMargins(0, 40, 0, marginPixelBottom);
		if (start == 1) {
			prePage.setEnabled(false);
		} else {
			prePage.setEnabled(true);
		}

		int end = 0, totalResult = Integer.parseInt(DoubanAccessor
				.getInstance().totalResults);
		end = start + length - 1;
		if (totalResult != 0) {
			end = end > totalResult ? totalResult : end;
			paginatorTitleText += "(" + start + "-" + end + ", 共" + totalResult
					+ "条记录)";
		} else {

			paginatorTitleText += "(" + (adapter.getCount() == 0 ? 0 : start)
					+ "-" + end + ")";
		}

		if (end == totalResult) {
			nextPage.setEnabled(false);
		} else {
			nextPage.setEnabled(true);
		}
		paginatorTitle.setText(paginatorTitleText);
	}

	public void onResume() {
		super.onResume();

		douban = DoubanAccessor.getInstance();
		if (DoubanAuthData.getCurrent() != null) {
			reloadHeadView(DoubanAuthData.getCurrent());
		}
		removeExtraView();
		if(refreshPage){
			showDialog(0);
		}
	}

	AlertDialog.Builder builder;
	AlertDialog alert;
	static Gallery headMenu;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.listview);
		this.getListView().setBackgroundResource(
				DoubanDiablo.currentBgResourceId);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity = this; 
		if(DoubanAuthData.getCurrent() != null){
			headView = HeadViewInflateHelper.inflateMe(this, DoubanAuthData
				.getCurrent().getUsername(), DoubanAuthData.getCurrent()
				.getIcon());
			this.getListView().addHeaderView(headView);
		}
		builder = new AlertDialog.Builder(this);

		SharedPreferences sp = getSharedPreferences("token",
				MODE_WORLD_WRITEABLE);
		editor = sp.edit();

		headMenu = (Gallery) findViewById(R.id.gallery);
		final HeadMenuAdapter adapter = new HeadMenuAdapter(this);
		headMenu.setAdapter(adapter);
		headMenu.setSelection(Integer.MAX_VALUE / 2);

		headMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				Log.i(TAG, position + ", " + position % HeadMenuAdapter.mImageIds.length);
				if (position >= HeadMenuAdapter.mImageIds.length) {
					position = position % HeadMenuAdapter.mImageIds.length;
				}
				current = DoubanAccessor.getInstance().getMe();
				HeadMenuAdapter.currentSelection = position;
				adapter.notifyDataSetChanged();
				type = MenuType.values()[position];
				
				resetPage();
				removeExtraView();
				showDialog(PROGRESS_DIALOG);

			}
		});

		View view = LayoutInflater.from(this).inflate(
				R.layout.header_paginator, null);
		paginatorTitle = ((TextView) view.findViewById(R.id.paginatorTitle));
		paginatorTitle.setText(R.string.broadcast);

		prePage = (Button) view.findViewById(R.id.prePage);
		prePage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				start -= length;
				start = start < 1 ? 1 : start;
				removeExtraView();
				showDialog(PROGRESS_DIALOG);
			}
		});

		nextPage = (Button) view.findViewById(R.id.nextPage);
		nextPage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				start += length;
				removeExtraView();
				showDialog(PROGRESS_DIALOG);
			}
		});
		getListView().addFooterView(view);
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.glance_list_item, new ArrayList<String>()));
		mlp = (ViewGroup.MarginLayoutParams) HomepageActivity.this
				.getListView().getLayoutParams();
		// showDialog(PROGRESS_DIALOG);
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

	TextView currentPage;
	private void reloadHeadView(DoubanAuthData cur) {
		LoaderImageView img = (LoaderImageView) headView
				.findViewById(R.id.thumbnailMe);		
		img.setImageDrawable(cur.getIcon(), true);
		TextView welcome = (TextView) headView.findViewById(R.id.welcome);
		welcome.setText("欢迎你，" + cur.getUsername());
		
		currentPage = (TextView)headView.findViewById(R.id.current_position);
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
				douban = DoubanAccessor.getInstance();
				reloadHeadView(dat);
				if (extraHeadView != null) {
					((ViewGroup) extraHeadView.getParent())
							.removeView(extraHeadView);
					extraHeadView = null;
				}

				if (extraFootView != null) {
					((ViewGroup) extraFootView.getParent())
							.removeView(extraFootView);
					extraFootView = null;
				}

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

	public static final String TAG = "DoubanDiablo";

	public void removeExtraView() {
		if (extraHeadView != null) {
			((ViewGroup) extraHeadView.getParent()).removeView(extraHeadView);
			extraHeadView = null;
		}

		if (extraFootView != null) {
			((ViewGroup) extraFootView.getParent()).removeView(extraFootView);
			extraFootView = null;
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

	final Handler searchDialog = new Handler() {
		public void handleMessage(Message msg) {
			removeExtraView();
			SearchDatasProvider.keyword = msg.getData().getString("keyword");
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("请选择搜索类别");
			builder.setSingleChoiceItems(SearchDatasProvider.searchClassify,
					-1, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							SearchDatasProvider.searchType = item;
							dialog.dismiss();
							activity.showDialog(0);

						}
					});

			builder.create().show();
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
