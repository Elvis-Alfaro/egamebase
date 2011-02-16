package diablo.douban.glance;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import diablo.douban.R;
import diablo.douban.R.layout;
import diablo.douban.accessor.pojo.ReviewItem;

public abstract class AbstractReviewActivity extends ListActivity {
	protected abstract void setFeedUrlAndTag();
	static final int PROGRESS_DIALOG = 0;
	String feedUrl = "http://www.douban.com/feed/review/book";
	String tag = "MovieReview ";
	Button button;
	ProgressThread progressThread;
	ProgressDialog progressDialog;

	ArrayList<String> reviews = new ArrayList<String>();
	ArrayList<ReviewItem> contents = new ArrayList<ReviewItem>();
	ListActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.glance_list_item,
				reviews));
		activity = this;
		showDialog(PROGRESS_DIALOG);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(AbstractReviewActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressThread = new ProgressThread(handler);
			progressThread.start();
			return progressDialog;
		default:
			return null;
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			progressDialog.setProgress(total);
			if (total >= 100) {
				dismissDialog(PROGRESS_DIALOG);
				progressThread.setState(ProgressThread.STATE_DONE);
				
				setListAdapter(new ArrayAdapter<String>(activity, R.layout.glance_list_item,
						reviews));				
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);

				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// When clicked, show a toast with the TextView text
						//Toast.makeText(getApplicationContext(),
						//		((TextView) view).getText(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(AbstractReviewActivity.this, ItemContentActivity.class);
						intent.putExtra("content", contents.get(position));
						//intent.putExtra("title", reviews.get(position));
						startActivity(intent);
					}
				});
			}
		}
	};

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		int mState;
		int total;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			mState = STATE_RUNNING;
			total = 0;
			
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpGet get = new HttpGet(
						feedUrl);
				HttpResponse response;

				response = httpclient.execute(get);

				HttpEntity entity = response.getEntity();
				// System.out.println(entity.getContentType());

				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory
						.newDocumentBuilder();
				Document doc = documentBuilder.parse(entity.getContent());
				NodeList list = doc.getElementsByTagName("item");
				for (int i = 0; i < list.getLength(); i++) {				
					Node n = list.item(i);
					NodeList clist = n.getChildNodes();
					ReviewItem review = new ReviewItem();
					review.setTitle(clist.item(1).getFirstChild().getNodeValue());
					review.setLink(clist.item(3).getFirstChild().getNodeValue());
					review.setDescription(clist.item(5).getFirstChild().getNodeValue());					
					review.setCreator(clist.item(9).getFirstChild().getNodeValue());
					review.setPubDate(clist.item(11).getFirstChild().getNodeValue());
					reviews.add(review.getTitle());
					
					contents.add(review);
				}
				total = 100;
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("total", total);
				msg.setData(b);
				mHandler.sendMessage(msg);
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.e(tag, e.toString(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(tag, e.toString(), e);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				Log.e(tag, e.toString(), e);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.e(tag, e.toString(), e);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				Log.e(tag, e.toString(), e);
			}			
		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}
	}
}
