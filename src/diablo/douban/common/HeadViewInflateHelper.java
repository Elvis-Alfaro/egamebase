package diablo.douban.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import diablo.douban.R;
import diablo.douban.accessor.pojo.DoubanUser;

public class HeadViewInflateHelper {

	public static View inflateMe(Context context, String name, String icon){		
		View view = LayoutInflater.from(context).inflate(R.layout.header_me, null);
		LoaderImageView img = (LoaderImageView)view.findViewById(R.id.thumbnailMe);
		img.setImageDrawable(icon);
		TextView welcome = (TextView)view.findViewById(R.id.welcome);
		welcome.setText(welcome.getText() + name);
		return view;
	}
	
	public static View inflatePaginator(Context context, OnClickListener onPre, OnClickListener onNext){
		View view = LayoutInflater.from(context).inflate(R.layout.header_paginator, null);
		TextView paginatorTitle = ((TextView)view.findViewById(R.id.paginatorTitle));
		paginatorTitle.setText(R.string.broadcast);		
		
		Button prePage = (Button)view.findViewById(R.id.prePage);
		prePage.setOnClickListener(onPre);
		
		Button nextPage = (Button)view.findViewById(R.id.nextPage);
		nextPage.setOnClickListener(onNext);
		return view;
	}
	
}
