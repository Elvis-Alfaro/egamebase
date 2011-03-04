package diablo.douban.glance;

import android.R.color;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.R.id;
import diablo.douban.R.layout;
import diablo.douban.accessor.pojo.ReviewItem;

public class ItemContentActivity extends Activity{
	private TextView text;
	private TextView head;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glance_item_content);
		
		ReviewItem content = (ReviewItem)getIntent().getSerializableExtra("content");
		
		text = (TextView)findViewById(R.id.content_text);
		text.setText(content.getDescription());	
		this.setTitle(content.getTitle());
		//this.setTitleColor(color.primary_text_light);		
		
		head = (TextView)findViewById(R.id.content_title_text);
		head.setMovementMethod(LinkMovementMethod.getInstance());
		head.setText(android.text.Html.fromHtml(content.getTitle()));
	}
}
