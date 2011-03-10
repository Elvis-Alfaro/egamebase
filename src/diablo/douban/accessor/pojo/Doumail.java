package diablo.douban.accessor.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.Serializable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Doumail implements Serializable{
	
	private String id;
	private String title;
	private String content;
	
	private DoubanUser from;
	private DoubanUser to;	
	private Date time;
	private boolean unread = false;
	private String self;
	private String alternate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public DoubanUser getFrom() {
		return from;
	}
	public void setFrom(DoubanUser from) {
		this.from = from;
	}
	public DoubanUser getTo() {
		return to;
	}
	public void setTo(DoubanUser to) {
		this.to = to;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public boolean isUnread() {
		return unread;
	}
	public void setUnread(boolean unread) {
		this.unread = unread;
	}
	public String getSelf() {
		return self;
	}
	public void setSelf(String self) {
		this.self = self;
	}
	public String getAlternate() {
		return alternate;
	}
	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}
	
	public static Doumail parseDoumail(Node entryNode, DoubanUser me) {
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
					mail.setFrom(DoubanUser.parseUser(node));
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
					mail.setTo(DoubanUser.parseUser(node));
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
}
