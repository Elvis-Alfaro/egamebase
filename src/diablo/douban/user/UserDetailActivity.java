package diablo.douban.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.common.AbstractProgressActivity;
import diablo.douban.common.HomepageActivity;
import diablo.douban.common.LoaderImageView;
import diablo.douban.doumail.ComposeDoumailActivity;

public class UserDetailActivity extends AbstractProgressActivity{
	private DoubanUser user;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_detail);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		user = (DoubanUser)getIntent().getSerializableExtra("user");
		showDialog(0);
	}

	protected void onProgressComplete() {
		LoaderImageView thumbnail = (LoaderImageView)findViewById(R.id.thumbnail);
		TextView id = (TextView)findViewById(R.id.user_id);
		TextView name = (TextView)findViewById(R.id.user_name);
		TextView location = (TextView)findViewById(R.id.location);
		
		
		thumbnail.setImageDrawable(user.getIcon());
		id.setText("id：" + user.getUid());
		name.setText("名称：" + user.getTitle());
		if(user.getLocation() != null){
			location.setText("所在地：" + user.getLocation());
		}else{
			location.setVisibility(View.GONE);
		}
		
		TextView alternate = (TextView)findViewById(R.id.alternate);
		alternate.setMovementMethod(LinkMovementMethod.getInstance());
		if(user.getAlternate() != null){
			alternate.setText(android.text.Html.fromHtml("<a href='" +  user.getAlternate() + "'>访问TA的豆瓣主页</a>"));
		}else{
			alternate.setVisibility(View.GONE);
		}
		
		TextView homepage = (TextView)findViewById(R.id.homepage);
		homepage.setMovementMethod(LinkMovementMethod.getInstance());
		if(user.getHomepage() != null){
			homepage.setText(android.text.Html.fromHtml("<a href='" +  user.getAlternate() + "'>访问TA的个人主页</a>"));
		}else{
			homepage.setVisibility(View.GONE);
		}
		
		TextView content_text = (TextView)findViewById(R.id.content_text);
		//content_text.setMovementMethod(LinkMovementMethod.getInstance());
		content_text.setText(user.getContent());
		
		Button doumail = (Button)findViewById(R.id.doumail);
		doumail.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Doumail mail = new Doumail();
				mail.setFrom(DoubanAccessor.getInstance().getMe());
				mail.setTo(user);
				
				Intent intent = new Intent(activity, ComposeDoumailActivity.class);				
				intent.putExtra(ComposeDoumailActivity.DOUMAIL, mail);
				activity.startActivity(intent);
			}
		});
		Button note = (Button)findViewById(R.id.note);
		note.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				HomepageActivity.reloadData(user, HomepageActivity.MenuType.note);
				UserDetailActivity.this.finish();
			}
		});
	}

	protected void onProgressLoadData() {
		user = DoubanAccessor.getInstance().getPeopleByUrl(user.getId());
	}

}
