package diablo.douban.relationship;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import diablo.douban.R;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.common.LoaderImageView;

public class DoubanUserAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<DoubanUser> mData;

	public DoubanUserAdapter(Context context, List<DoubanUser> mData) {
		this.mInflater = LayoutInflater.from(context);
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
		// TODO Auto-generated method stub
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
		DoubanUser user = mData.get(position);
		holder.img.setImageDrawable(user.getIcon());
		holder.title.setText(user.getTitle());
		if(user.getLocation() != null){
			holder.info.setText("(" + user.getLocation() + ")");
		}
			
		
		holder.viewBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showInfo(position);
			}
		});
		
		holder.doumailBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showInfo(position);
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
