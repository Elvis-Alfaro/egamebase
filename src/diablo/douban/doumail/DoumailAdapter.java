package diablo.douban.doumail;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import diablo.douban.DoubanDiablo;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.common.LoaderImageView;

public class DoumailAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Doumail> mData;
	private SimpleDateFormat format;
	private Activity activity;
	private boolean inbox; 
	
	public DoumailAdapter(Activity activity, List<Doumail> mData, boolean inbox) {
		this.activity = activity;
		this.mInflater = LayoutInflater.from(activity);
		this.mData = mData;
		format = new SimpleDateFormat("MM-dd HH:mm:ss");
		this.inbox = inbox;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private boolean isInbox(){
		return inbox;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(
					R.layout.doumail_listitem, null);
			((LinearLayout)(convertView.findViewById(R.id.list_item))).setBackgroundResource(DoubanDiablo.currentListItemBgResourceId);
			
			holder.userImg = (LoaderImageView) convertView
					.findViewById(R.id.saying_thumbnail);
			holder.user = (Button) convertView.findViewById(R.id.user);
			holder.title = (TextView) convertView.findViewById(R.id.doumail_title);
			//holder.info.setVisibility(View.GONE);
			holder.newDoumail = (TextView)convertView.findViewById(R.id.newDoubail);
			holder.detail = (Button) convertView.findViewById(R.id.doumail_detail);
			holder.delete = (Button)convertView.findViewById(R.id.doumail_delete);
			holder.time = (TextView) convertView.findViewById(R.id.doumail_time);
			//holder.detailImg = (LoaderImageView)convertView.findViewById(R.id.saying_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Doumail bd = mData.get(position);
		
		holder.newDoumail.setVisibility(View.GONE);
		if(bd.isUnread()){
			holder.newDoumail.setVisibility(View.VISIBLE);
		}
		
		holder.userImg.setImageDrawable(bd.getFrom().getIcon());
		holder.user.setText("来自：" + bd.getFrom().getTitle());
		if(bd.getTime() != null){
			holder.time.setText(format.format(bd.getTime()));
		}
		String title = bd.getTitle();		
		
		holder.title.setText(title);
		
		OnClickListener l = new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(bd.getTitle());
				builder.setMessage(DoubanAccessor.getInstance().getDoumail(bd.getId()).getContent());
				if(isInbox()){
					builder.setPositiveButton("回复", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               DoubanUser to = bd.getFrom();
				               Doumail mail = new Doumail();
				               mail.setTitle(bd.getTitle());
				               mail.setContent(bd.getContent());
				               Intent intent = new Intent(activity, ComposeDoumailActivity.class);
				               mail.setFrom(bd.getTo());
				               mail.setTo(to);
				               intent.putExtra(ComposeDoumailActivity.DOUMAIL, mail);
				               activity.startActivity(intent);
				           }
				       })
				       .setNegativeButton("返回", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               dialog.cancel();
				           }
				       });
				}
				builder.show();
			}
		};
		holder.title.setOnClickListener(l);
		holder.detail.setOnClickListener(l);
		
		holder.delete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {		
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("确定删除豆邮？");
				builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   DoubanAccessor.getInstance().deleteDoumail(bd.getId());
		        		   mData.remove(bd);
		        		   DoumailAdapter.this.notifyDataSetChanged();
		        		   Toast.makeText(activity.getApplicationContext(), "已删除。", 1).show();
			        	  
			           }
			       })
			       .setNegativeButton("否", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               dialog.cancel();
			           }
			       });
				builder.show();
			}
		});

		return convertView;
	}
	
	final class ViewHolder {
		public LoaderImageView userImg;
		public Button user;
		public TextView newDoumail;
		public TextView title;
		public TextView time;
		public Button detail;
		public Button delete;
	}
}
