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
	private int length = 15;
	private DoubanUser user;
	public ContactDatasProvider(DoubanAccessor douban, Activity activity, DoubanUser user) {
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
		this.user = user;
	}
	
	
	public ListAdapter getDatas(int start) {
		List<DoubanUser> friendList = douban.getPeopleFriends(user, start, length);
		for(DoubanUser b : friendList){
			if(b.getIcon()!=null){
				LoaderImageView.loadDataIntoMap(b.getIcon());
			}
		}
		
		return new DoubanUserAdapter(activity, friendList);	
	}

	
	public View getFootView() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public View getHeaderView() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle(){
		if(user == null || user.getTitle().equals(douban.getMe().getTitle())){
			return "我关注的人";			
		}else{
			return user.getTitle() + "关注的人";
		}
	}
	
	public String getPaginatorText() {
		return "我关注的人";
	}

}
