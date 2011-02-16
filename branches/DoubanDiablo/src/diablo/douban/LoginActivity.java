package diablo.douban;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import ssl.EasySSLSocketFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.glance.ReviewTabActivity;

public class LoginActivity extends Activity {

	private static final String TAG = "DoubanDiablo";
	private static final String DOMAIN = ".douban.com";
	public static DoubanAccessor douban;
	private Button lookAroundBtn;
	private Button oauthBtn;
	private EditText username, password;
	private DefaultHttpClient httpclient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//this.addContentView(LayoutInflater.from(this).inflate(R.layout.header_me, null), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		lookAroundBtn = (Button) findViewById(R.id.look_around_button);
		oauthBtn = (Button) findViewById(R.id.auth_button);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		httpclient = new DefaultHttpClient();
		lookAroundBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				// webView.loadUrl(authURL);
				Intent intent = new Intent(LoginActivity.this,
						ReviewTabActivity.class);
				startActivity(intent);
			}
		});
		oauthBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				try {
					httpclient = getHttpClient();
					HttpGet get = new HttpGet("http://www.douban.com");
					httpclient.execute(get);
					
					String u = username.getText().toString();
					String p = password.getText().toString();
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("form_email",	u));
					nameValuePairs.add(new BasicNameValuePair("form_password",p));

					HttpPost httppost = new HttpPost(
							"https://www.douban.com/accounts/login");

					httppost
							.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					//Log.i(TAG, EntityUtils.toString(response.getEntity()));
					List<Cookie> cookies = httpclient.getCookieStore()
							.getCookies();
					String cookieString = "";
					for(Cookie c : cookies){
						if(c.getName().equals("dbcl2"))
							cookieString += c.getName() + "=\"" + c.getValue() + "\";\n";
					}
					
					if (cookies.size() < 4) {
						Toast.makeText(getApplicationContext(), "登录失败！用户名密码错误。", 1).show();
					} else {
						Toast.makeText(getApplicationContext(), "登录成功，现在转到授权页面。", 1).show();
						douban = DoubanAccessor.getInstance();
						String authorizationURL = douban.getUserAuthorizationURL();
						Intent intent = new Intent(LoginActivity.this, UserAuthorizationActivity.class);
						intent.putExtra(UserAuthorizationActivity.AUTH_URL, authorizationURL);
						intent.putExtra(UserAuthorizationActivity.COOKIE_STR, cookieString);
						startActivity(intent);
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString(), e);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString(), e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, e.toString(), e);
				} 
			}
		});
	}

	private static DefaultHttpClient getHttpClient()
			throws ClientProtocolException {

		SchemeRegistry supportedSchemes = new SchemeRegistry();
		supportedSchemes.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", EasySSLSocketFactory
				.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf-8");
		params.setBooleanParameter("http.protocol.expect-continue", false);
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
				supportedSchemes);

		DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);
		return httpclient;
	}
}
