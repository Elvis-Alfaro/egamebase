package diablo.douban.broadcast;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import diablo.douban.DoubanDiablo;
import diablo.douban.R;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.accessor.pojo.DoubanPhoto;
import diablo.douban.common.LoaderImageView;
import diablo.douban.user.UserDetailActivity;

public class SayingAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DoubanBroadcast> mData;
	private SimpleDateFormat format;
	private Activity activity;

	// private OnReplyClickListener listener;

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

			convertView = mInflater.inflate(R.layout.saying_broadcast_item,
					null);
			((LinearLayout) (convertView.findViewById(R.id.list_item)))
					.setBackgroundResource(DoubanDiablo.currentListItemBgResourceId);
			holder.userImg = (LoaderImageView) convertView
					.findViewById(R.id.saying_thumbnail);
			holder.user = (Button) convertView.findViewById(R.id.saying_user);
			// holder.info = (TextView)
			// convertView.findViewById(R.id.saying_info);
			// holder.info.setVisibility(View.GONE);
			holder.replyText = (TextView) convertView
					.findViewById(R.id.saying_reply);
			holder.reply = (Button) convertView
					.findViewById(R.id.saying_reply_btn);
			holder.detail = (TextView) convertView
					.findViewById(R.id.saying_detail);
			holder.time = (TextView) convertView.findViewById(R.id.saying_time);
			holder.detailImg = (LoaderImageView) convertView
					.findViewById(R.id.relate_image);
			holder.view_detail_btn = (Button) convertView
				.findViewById(R.id.view_detail_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final DoubanBroadcast bd = mData.get(position);
		holder.userImg.setImageDrawable(bd.getUser().getIcon(), true);
		holder.user.setText(bd.getUser().getTitle());
		if (bd.getTime() != null) {
			holder.time.setText(format.format(bd.getTime()));
		}
		String title = bd.getTitle();

		String comment_count = (String)bd.getMap().get("comments_count");

		String category = bd.getCategory();
		// Log.i("DoubanDiablo", "comment_count: " + comment_count +
		// ", category: " + category);
		if (category != null) {
			if (category.equals("saying")) {
				title = "说：" + title;
				holder.reply.setVisibility(View.VISIBLE);
				if (comment_count != null
						&& Integer.parseInt(comment_count) > 0) {
					holder.reply.setText(comment_count + "回应");
				} else {
					holder.reply.setText("回应");
				}
				holder.replyText.setVisibility(View.GONE);
				holder.view_detail_btn.setVisibility(View.GONE);
			} else if (category.equals("recommendation")) {
				if (bd.getMap().get("comment") != null
						&& !bd.getMap().get("comment").equals("")) {
					holder.replyText.setText(((String)bd.getMap().get("comment")).trim());
					holder.replyText.setVisibility(View.VISIBLE);
				} else {
					holder.replyText.setVisibility(View.GONE);
				}
				if (comment_count != null
						&& Integer.parseInt(comment_count) > 0) {
					holder.reply.setText(comment_count + "回应");
				} else {
					holder.reply.setText("回应");
				}				
				holder.reply.setVisibility(View.VISIBLE);
				
				if (bd.getMap().get("category") != null ){
					if(bd.getMap().get("category").equals("photo")){
						holder.view_detail_btn.setText("照片详情");
						holder.view_detail_btn.setVisibility(View.VISIBLE);
					}else if(bd.getMap().get("category").equals("photo_album")){
						holder.view_detail_btn.setText("相册详情");
						holder.view_detail_btn.setVisibility(View.VISIBLE);
					} else{
						holder.view_detail_btn.setVisibility(View.GONE);
					}
				}
			} else if(category.equals("photo")){
				holder.view_detail_btn.setVisibility(View.VISIBLE);
				holder.view_detail_btn.setText("照片详情");
				holder.reply.setVisibility(View.GONE);
				holder.replyText.setVisibility(View.GONE);
			}else{
				holder.replyText.setVisibility(View.GONE);
				holder.reply.setVisibility(View.GONE);
				holder.view_detail_btn.setVisibility(View.GONE);
			}
		} else {
			if (bd.getMap().get("category") != null && bd.getMap().get("category").equals("photo")){				
				holder.view_detail_btn.setText("照片详情");
				holder.view_detail_btn.setVisibility(View.VISIBLE);				
			}else{			
				holder.view_detail_btn.setVisibility(View.GONE);
			}
			holder.replyText.setVisibility(View.GONE);
			holder.reply.setVisibility(View.GONE);
		}
		// holder.info.setText(title);
		// holder.detail.setBackgroundColor(0);
		// holder.detail.getSettings().setDefaultFontSize(12);
		// holder.detail.loadDataWithBaseURL (null, bd.getContent(),
		// "text/html", "utf-8",null);
		holder.detail.setMovementMethod(LinkMovementMethod.getInstance());
		holder.detail.setText(android.text.Html.fromHtml(bd.getContent()));

		// holder.detail.setVisibility(View.GONE);
		if (bd.getMap().get("image") != null) {
			holder.detailImg.setImageDrawable((String)bd.getMap().get("image"), false);
			holder.detailImg.setVisibility(View.VISIBLE);
		} else {
			holder.detailImg.setVisibility(View.GONE);
		}

		holder.view_detail_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Object o = bd.getMap().get("object");
				if(o != null){
					if(o instanceof DoubanPhoto){
						Log.i("DoubanDiablo", o.toString());
					}
				}
			}
		});
		
		holder.user.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/*
				 * Uri uri = Uri.parse(bd.getUser().getAlternate()); Intent
				 * intent = new Intent(Intent.ACTION_VIEW, uri );
				 * activity.startActivity(intent);
				 */
				Intent intent = new Intent(activity, UserDetailActivity.class);
				intent.putExtra("user", bd.getUser());
				activity.startActivity(intent);
			}
		});

		holder.reply.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Log.i("DoubanDiablo", bd.getId());
				Intent intent = new Intent(activity, CommentsActivity.class);
				intent.putExtra("DATA", bd);
				activity.startActivity(intent);
				// if(listener != null){
				// listener.onReply(bd);
				// }
			}
		});

		return convertView;
	}

	public interface OnReplyClickListener {
		public void onReply(DoubanBroadcast bd);
	}

}

final class ViewHolder {
	public LoaderImageView userImg;
	public LoaderImageView detailImg;
	public Button user;
	public TextView info;
	public TextView time;
	public TextView detail;
	public Button view_detail_btn;
	public TextView replyText;
	public Button reply;
}
