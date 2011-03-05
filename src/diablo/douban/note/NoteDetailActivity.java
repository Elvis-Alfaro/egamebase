package diablo.douban.note;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanNote;

public class NoteDetailActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_content);
		
		final DoubanNote content = (DoubanNote)getIntent().getSerializableExtra("content");
		
		TextView text = (TextView)findViewById(R.id.content_text);
		text.setText(content.getContent());	
		//this.setTitle(content.getTitle());
		//this.setTitleColor(color.primary_text_light);		
		
		TextView head = (TextView)findViewById(R.id.content_title_text);
		head.setMovementMethod(LinkMovementMethod.getInstance());
		head.setText(android.text.Html.fromHtml(content.getTitle()));
		
		TextView publishTime = (TextView)findViewById(R.id.publish_time);
		publishTime.setText("发布时间：" + new SimpleDateFormat("MM-dd HH:mm:ss").format(content.getPublished()));
		
		TextView privacy = (TextView)findViewById(R.id.privacy);
		if(content.getPrivacy().equals("public")){
			privacy.setText("阅读权限：所有人可见");
		}else if(content.getPrivacy().equals("friend")){
			privacy.setText("阅读权限：仅朋友可见");
		}else{
			privacy.setText("阅读权限：仅自己可见"); 
		}
		
		Button edit = (Button)findViewById(R.id.edit);
		if((content.getAuthor().getId() != null && content.getAuthor().getId().equals(DoubanAuthData.getCurrent().getId()))
				|| (content.getAuthor().getUid() != null && content.getAuthor().getUid().equals(DoubanAuthData.getCurrent().getUserid()))){
			edit.setVisibility(View.VISIBLE);
			edit.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(NoteDetailActivity.this, WriteNoteActivity.class);
					intent.putExtra("content", content);
					NoteDetailActivity.this.startActivity(intent);
					NoteDetailActivity.this.finish();
				}
			});
		}
	}
}
