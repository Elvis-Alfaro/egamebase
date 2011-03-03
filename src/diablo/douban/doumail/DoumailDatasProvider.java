package diablo.douban.doumail;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.common.HomepageActivity;
import diablo.douban.common.IDoubanDataProvider;
import diablo.douban.common.LoaderImageView;

public class DoumailDatasProvider implements IDoubanDataProvider {

	private List<String> iconList;
	private DoubanAccessor douban;
	private ListActivity activity;
	private static boolean inbox = true;
	private int length = 10;

	public DoumailDatasProvider(DoubanAccessor douban, ListActivity activity){
		this.douban = douban;
		this.activity = activity;
		iconList = new ArrayList<String>();
	}

	
	public ListAdapter getDatas(int start) {
		List<Doumail> doumailList = null;
		if (inbox) {
			doumailList = douban.getDoumailList("INBOX", false, start, length);
		} else {
			doumailList = douban.getDoumailList("OUTBOX", false, start, length);
		}

		for (Doumail b : doumailList) {
			if (b.getFrom() != null && iconList.contains(b.getFrom().getIcon())) {
				iconList.add(b.getFrom().getIcon());
			}
		}
		LoaderImageView.loadDatasIntoMap(iconList);

		return new DoumailAdapter(activity, doumailList, inbox);
	}

	
	public View getFootView() {
		RelativeLayout view = new RelativeLayout(activity);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); // Verbose!
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		final Button btn1 = new Button(activity);
		btn1.setId(9999);
		btn1.setBackgroundResource(R.drawable.orange_btn);
		if(inbox){
			btn1.setText("转到发件箱");
		}else{
			btn1.setText("转到收件箱"); 
		}
		btn1.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				((HomepageActivity)activity).removeExtraView();
				inbox = !inbox;				
				activity.showDialog(0);				
				btn1.setEnabled(false);
			}
		});
		view.addView(btn1, lp);
		
		return view;
	}

	
	public View getHeaderView() {
		
		return null;
	}

	
	public String getPaginatorText() {
		return inbox ? "收件箱" : "发件箱";
	}

}
