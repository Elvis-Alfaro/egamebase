package diablo.douban.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanNote;

public class WriteNoteActivity extends Activity{
	private static final String[] searchClassify = new String[]{"所有人可见", "仅朋友可见", "仅自己可见"};
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.doumail_form);
		DoubanNote data = (DoubanNote)getIntent().getSerializableExtra("content");
		
		findViewById(R.id.doumail_from).setVisibility(View.GONE);
		findViewById(R.id.doumail_to).setVisibility(View.GONE);
		//View headView = HeadViewInflateHelper.inflateMe(this, mail.getFrom());
		//this.getWindow().addContentView(headView,  new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		final EditText title = (EditText)findViewById(R.id.doumailTitle);
		final EditText content = (EditText)findViewById(R.id.doumailContent);
		if(data != null){
			title.setText(data.getTitle());
			content.setText(data.getContent());
		}
		content.setLines(5);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("日志开放等级：");
		builder.setSingleChoiceItems(searchClassify, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		    	String privacy = "";
		    	if(item == 0){
		    		privacy = "public";
		    	}else if(item == 1){
		    		privacy = "friend";
		    	}else{
		    		privacy = "private";
		    	}
		    	DoubanNote note = DoubanAccessor.getInstance().postNote(title.getText().toString(), content.getText().toString(), privacy, true);
		        dialog.dismiss();
		        if(note != null){
		        	Intent intent = new Intent(WriteNoteActivity.this, NoteDetailActivity.class);
		        	intent.putExtra("content", note);
		        	WriteNoteActivity.this.startActivity(intent);
		        }
		        WriteNoteActivity.this.finish();
		    }
		});
		final AlertDialog alert  = builder.create();	
		
		
		Button submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				alert.show();				
			}
		});
	}
}
