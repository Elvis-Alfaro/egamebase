package diablo.douban.doumail;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.common.LoaderImageView;

public class ComposeDoumailActivity extends Activity{
	public static String DOUMAIL = "dou_mail";
	
	private Doumail mail;
	
	TextView currentUser;
	
	
	List<DoubanAuthData> dat;
	CharSequence[] items;
	
	private void changeUser(int index){		
		currentUser.setText("From: " + dat.get(index).getUsername());
		DoubanAccessor.init(dat.get(index).getToken(), dat.get(index).getSecret());		
	}
	
	String verifyToken;
	private String postDoumail(String receiver, String title, String content, String token, String verifyCode){
		DoubanAccessor douban = DoubanAccessor.getInstance();
		
		return douban.postDoumailWithToken(receiver, title, content, token, verifyCode.equals("")?"1":verifyCode);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doumail_form);
		
		mail = (Doumail) getIntent().getSerializableExtra(DOUMAIL);
		if(mail.getTitle()!= null){
			this.setTitle("Re：" + mail.getTitle());
		}
		//View headView = HeadViewInflateHelper.inflateMe(this, mail.getFrom());
		//this.getWindow().addContentView(headView,  new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		final EditText title = (EditText)findViewById(R.id.doumailTitle);
		final EditText content = (EditText)findViewById(R.id.doumailContent);
		final EditText verifyText = (EditText)findViewById(R.id.doumail_verify_text);
		final LoaderImageView verifyImg = (LoaderImageView)findViewById(R.id.doumail_verify_img);
		currentUser = (TextView)findViewById(R.id.currentUser);		
		TextView toText = (TextView)findViewById(R.id.to_user);
		LoaderImageView toImg = (LoaderImageView)findViewById(R.id.to_user_img);
		Button changeUser = (Button)findViewById(R.id.changeUser);
		Button submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				v.setEnabled(false);
				
				String titleValue = title.getText().toString();
				String contentValue = content.getText().toString();
				String verifyTextValue = verifyText.getText().toString();
				String response = postDoumail(mail.getTo().getId(), titleValue, contentValue, verifyToken, verifyTextValue);
				
				Log.i("DoubanDiablo", response);
				if(response.equals("ok")){
					Toast.makeText(getApplicationContext(), "发送成功！", 1).show();
										
					ComposeDoumailActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "发送失败！需要验证码。", 1).show();
					v.setEnabled(true);
					
					String[] respArr = response.split("&amp;");					
					String captcha_url = null;
					
					for(String s : respArr){
						String[] tmp = s.split("=");
						if(tmp[0].equals("captcha_token")){
							verifyToken = tmp[1];
						}else if(tmp[0].equals("captcha_url")){
							captcha_url = tmp[1] + "=" + tmp[2];
						}
					}					
					verifyImg.setImageDrawable(captcha_url);
					verifyText.setVisibility(View.VISIBLE);
					verifyImg.setVisibility(View.VISIBLE);
				}
			}
		});
		
		dat = DoubanAuthData.getAuthData();
		items = new CharSequence[dat.size()];		
		for(int i=0; i<dat.size(); i++){
			items[i] = dat.get(i).getUsername();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ComposeDoumailActivity.this);
		builder.setTitle("请选择已授权用户");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		    	changeUser(item);
		        dialog.dismiss();
		    }
		});
		final AlertDialog alert  = builder.create();
		
		changeUser.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {				
				alert.show();
			}			
		});
		
		if(mail != null){
			if(mail.getTo() != null){
				toText.setText(toText.getText() + mail.getTo().getTitle());
				toImg.setImageDrawable(mail.getTo().getIcon());
			}
			if(mail.getFrom() != null){
				currentUser.setText("From: " + mail.getFrom().getTitle());
			}
		}
	}
}
