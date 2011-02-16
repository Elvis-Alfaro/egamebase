package diablo.douban;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.glance.ReviewTabActivity;

public class UserAuthorizationActivity extends Activity {
	private static final String DOUBAN_DOMAIN = "www.douban.com";
	public static final String AUTH_URL = "user_authorization_url";
	protected static final String COOKIE_STR = "cookies";
	private WebView webView;
	private Button finishBtn;
	private Button cancleBtn;
	private Button reloadBtn;
	private String authURL;
	private String cookieString;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.auth);
		
		context = this;
		
		authURL = getIntent().getStringExtra(AUTH_URL);
		cookieString = getIntent().getStringExtra(COOKIE_STR);
		Log.i("tag", cookieString);
		webView = (WebView) findViewById(R.id.auth_view);
		
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		
		//cookieManager.removeSessionCookie();
		cookieManager.setCookie(DOUBAN_DOMAIN, cookieString);
		cookieManager.setAcceptCookie(true);
		//Log.i("Douban", "cookie: " +  cookieManager.getCookie(DOUBAN_DOMAIN));
		CookieSyncManager.getInstance().sync();


		//cookieManager = CookieManager.getInstance();
		
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			public void onPageFinished(WebView view, String url){
				Log.i("tag", url);
				CookieManager cookieManager = CookieManager.getInstance();
				Log.i("Douban", "cookie: " + cookieManager.getCookie("www.douban.com"));
			}
		});

		finishBtn = (Button) findViewById(R.id.finish_btn);
		cancleBtn = (Button) findViewById(R.id.cancle_auth_btn);
		reloadBtn = (Button) findViewById(R.id.reload_btn);

		finishBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//DoubanAccessor.getInstance().saveAccessToken(getSharedPreferences("token", MODE_WORLD_WRITEABLE));
				DoubanAccessor.getInstance().saveAccessToken(context);
				Intent intent = new Intent(UserAuthorizationActivity.this, DoubanDiablo.class);
				startActivity(intent);
			}
		});
		cancleBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				webView.loadUrl("http://www.douban.com/");
				// Intent intent = new Intent(UserAuthorizationActivity.this,
				// DoubanDiablo.class);
				// startActivity(intent);
			}
		});

		reloadBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				// webView.loadUrl(authURL);
				Intent intent = new Intent(UserAuthorizationActivity.this,
						ReviewTabActivity.class);
				startActivity(intent);
			}
		});

		webView.loadUrl(authURL);
	}

}
