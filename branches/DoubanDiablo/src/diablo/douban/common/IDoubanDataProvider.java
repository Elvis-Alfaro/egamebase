package diablo.douban.common;

import android.view.View;
import android.widget.ListAdapter;

public interface IDoubanDataProvider {
	
	public String getPaginatorText();
	
	public ListAdapter getDatas(int start);
	
	public View getHeaderView();
	
	public View getFootView();
	
}
