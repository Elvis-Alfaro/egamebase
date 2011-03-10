package diablo.douban.accessor.pojo;

import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DoubanUser  implements Serializable{
	private String id;			//	�û�������ҳ
	private String location;	//	�û��ĳ��ӵ�	�û�����û�г��ӵ�
	private String uid;			//	�û��ڶ����ϵ�username	
	private String title;		//	�û�������	
	private String content;		//	�û������ҽ���	
	private String alternate;	//	�û��Ķ���ҳ��	
	private String icon;		//	�û���ͷ��ͼƬ����	
	private String homepage;	//�û��ĸ�����ҳ	
	
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
