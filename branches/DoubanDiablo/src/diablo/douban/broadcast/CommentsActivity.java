package diablo.douban.broadcast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import diablo.douban.R;
import diablo.douban.accessor.DoubanAccessor;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.common.AbstractProgressListActivity;
import diablo.douban.common.LoaderImageView;

public class CommentsActivity extends AbstractProgressListActivity {	
	private SimpleAdapter adapter;

	private DoubanBroadcast curBd;
	
	private TextView paginatorTitle;
	private Button prePage, nextPage;
	private int start = 1, length = 20;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		curBd = (DoubanBroadcast) getIntent().getSerializableExtra("DATA");
		adapter = new SimpleAdapter(this, new ArrayList<Map<String, Object>>(),
				R.layout.comments_list_item,
				new String[] { "user", "content" }, new int[] { R.id.user,
						R.id.content });
		//this.setContentView(R.layout.listview);
		//getListView().addHeaderView(HeadViewInflateHelper.inflateMe(this, DoubanDiablo.curAuthDat.getUsername(), DoubanDiablo.curAuthDat.getIcon()));
		getListView().addHeaderView(inflaterHeaderView());
		
		
		
		
		View view = LayoutInflater.from(this).inflate(R.layout.header_paginator, null);
		paginatorTitle = ((TextView)view.findViewById(R.id.paginatorTitle));
		paginatorTitle.setText(R.string.reply);		
		
		prePage = (Button)view.findViewById(R.id.prePage);
		prePage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				start -= length; 	
				start = start < 1 ? 1 : start;
				showDialog(PROGRESS_DIALOG);
			}
		});
		
		nextPage = (Button)view.findViewById(R.id.nextPage);
		nextPage.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				start += length;
				
				showDialog(PROGRESS_DIALOG);
			}
		});
		getListView().addFooterView(view);
		
		view = LayoutInflater.from(this).inflate(R.layout.saying_form, null);
		final EditText content = (EditText)view.findViewById(R.id.sayingContent);
		
		Button submit = (Button)view.findViewById(R.id.iSay);	
		submit.setText(R.string.reply);
		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				DoubanAccessor.getInstance().postComment(curBd.getId() + "/comments", content.getText().toString());
				content.setText("");
				showDialog(PROGRESS_DIALOG);
			}
		});
		getListView().addFooterView(view);
		showDialog(this.PROGRESS_DIALOG);
	}
	
	private View inflaterHeaderView(){
		View view = LayoutInflater.from(this).inflate(
				R.layout.saying_broadcast_item, null);

		ViewHolder holder = new ViewHolder();
		holder.userImg = (LoaderImageView) view
				.findViewById(R.id.saying_thumbnail);
		holder.user = (Button) view.findViewById(R.id.saying_user);
		//holder.info = (TextView) view.findViewById(R.id.saying_info);
		holder.replyText = (TextView) view
				.findViewById(R.id.saying_reply);
		holder.reply = (Button) view.findViewById(R.id.saying_reply_btn);
		holder.reply.setVisibility(View.GONE);
		holder.replyText.setText(curBd.getMap().get("comment"));
		
		
		holder.detail = (TextView) view.findViewById(R.id.saying_detail);
		holder.detail.setBackgroundColor(0); 
		holder.time = (TextView) view.findViewById(R.id.saying_time);
		holder.time.setVisibility(View.GONE); 
		
		holder.userImg.setImageDrawable(curBd.getUser().getIcon());
		holder.user.setText(curBd.getUser().getTitle());
		
		String title = curBd.getTitle();
		
		
		String category = curBd.getCategory();
		if(category!= null && category.equals("saying")){
			title = "说：" + title;
			
		}
		holder.detail.setMovementMethod(LinkMovementMethod.getInstance());
		holder.detail.setText(android.text.Html.fromHtml(curBd.getContent()));
		//holder.detail.loadDataWithBaseURL (null, curBd.getContent(), "text/html", "utf-8",null);
		return view;
	}

	@Override
	protected void onProgressComplete() {
		setListAdapter(adapter);
		paginatorTitle.setText(R.string.my_contacts);
		int end = start + length;
		int totalResult = Integer.parseInt(DoubanAccessor.getInstance().totalResults);
		end = end > totalResult ? totalResult : end;
		String pageInfo = "(" + start + "-" + end +",共" + DoubanAccessor.getInstance().totalResults + "条回复.)";
		paginatorTitle.setText("广播回复" + pageInfo);
		if(start == 1){
			prePage.setEnabled(false);
		}else{
			prePage.setEnabled(true);
		}
		if(end == totalResult){
			nextPage.setEnabled(false);
		}else{
			nextPage.setEnabled(true);
		}
	}

	@Override
	protected void onProgressLoadData() {
		
		if(curBd.getCategory().equals("recommendation")){
			List<DoubanBroadcast> rlist = DoubanAccessor.getInstance().getBroadcast("RECOMMENDATION", curBd.getUser().getId(), 1, 10);
			curBd = DoubanAccessor.getInstance().getRecommendation(curBd, rlist);			
			if(curBd == null){				
				Toast.makeText(this, 
						"没找到对应推荐的评论！这个不能怪大菠萝！要怪就怪豆瓣API的设计人员！！" +
						"原因是API里同一条“广播”和对应的“推荐”的ID竟然是不同的！！" +
						"害你们增加了流量，我增加了代码量，而且还可能会出现这种找不到评论的错误！", 1).show();				
			}
		}
		
		if(curBd != null){
			List<DoubanBroadcast> list = DoubanAccessor.getInstance().getComments(curBd.getId(), start, length);
			List<Map<String, Object>> dat = new ArrayList<Map<String, Object>>();
			for (DoubanBroadcast d : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("user", d.getUser().getTitle() + ":  ");
				map.put("content", d.getContent());
				dat.add(map);
			}
			adapter = new SimpleAdapter(this, dat, R.layout.comments_list_item,
					new String[] { "user", "content" }, new int[] { R.id.user,
							R.id.content });
		}
	}

}
