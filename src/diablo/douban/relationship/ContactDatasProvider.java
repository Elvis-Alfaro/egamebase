package diablo.douban.relationship;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.ListAdapter;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.common.IDoubanDataProvider;
import diablo.douban.common.LoaderImageView;

public class ContactDatasProvider implements IDoubanDataProvider{

	private List<String> iconList;
	private DoubanAccessor douban;
	private Activity activity;
	
	public ContactDatasProvider(DoubanAccessor douban, Activity activity) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
	}
	
	@Override
	public ListAdapter getDatas(int start, int length) {
		List<DoubanUser> friendList = douban.getPeopleFriends(null, start, length);
		for(DoubanUser b : friendList){
			if(b.getIcon()!=null){
				LoaderImageView.loadDataIntoMap(b.getIcon());
			}
		}
		
		return new DoubanUserAdapter(activity, friendList);	
	}

	@Override
	public View getFootView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPaginatorText() {
		return "我关注的人";
	}

}
