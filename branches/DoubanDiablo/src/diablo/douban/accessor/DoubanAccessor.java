package diablo.douban.accessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import diablo.douban.DoubanDiablo;
import diablo.douban.accessor.pojo.DoubanAuthData;
import diablo.douban.accessor.pojo.DoubanBroadcast;
import diablo.douban.accessor.pojo.DoubanUser;
import diablo.douban.accessor.pojo.Doumail;

//import android.util.Log;

public class DoubanAccessor {
	public static final String TAG = "Douban Accessor";
	public static final String ME = "http://api.douban.com/people/%40me";
	public static final String PEOPLE = "http://api.douban.com/people/{user_id}";
	public static final String PEOPLE_SEARCH = "http://api.douban.com/people";
	public static final String PEOPLE_FRIENDS = "http://api.douban.com/people/{userID}/friends";
	public static final String PEOPLE_CONTACTS = "http://api.douban.com/people/{userID}/contacts";
	public static final String BROADCASTS = "http://api.douban.com/people/{userID}/miniblog/contacts";
	public static final String RECOMMENDATIONS = "http://api.douban.com/people/{userID}/recommendations";
	public static final String BROADCASTS_COMMENTS = "http://api.douban.com/miniblog/{miniblogID}/comments";
	public static final String POST_DOUMAIL = "http://api.douban.com/doumails";
	public static final String SAYING = "http://api.douban.com/miniblog/saying";
	public static final String DOUMAIL_INBOX = "http://api.douban.com/doumail/inbox";
	public static final String DOUMAIL_OUTBOX = "http://api.douban.com/doumail/outbox";
	public String totalResults = "0", itemsPerPage = "0", startIndex = "0";

	public static String consumerKey = "01d679e540c966c405836dafc25c344c";
	public static String consumerSecret = "439d1fac8eb97aaa";
	private static String accessToken = "34b024f5eb982de79e106ee1c2c889b5";
	private static String tokenSecret = "12f24dc70180d3fc";
	private OAuthAccessor accessor;
	private OAuthClient oauthClient;
	private DocumentBuilder documentBuilder;

	public static DefaultHttpClient httpclient;
	private static String requestTokenURL = "http://www.douban.com/service/auth/request_token";
	private static String userAuthorizationURL = "http://www.douban.com/service/auth/authorize";
	private static String accessTokenURL = "http://www.douban.com/service/auth/access_token";

	private static DoubanAccessor douban;

	public static DoubanUser me;

	public static DoubanAccessor getInstance() {
		if (douban == null) {
			douban = new DoubanAccessor();
		}
		return douban;
	}

	private void init() {
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(
				requestTokenURL, userAuthorizationURL, accessTokenURL);

		OAuthConsumer oauthConsumer = new OAuthConsumer(null, consumerKey,
				consumerSecret, serviceProvider);

		oauthConsumer
				.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
		accessor = new OAuthAccessor(oauthConsumer);
		oauthClient = new OAuthClient(new HttpClient4());
	}

	private DoubanAccessor() {
		init();
	}

	public String getUserAuthorizationURL() {
		String authorizationURL = "";
		try {
			if (oauthClient == null) {
				init();
			}
			oauthClient.getRequestToken(accessor, "get");
			authorizationURL = OAuth.addParameters(
					accessor.consumer.serviceProvider.userAuthorizationURL,
					OAuth.OAUTH_TOKEN, accessor.requestToken);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OAuthException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// e.printStackTrace();
			Log.i("DoubanDiablo", "URISyntaxException");
		}
		return authorizationURL;
	}

