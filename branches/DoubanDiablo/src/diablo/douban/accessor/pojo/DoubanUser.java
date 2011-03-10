package diablo.douban.accessor.pojo;

import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DoubanUser  implements Serializable{
	private String id;			//	用户豆瓣主页
	private String location;	//	用户的长居地	用户可能没有长居地
	private String uid;			//	用户在豆瓣上的username	
	private String title;		//	用户的名号	
	private String content;		//	用户的自我介绍	
	private String alternate;	//	用户的豆瓣页面	
	private String icon;		//	用户的头像图片链接	
	private String homepage;	//用户的个人主页	
	
	public static DoubanUser parseUser(Node entryNode) {
		DoubanUser user = new DoubanUser();
		NodeList list = entryNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).hasChildNodes()) {
				Node node = list.item(i);
				String tag = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();
				//Log.i("DoubanDiablo", "~~~" + tag + ": " + value);
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
