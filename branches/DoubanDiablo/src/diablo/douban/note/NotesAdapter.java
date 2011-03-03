package diablo.douban.note;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
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
import diablo.douban.accessor.pojo.DoubanNote;
import diablo.douban.broadcast.CommentsActivity;
import diablo.douban.common.LoaderImageView;

public class NotesAdapter  extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DoubanNote> mData;
	private SimpleDateFormat format;
	private Activity activity;
	//private OnReplyClickListener listener;
	
	public NotesAdapter(Activity activity, List<DoubanNote> mData) {
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
			((LinearLayout)(convertView.findViewById(R.id.list_item))).setBackgroundResource(DoubanDiablo.currentListItemBgResourceId);
			holder.userImg = (LoaderImageView) convertView
					.findViewById(R.id.saying_thumbnail);
			holder.title = (Button) convertView.findViewById(R.id.saying_user);
			//holder.info = (TextView) convertView.findViewById(R.id.saying_info);
			//holder.info.setVisibility(View.GONE);
			holder.replyText = (TextView) convertView.findViewById(R.id.saying_reply);
			holder.reply = (Button) convertView.findViewById(R.id.saying_reply_btn);
			holder.detail = (TextView)convertView.findViewById(R.id.saying_detail);
			holder.time = (TextView) convertView.findViewById(R.id.saying_time);
			//holder.detailImg = (LoaderImageView)convertView.findViewById(R.id.saying_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final DoubanNote note = mData.get(position);
		holder.userImg.setImageDrawable(note.getAuthor().getIcon());
		holder.title.setText(note.getTitle());
		if(note.getPublished() != null){
			holder.time.setText(format.format(note.getPublished()));
		}
		
		//Log.i("DoubanDiablo", "comment_count: " + comment_count + ", category: " + category);
		
		holder.detail.setMovementMethod(LinkMovementMethod.getInstance());
		holder.detail.setText(android.text.Html.fromHtml(note.getSummary()));
		
		//holder.detail.setVisibility(View.GONE);
		/*if(bd.getMap().get("image") != null){  
			holder.detailImg.setImageDrawable(bd.getMap().get("image"));
		}*/
		
		holder.title.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
		holder.reply.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});

		return convertView;
	}
	
	public interface OnReplyClickListener{
		public void onReply(DoubanBroadcast bd);
	}

	
}

final class ViewHolder {
	public LoaderImageView userImg;
	public LoaderImageView detailImg;
	public Button title;
	public TextView info;
	public TextView time;
	public TextView detail;
	public TextView replyText;
	public Button reply;
}
