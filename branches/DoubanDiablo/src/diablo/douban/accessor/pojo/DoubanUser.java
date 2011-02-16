package diablo.douban.accessor.pojo;

import java.io.Serializable;

public class DoubanUser  implements Serializable{
	private String id;			//	用户豆瓣主页
	private String location;	//	用户的长居地	用户可能没有长居地
	private String uid;			//	用户在豆瓣上的username	
	private String title;		//	用户的名号	
	private String content;		//	用户的自我介绍	
	private String alternate;	//	用户的豆瓣页面	
	private String icon;		//	用户的头像图片链接	
	private String homepage;	//用户的个人主页	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
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
	public String getAlternate() {
		return alternate;
	}
	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	
	public String toString(){
		return "Id: " + id + "\nuid: " + uid + "\nlocation: " + location + "\ntitle: " + title
			+ "\nalternate: " + alternate + "\nicon: " + icon + "\nhomepage: " + homepage 
			+ "\ncontent: ..." ;
	}
}
