package diablo.douban.accessor.pojo;

import java.io.Serializable;

public class DoubanUser  implements Serializable{
	private String id;			//	�û�������ҳ
	private String location;	//	�û��ĳ��ӵ�	�û�����û�г��ӵ�
	private String uid;			//	�û��ڶ����ϵ�username	
	private String title;		//	�û�������	
	private String content;		//	�û������ҽ���	
	private String alternate;	//	�û��Ķ���ҳ��	
	private String icon;		//	�û���ͷ��ͼƬ����	
	private String homepage;	//�û��ĸ�����ҳ	
	
	
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
