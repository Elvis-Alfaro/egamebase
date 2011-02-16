package diablo.douban.accessor.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;

public class DoubanBroadcast implements Serializable{
	private String id;
	private DoubanUser user;	
	private String title;
	private String category;
	private String content;
	private Date time;
	private Map<String, String> map = new HashMap<String, String>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DoubanUser getUser() {
		return user;
	}
	public void setUser(DoubanUser user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	public void addAttribute(String key, String value){
		map.put(key, value);
	}
}