	public boolean saveAccessToken(SharedPreferences pref) {
		try {
			oauthClient.getAccessToken(accessor, "get", null);
			accessToken = accessor.accessToken;
			tokenSecret = accessor.tokenSecret;
			if (accessToken != null && tokenSecret != null) {
				Log.i("OAUTH", accessToken + " : " + tokenSecret);

				SharedPreferences.Editor editor = pref.edit();
				editor.putString("accessToken", accessToken);
				editor.putString("tokenSecret", tokenSecret);
				editor.commit();

				me = getPeople(null);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OAuthException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Log.i("DoubanDiablo", "URISyntaxException");
		}
		return false;
	}

	public boolean saveAccessToken(Context context) {
		try {
			oauthClient.getAccessToken(accessor, "get", null);
			accessToken = accessor.accessToken;
			tokenSecret = accessor.tokenSecret;
			if (accessToken != null && tokenSecret != null) {
				Log.i("OAUTH", accessToken + " : " + tokenSecret);
				me = getPeople(null);

				DiabloDatabase db = DoubanDiablo.database;
				db.insert(me.getUid(), me.getTitle(), accessToken, tokenSecret,
						me.getIcon());

				SharedPreferences.Editor editor = context.getSharedPreferences(
						"token", Context.MODE_WORLD_WRITEABLE).edit();
				editor.putString("currentUserid", me.getUid());
				editor.commit();

				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OAuthException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Log.i("DoubanDiablo", "URISyntaxException");
		}
		return false;
	}

	public static void init(String accessToken, String tokenSecret) {
		douban = new DoubanAccessor(accessToken, tokenSecret);
		DoubanAuthData current = DoubanAuthData.getCurrent();
		if (current != null) {
			me = new DoubanUser();
			me.setIcon(current.getIcon());
			me.setUid(current.getUserid());
			me.setTitle(current.getUsername());
		}
	}

	// -----------------has accesstoken-------------
	private DoubanAccessor(String accessToken, String tokenSecret) {
		this.accessToken = accessToken;
		this.tokenSecret = tokenSecret;
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(
				requestTokenURL, userAuthorizationURL, accessTokenURL);
		OAuthConsumer oauthConsumer = new OAuthConsumer(null, consumerKey,
				consumerSecret, serviceProvider);
		oauthConsumer
				.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

		accessor = new OAuthAccessor(oauthConsumer);
		accessor.accessToken = accessToken;
		accessor.tokenSecret = tokenSecret;
		httpclient = new DefaultHttpClient();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private Document getDocument(String url) {
		HttpGet get = new HttpGet(url);
		get.setHeader("Authorization", getAuthHeader("GET", url));
		try {
			if (httpclient == null) {
				httpclient = new DefaultHttpClient();
			}
			if (documentBuilder == null) {
				documentBuilder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
			}
			HttpResponse response = httpclient.execute(get);
			Document doc = documentBuilder.parse(response.getEntity()
					.getContent());
			return doc;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return null;
	}

	public DoubanUser getMe() {
		if (me == null) {
			me = getPeople(null);
		}
		return me;
	}

	public DoubanUser getPeople(String userid) {
		String url = "";
		String authUrl = "";
		if (userid == null || userid.equals("")) {
			url = authUrl = ME;
		} else {
			authUrl = PEOPLE;
			url = PEOPLE.replace("{user_id}", userid);
		}
		Document doc = getDocument(url);

		// Log.v("DoubanDiablo", XmlUtil.xmlDocumentToString(doc));
		return parseUser(doc.getElementsByTagName("entry").item(0));

	}

	public List<DoubanUser> getPeopleFriends(String userid, int start, int max) {
		String url = "";
		String authUrl = "";
		if (userid == null || userid.equals("")) {
			if (me == null) {
				me = getPeople(null);
			}
			userid = me.getUid();
		}

		authUrl = PEOPLE_CONTACTS;
		url = PEOPLE_CONTACTS.replace("{userID}", userid) + "?start-index="
				+ start + "&max-results=" + max;
		Log.i("DoubanDiablo", " " + url);
		List<DoubanUser> friendList = new ArrayList<DoubanUser>();
		Document doc = getDocument(url);

		// Log.i("DoubanDiablo", " " + XmlUtil.xmlDocumentToString(doc));

		NodeList entryList = doc.getElementsByTagName("entry");
		Log.i("DoubanDiablo", " " + entryList.getLength());
		for (int i = 0; i < entryList.getLength(); i++) {
			friendList.add(parseUser(entryList.item(i)));
			// Log.i("DoubanDiablo",
			// parseUser(entryList.item(i).getChildNodes()).toString());
		}
		totalResults = doc.getElementsByTagName("openSearch:totalResults")
				.item(0).getFirstChild().getNodeValue();
		// Log.i("DoubanDiablo", "totalResults: " + totalResults);
		itemsPerPage = doc.getElementsByTagName("openSearch:itemsPerPage")
				.item(0).getFirstChild().getNodeValue();
		// Log.i("DoubanDiablo", "itemsPerPage: " + itemsPerPage);
		startIndex = doc.getElementsByTagName("openSearch:startIndex").item(0)
				.getFirstChild().getNodeValue();
		// Log.i("DoubanDiablo", "startIndex: " + startIndex);
		return friendList;
	}

	public void postComment(String url, String content) {
		HttpPost post = new HttpPost(url);
		post.addHeader("Authorization", getAuthHeader("POST", url));
		post.addHeader("Content-Type", "application/atom+xml");
		try {
			StringEntity myEntity = new StringEntity("<entry><content>"
					+ content + "</content></entry>", "utf-8");
			post.setEntity(myEntity);
			httpclient.execute(post);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void postSaying(String content) {
		HttpPost post = new HttpPost(SAYING);
		post.addHeader("Authorization", getAuthHeader("POST", SAYING));
		post.addHeader("Content-Type", "application/atom+xml");
		try {
			StringEntity myEntity = new StringEntity("<entry><content>"
					+ content + "</content></entry>", "utf-8");
			post.setEntity(myEntity);
			httpclient.execute(post);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public String postDoumail(String uid, String title, String content) {
		HttpPost post = new HttpPost(POST_DOUMAIL);
		post.addHeader("Authorization", getAuthHeader("POST", POST_DOUMAIL));
		post.addHeader("Content-Type", "application/atom+xml");
		try {
			String postData = "<?xml version='1.0' encoding='UTF-8'?>"
					+ "<entry xmlns='http://www.w3.org/2005/Atom' xmlns:db='http://www.douban.com/xmlns/' xmlns:gd='http://schemas.google.com/g/2005' xmlns:opensearch='http://a9.com/-/spec/opensearchrss/1.0/'>"
					+ "        <db:entity name='receiver'>"
					+ "               <uri>"
					+ uid
					+ "</uri>"
					+ "        </db:entity>"
					+ "        <content>"
					+ content
					+ "</content>"
					+ "       <title>"
					+ title
					+ "</title>"
					+ "        <db:attribute name='captcha_token'>1</db:attribute>"
					+ "        <db:attribute name='captcha_string'>1</db:attribute>"
					+ "</entry>";
			Log.i("DoubanDiablo", postData);
			StringEntity myEntity = new StringEntity(postData, "utf-8");

			post.setEntity(myEntity);
			HttpResponse resp = httpclient.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resp.getEntity().getContent()));		
			
			return reader.readLine().trim();
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	public String postDoumailWithToken(String uid, String title, String content,
			String token, String verify) {
		HttpPost post = new HttpPost(POST_DOUMAIL);
		post.addHeader("Authorization", getAuthHeader("POST", POST_DOUMAIL));
		post.addHeader("Content-Type", "application/atom+xml");
		try {
			String postData = "<?xml version='1.0' encoding='UTF-8'?>"
				+ "<entry xmlns='http://www.w3.org/2005/Atom' xmlns:db='http://www.douban.com/xmlns/' xmlns:gd='http://schemas.google.com/g/2005' xmlns:opensearch='http://a9.com/-/spec/opensearchrss/1.0/'>"
				+ "        <db:entity name='receiver'>"
				+ "               <uri>"
				+ uid
				+ "</uri>"
				+ "        </db:entity>"
				+ "        <content>"
				+ content
				+ "</content>"
				+ "       <title>"
				+ title
				+ "</title>"
				+ "        <db:attribute name='captcha_token'>" + token + "</db:attribute>"
				+ "        <db:attribute name='captcha_string'>" + verify + "</db:attribute>"
				+ "</entry>";
			StringEntity myEntity = new StringEntity(postData, "utf-8");			
			post.setEntity(myEntity);
			Log.i("DoubanDiablo", postData);
			HttpResponse resp = httpclient.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resp.getEntity().getContent()));		
			
			return reader.readLine().trim();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	public String deleteDoumail(String url){
		HttpDelete delete = new HttpDelete(url);
		delete.addHeader("Authorization", getAuthHeader("DELETE", url));
		try {
			HttpResponse resp = httpclient.execute(delete);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resp.getEntity().getContent()));		
			
			return reader.readLine().trim();
		}catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}
	public Doumail getDoumail(String url){
		Document doc = getDocument(url);
		NodeList entryList = doc.getElementsByTagName("entry");
		return parseDoumail(entryList.item(0));
	}
	
	public List<Doumail> getDoumailList(String box, boolean unread, int start,
			int max) {
		List<Doumail> list = new ArrayList<Doumail>();

		String url = null;
		if (box.equalsIgnoreCase("INBOX")) {
			url = DOUMAIL_INBOX;
			if (unread) {
				url += "/unread";
			}
		} else {
			url = DOUMAIL_OUTBOX;
		}
		url += "?start-index=" + start + "&max-results=" + max;
		Document doc = getDocument(url);
		NodeList entryList = doc.getElementsByTagName("entry");
		for (int i = 0; i < entryList.getLength(); i++) {
			list.add(parseDoumail(entryList.item(i)));
		}

		if (doc.getElementsByTagName("openSearch:totalResults").getLength() > 0) {
			totalResults = doc.getElementsByTagName("openSearch:totalResults")
					.item(0).getFirstChild().getNodeValue();
		}
		if (doc.getElementsByTagName("openSearch:itemsPerPage").getLength() > 0) {
			itemsPerPage = doc.getElementsByTagName("openSearch:itemsPerPage")
					.item(0).getFirstChild().getNodeValue();
		}
		if (doc.getElementsByTagName("openSearch:startIndex").getLength() > 0) {
			startIndex = doc.getElementsByTagName("openSearch:startIndex")
					.item(0).getFirstChild().getNodeValue();
		}

		return list;
	}

	public DoubanBroadcast getRecommendation(DoubanBroadcast target, List<DoubanBroadcast> list){
		for(DoubanBroadcast b : list){
			if(b.getTitle().equals(target.getTitle())){
				return b;
			}
		}
		return null;
	}
	
	public List<DoubanUser> searchUser(String keyword, int start, int length){
		List<DoubanUser> matchList = new ArrayList<DoubanUser>();
		String url = PEOPLE_SEARCH + "?q=" + keyword + "&start-index=" + start + "&max-results=" + length;
		Document doc = getDocument(url);
		NodeList entryList = doc.getElementsByTagName("entry");
		
		for (int i = 0; i < entryList.getLength(); i++) {
			matchList.add(parseUser(entryList.item(i)));
		}
		
		if (doc.getElementsByTagName("openSearch:totalResults").getLength() > 0) {
			totalResults = doc.getElementsByTagName("openSearch:totalResults")
					.item(0).getFirstChild().getNodeValue();
		}
		if (doc.getElementsByTagName("openSearch:itemsPerPage").getLength() > 0) {
			itemsPerPage = doc.getElementsByTagName("openSearch:itemsPerPage")
					.item(0).getFirstChild().getNodeValue();
		}
		if (doc.getElementsByTagName("openSearch:startIndex").getLength() > 0) {
			startIndex = doc.getElementsByTagName("openSearch:startIndex")
					.item(0).getFirstChild().getNodeValue();
		}
		return matchList;
	}
	
	public List<DoubanBroadcast> getBroadcast(String type, String uid, int start, int max) {
		String url = null;
		if(type.equals("broadcast")){
			url = BROADCASTS.replace("{userID}", uid) + "?start-index="
				+ start + "&max-results=" + max;
		}else{
			url = uid + "/recommendations?start-index="
				+ start + "&max-results=" + max;
		}
		
		Log.i("DoubanDiablo", "" + url);
		List<DoubanBroadcast> list = new ArrayList<DoubanBroadcast>();
		Document doc = getDocument(url);
		// Log.i("DoubanDiablo", XmlUtil.xmlDocumentToString(doc));
		
		NodeList entryList = doc.getElementsByTagName("entry");
		Log.i("DoubanDiablo", " ~~:" + entryList.getLength());

		for (int i = 0; i < entryList.getLength(); i++) {
			list.add(parseBroadcast(entryList.item(i)));
		}
		return list;
	}

	public List<DoubanBroadcast> getComments(String url, int start, int length) {
		List<DoubanBroadcast> list = new ArrayList<DoubanBroadcast>();
		url += "/comments?start-index=" + start + "&max-results=" + length;
		Log.i("DoubanDiablo", url);
		Document doc = getDocument(url);
		NodeList entryList = doc.getElementsByTagName("entry");
		// Log.i("DoubanDiablo", " ~~:" + entryList.getLength());

		for (int i = 0; i < entryList.getLength(); i++) {
			list.add(parseBroadcast(entryList.item(i)));
		}
		totalResults = doc.getElementsByTagName("openSearch:totalResults")
				.item(0).getFirstChild().getNodeValue();
		// Log.i("DoubanDiablo", "totalResults: " + totalResults);
		itemsPerPage = doc.getElementsByTagName("openSearch:itemsPerPage")
				.item(0).getFirstChild().getNodeValue();
		// Log.i("DoubanDiablo", "itemsPerPage: " + itemsPerPage);
		startIndex = doc.getElementsByTagName("openSearch:startIndex").item(0)
				.getFirstChild().getNodeValue();
		// Log.i("DoubanDiablo", "startIndex: " + startIndex);
		return list;
	}

	private static Doumail parseDoumail(Node entryNode) {
		Doumail mail = new Doumail();
		NodeList list = entryNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.hasChildNodes()) {

				String tag = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();
				// Log.i("DoubanDiablo", tag + ": " + value);
				if (tag.equals("id")) {
					mail.setId(value);
				} else if (tag.equals("author")) {
					mail.setFrom(parseUser(node));
					mail.setTo(me);
				} else if (tag.equals("title")) {
					mail.setTitle(value);
				} else if (tag.equals("published")) {
					try {
						mail.setTime(new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (tag.equals("content")) {
					mail.setContent(value);
				} else if (tag.equals("db:attribute")) {
					if (node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("unread") && value.equals("true")) {						
							mail.setUnread(true);						
					} 
				} else if (tag.equals("db:entry")) {
					mail.setTo(parseUser(node));
					mail.setFrom(me);
				}
			} else {
				/*
				 * NamedNodeMap attrs = node.getAttributes(); if
				 * (attrs.getNamedItem("rel").getNodeValue().equals("self")) {
				 * 
				 * mail.setSelf(attrs.getNamedItem("href").getNodeValue()); }
				 * else if (attrs.getNamedItem("rel").getNodeValue().equals(
				 * "alternate")) { mail .setAlternate(attrs.getNamedItem("href")
				 * .getNodeValue()); // Log.i("DoubanDiablo", b.getCategory());
				 * }
				 */
			}
		}

		return mail;
	}

	private static DoubanUser parseUser(Node entryNode) {
		DoubanUser user = new DoubanUser();
		NodeList list = entryNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).hasChildNodes()) {
				Node node = list.item(i);
				String tag = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();
				if (tag.equalsIgnoreCase("id")) {
					user.setId(value);
				} else if (tag.equalsIgnoreCase("db:uid")) {
					user.setUid(value);
				} else if (tag.equalsIgnoreCase("title")) {
					user.setTitle(value);
				} else if (tag.equalsIgnoreCase("db:location")) {
					user.setLocation(value);
				} else if (tag.equalsIgnoreCase("content")) {
					user.setContent(value);
				} else if (tag.equals("name")) {
					user.setTitle(value);
				} else if (tag.equals("uri")) {
					user.setId(value);
				}
			} else if (list.item(i).hasAttributes()) {
				NamedNodeMap attr = list.item(i).getAttributes();
				String ref = attr.getNamedItem("rel").getNodeValue();
				String href = attr.getNamedItem("href").getNodeValue();
				if (ref.equals("alternate")) {
					user.setAlternate(href);
				} else if (ref.equals("icon")) {
					user.setIcon(href);
				} else if (ref.equals("homepage")) {
					user.setHomepage(href);
				}
			}
		}
		return user;
	}

	private static DoubanBroadcast parseBroadcast(Node entryNode) {
		DoubanBroadcast b = new DoubanBroadcast();
		NodeList list = entryNode.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.hasChildNodes()) {

				String tag = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();
				// Log.i("DoubanDiablo", tag + ": " + value);
				if (tag.equals("id")) {
					b.setId(value);
				} else if (tag.equals("author")) {
					b.setUser(parseUser(node));
				} else if (tag.equals("title")) {
					b.setTitle(value);
				} else if (tag.equals("published")) {
					try {
						b.setTime(new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (tag.equals("content")) {
					b.setContent(value);
				} else if (tag.equals("db:attribute")) {
					b.addAttribute(node.getAttributes().getNamedItem("name")
							.getNodeValue(), value);
				}
			} else {
				NamedNodeMap attrs = node.getAttributes();
				if (node.getNodeName().equals("link")) {
					b.addAttribute(attrs.getNamedItem("rel").getNodeValue(),
							attrs.getNamedItem("href").getNodeValue());
				} else if (node.getNodeName().equals("category")) {
					b.setCategory(attrs.getNamedItem("term").getNodeValue()
							.split("#miniblog\\.")[1]);
					// Log.i("DoubanDiablo", b.getCategory());
				}
			}
		}
		return b;
	}

	public Document getPeopleInfo(String peopleId) {
		String url = PEOPLE.replace("{user_id}", peopleId);

		HttpGet get = new HttpGet(url);

		get.addHeader("Authorization", getAuthHeader("GET", PEOPLE));
		get.addHeader("Content-Type", "text/xml");
		try {
			HttpResponse response = httpclient.execute(get);
			// Log.i(TAG, EntityUtils.toString(response.getEntity()));
			Document doc = documentBuilder.parse(response.getEntity()
					.getContent());
			return doc;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAuthHeader(String method, String url) {
		OAuthMessage msg = new OAuthMessage(method, url, null);
		try {
			msg.addRequiredParameters(accessor);
			String authHeader = msg.getAuthorizationHeader("");
			return authHeader;
		} catch (OAuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Log.i("DoubanDiablo", "URISyntaxException");
		}
		return null;
	}

}
