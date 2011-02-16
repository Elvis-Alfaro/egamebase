package diablo.douban.accessor.pojo;

import java.util.Date;

import java.io.Serializable;

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
	
}
