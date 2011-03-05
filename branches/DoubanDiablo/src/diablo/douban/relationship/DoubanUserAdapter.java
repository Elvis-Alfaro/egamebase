package diablo.douban.relationship;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.common.LoaderImageView;
import diablo.douban.doumail.ComposeDoumailActivity;
import diablo.douban.user.UserDetailActivity;

public class DoubanUserAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DoubanUser> mData;
	private Activity activity;
	public DoubanUserAdapter(Activity activity, List<DoubanUser> mData) {
		this.activity = activity;
		this.mInflater = LayoutInflater.from(activity);
		this.mData = mData;
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

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(
					R.layout.relationship_userlist_item, null);

			holder.img = (LoaderImageView) convertView
					.findViewById(R.id.loaderImageView);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			holder.viewBtn = (Button) convertView.findViewById(R.id.view_btn);
			holder.doumailBtn = (Button)convertView.findViewById(R.id.doumail_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final DoubanUser user = mData.get(position);
		holder.img.setImageDrawable(user.getIcon());
		holder.title.setText(user.getTitle());
		if(user.getLocation() != null){
			holder.info.setText("(" + user.getLocation() + ")");
		}
			
		
		holder.viewBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity, UserDetailActivity.class);				
				intent.putExtra("user", user);
				activity.startActivity(intent);
			}
		});
		
		holder.doumailBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(activity, ComposeDoumailActivity.class);
				
				Doumail mail = new Doumail();
				mail.setFrom(DoubanAccessor.getInstance().getMe());
				mail.setTo(user);
				intent.putExtra(ComposeDoumailActivity.DOUMAIL, mail);
				activity.startActivity(intent);
			}
		});

		return convertView;

	}

	public void showInfo(int position) {
		Log.i("DoubanDiablo", mData.get(position).toString());
	}

}

final class ViewHolder {
	public LoaderImageView img;
	public TextView title;
	public TextView info;
	public Button viewBtn;
	public Button doumailBtn;
}
