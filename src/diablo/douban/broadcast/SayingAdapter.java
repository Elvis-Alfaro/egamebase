package diablo.douban.broadcast;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import diablo.douban.R;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.common.LoaderImageView;

public class SayingAdapter  extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DoubanBroadcast> mData;
	private SimpleDateFormat format;
	private Activity activity;
	
	public SayingAdapter(Activity activity, List<DoubanBroadcast> mData) {
		this.activity = activity;
		this.mInflater = LayoutInflater.from(activity);
		this.mData = mData;
		format = new SimpleDateFormat("MM-dd HH:mm:ss");
	}

	public int getCount() {
		return mData.size();
	}

	public Object getItem(int position) {		
		return null;
	}

	public long getItemId(int position) {		
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {		
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(
					R.layout.saying_broadcast_item, null);

			holder.userImg = (LoaderImageView) convertView
					.findViewById(R.id.saying_thumbnail);
			holder.user = (Button) convertView.findViewById(R.id.saying_user);
			//holder.info = (TextView) convertView.findViewById(R.id.saying_info);
			//holder.info.setVisibility(View.GONE);
			holder.replyText = (TextView) convertView.findViewById(R.id.saying_reply);
			holder.reply = (Button) convertView.findViewById(R.id.saying_reply_btn);
			holder.detail = (WebView)convertView.findViewById(R.id.saying_detail);
			holder.time = (TextView) convertView.findViewById(R.id.saying_time);
			//holder.detailImg = (LoaderImageView)convertView.findViewById(R.id.saying_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final DoubanBroadcast bd = mData.get(position);
		holder.userImg.setImageDrawable(bd.getUser().getIcon());
		holder.user.setText(bd.getUser().getTitle());
		if(bd.getTime() != null){
			holder.time.setText(format.format(bd.getTime()));
		}
		String title = bd.getTitle();
		
		String comment_count = bd.getMap().get("comments_count");
		
		String category = bd.getCategory();
		Log.i("DoubanDiablo", "comment_count: " + comment_count + ", category: " + category);
		if(category!= null && category.equals("saying")){
			title = "说：" + title;
			holder.reply.setVisibility(View.VISIBLE);
			if(comment_count != null && Integer.parseInt(comment_count) > 0){
				holder.reply.setText(comment_count + "回应");
			}else{
				holder.reply.setText("回应");
			}
			holder.replyText.setVisibility(View.GONE);
		}else if(category!= null && bd.getCategory().equals("recommendation")){
			if(bd.getMap().get("comment") != null && !bd.getMap().get("comment").equals("")){				
				holder.replyText.setText(bd.getMap().get("comment").trim());	
				holder.replyText.setVisibility(View.VISIBLE);				
			}else{
				holder.replyText.setVisibility(View.GONE);	
			}
			if(comment_count!=null && Integer.parseInt(comment_count) > 0){
				holder.reply.setText(comment_count + "回应");
			}else{
				holder.reply.setText("回应");
			}
			holder.reply.setVisibility(View.VISIBLE);
		}else{
			holder.replyText.setVisibility(View.GONE);
			holder.reply.setVisibility(View.GONE);
		}
		//holder.info.setText(title);
		holder.detail.setBackgroundColor(0);
		holder.detail.loadDataWithBaseURL (null, bd.getContent(), "text/html", "utf-8",null);
		//holder.detail.setVisibility(View.GONE);
		/*if(bd.getMap().get("image") != null){  
			holder.detailImg.setImageDrawable(bd.getMap().get("image"));
		}*/
		
		holder.user.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse(bd.getUser().getAlternate());  
				Intent intent = new Intent(Intent.ACTION_VIEW, uri );
				activity.startActivity(intent);
			}
		});
		
		holder.reply.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("DoubanDiablo", bd.getId());
				Intent intent = new Intent(activity, CommentsActivity.class);
				intent.putExtra("DATA", bd);
				activity.startActivity(intent);
				//LinearLayout l = (LinearLayout)v.getParent().getParent();
				//l.addView(new EditText(activity));
				
				//ListView listView = new ListView(activity);
			
				//listView.addFooterView(new EditText(activity));
			}
		});

		return convertView;
	}

	
}

final class ViewHolder {
	public LoaderImageView userImg;
	public LoaderImageView detailImg;
	public Button user;
	public TextView info;
	public TextView time;
	public WebView detail;
	public TextView replyText;
	public Button reply;
}
