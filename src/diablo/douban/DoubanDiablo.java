package diablo.douban;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import diablo.douban.accessor.DiabloDatabase;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.accessor.pojo.Doumail;
import diablo.douban.broadcast.SayingActivity;
import diablo.douban.doumail.ComposeDoumailActivity;
import diablo.douban.doumail.DoumailListActivity;

public class DoubanDiablo extends Activity {
	private static final String TAG = "DoubanDiablo";

	public static String accessToken;
	public static String tokenSecret;
	private DoubanAuthData curAuthDat;
	public static DiabloDatabase database;
	// public static DoubanUser me;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(database == null){
			database = new DiabloDatabase(this);
		}
		List<DoubanAuthData> authData = database.queryAll();
		DoubanAuthData.setAuthData(authData);

		SharedPreferences pref = getSharedPreferences("token",
				MODE_WORLD_READABLE);

		String curUid = pref.getString("currentUserid", "NaN");
		Log.i("DoubanDiablo", curUid);
		if (!curUid.equals("NaN")) {
			curAuthDat = DoubanAuthData.getByUid(curUid);
			DoubanAuthData.setCurrent(curAuthDat);
		}

		if (curAuthDat != null) {

			accessToken = curAuthDat.getToken();
			tokenSecret = curAuthDat.getSecret();
			
			Log.i(TAG, accessToken + ",  " + tokenSecret);
			DoubanAccessor.init(accessToken, tokenSecret);
//			DoubanUser me = DoubanAccessor.getInstance().getMe();
//			
//			Intent intent = new Intent(this, ComposeDoumailActivity.class);
//			Doumail mail = new Doumail();
//			mail.setTo(me);
//			mail.setFrom(me);
//			intent.putExtra(ComposeDoumailActivity.DOUMAIL, mail);
			Intent intent = new Intent(this, SayingActivity.class);
			
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			//Intent intent = new Intent(this, ComposeDoumailActivity.class);
			//startActivity(intent);
		}		
	}


	@Override
	public void onDestroy(){
		super.onDestroy();
		database.close();
		Log.i(TAG, "DoubanDiablo.Activity onDestroy");
	}
}