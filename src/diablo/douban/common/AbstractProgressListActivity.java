package diablo.douban.common;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import diablo.douban.LoginActivity;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.broadcast.SayingActivity;
import diablo.douban.relationship.ContactsActivity;

public abstract class AbstractProgressListActivity extends ListActivity{
	public static final int PROGRESS_DIALOG = 0;	
	protected Activity activity;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	SharedPreferences.Editor editor;
	View headView;
	
	protected abstract void onProgressLoadData();
	protected abstract void onProgressComplete();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.listview);
		activity = this;
		headView = HeadViewInflateHelper.inflateMe(this, DoubanAuthData.getCurrent().getUsername(), DoubanAuthData.getCurrent().getIcon());
		this.getListView().addHeaderView(headView);

		SharedPreferences sp = getSharedPreferences("token", MODE_WORLD_WRITEABLE);		
		editor = sp.edit();		
		
		final Gallery g = (Gallery) findViewById(R.id.gallery);
		final HeadMenuAdapter adapter = new HeadMenuAdapter(this);
	    g.setAdapter(adapter);
	    g.setSelection(Integer.MAX_VALUE/2);
	    
	    g.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	        	if (position >= HeadMenuAdapter.mImageIds.length) {
	                position = position % HeadMenuAdapter.mImageIds.length;
	            }
	        	HeadMenuAdapter.currentSelection = position;
	        	adapter.notifyDataSetChanged();
	        	//((ImageView)v).setImageResource(ImageAdapter.mImageIds[position]);
	            Toast.makeText(AbstractProgressListActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
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
	
	private void reloadHeadView(DoubanAuthData cur){
		LoaderImageView img = (LoaderImageView)headView.findViewById(R.id.thumbnailMe);
		img.setImageDrawable(cur.getIcon());
		TextView welcome = (TextView)headView.findViewById(R.id.welcome);
		welcome.setText("»¶Ó­Äã£¬" + cur.getUsername());		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		for(DoubanAuthData dat : DoubanAuthData.getAuthData()){
			if(item.getItemId() == dat.getId()){
				item.setChecked(true);
				editor.putString("currentUserid", dat.getUserid());
				editor.commit();
				//Intent intent = new Intent(this, DoubanDiablo.class);
				//DoubanDiablo.curAuthDat = dat;
				DoubanAuthData.setCurrent(dat);
				DoubanAccessor.init(dat.getToken(), dat.getSecret());
				reloadHeadView(dat);
				
				showDialog(PROGRESS_DIALOG);
				//startActivity(intent);
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
	protected void onPrepareDialog(int id, Dialog dialog){
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
				
			//get data
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
