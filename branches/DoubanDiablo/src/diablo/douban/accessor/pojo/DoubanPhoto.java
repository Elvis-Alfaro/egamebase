package diablo.douban.accessor.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
<?xml version="1.0" encoding="UTF-8"?> 
<entry xmlns="http://www.w3.org/2005/Atom" xmlns:db="http://www.douban.com/xmlns/" xmlns:gd="http://schemas.google.com/g/2005" xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/" xmlns:opensearch="http://a9.com/-/spec/opensearchrss/1.0/"> 
	<id>http://api.douban.com/photo/885370690</id> 
	<title>照片</title> 
	<author> 
		<link href="http://api.douban.com/people/2894865" rel="self"/> 
		<link href="http://www.douban.com/people/xaver222/" rel="alternate"/> 
		<link href="http://img3.douban.com/icon/u2894865-180.jpg" rel="icon"/> 
		<name>xaver222@日本</name> 
		<uri>http://api.douban.com/people/2894865</uri> 
	</author> 
	<published>2011-03-10T15:59:49+08:00</published> 
	<link href="http://api.douban.com/photo/885370690" rel="self"/> 
	<link href="http://www.douban.com/photos/photo/885370690/" rel="alternate"/> 
	<link href="http://img3.douban.com/view/photo/icon/public/p885370690.jpg" rel="icon"/> 
	<link href="http://img3.douban.com/view/photo/photo/public/p885370690.jpg" rel="image"/> 
	<link href="http://img3.douban.com/view/photo/thumb/public/p885370690.jpg" rel="thumb"/> 
	<content>11.03.10 至少五人前的手羽焼き炸鸡翅...然后悲剧的是我吃了2个外加一个饭团就饱了...（鸡中翅连翅尖 酱料 淀粉）</content> 
	<db:attribute name="comments_count">8</db:attribute> 
	<db:attribute name="recs_count">3</db:attribute> 
	<db:attribute name="position">0</db:attribute> 
	<db:attribute name="next_photo">884739931</db:attribute> 
	<db:attribute name="prev_photo">502448746</db:attribute> 
	<db:attribute name="album">28294228</db:attribute> 
	<db:attribute name="album_title">居家厨男养成记录</db:attribute> 
</entry> 
*/

public class DoubanPhoto {
	public static DoubanPhoto parsePhoto(Node entryNode){
		DoubanPhoto photo = new DoubanPhoto();
		NodeList list = entryNode.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.hasChildNodes()) {

				String tag = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();
				// Log.i("DoubanDiablo", tag + ": " + value);
				if (tag.equals("id")) {
					photo.setId(value);
				} else if (tag.equals("author")) {
					photo.setUser(DoubanUser.parseUser(node));
				} else if (tag.equals("published")) {
					try {
						photo.setPublished(new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (tag.equals("content")) {
					photo.setContent(value);
				} else if (tag.equals("db:attribute")) {
					if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("comments_count")){
						photo.setCommentsCount(Integer.parseInt(value));
					}else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("recs_count")){
						photo.setRecsCount(Integer.parseInt(value));
					} else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("position")){
						photo.setPosition(Integer.parseInt(value));
					} else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("next_photo")){
						photo.setNextPhoto(value);
					} else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("prev_photo")){
						photo.setPrevPhoto(value);
					} else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("album")){
						photo.setAlbumID(value);
					} else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("album_title")){
						photo.setAlbumTitle(value);
					}					
				}
			} else {
				NamedNodeMap attrs = node.getAttributes();
				if (node.getNodeName().equals("link")) {
					if(attrs.getNamedItem("rel").getNodeValue().equals("icon")){
						photo.setIcon(attrs.getNamedItem("href").getNodeValue());
					} else if(attrs.getNamedItem("rel").getNodeValue().equals("image")){
						photo.setImage(attrs.getNamedItem("href").getNodeValue());
					} else if(attrs.getNamedItem("rel").getNodeValue().equals("thumb")){
						photo.setThumb(attrs.getNamedItem("href").getNodeValue());
					} else if(attrs.getNamedItem("rel").getNodeValue().equals("alternate")){
						photo.setAlternate(attrs.getNamedItem("href").getNodeValue());
					}
							
				} 
			}
		}
		return photo;
	}
	private String id;
	private String alternate;
	private String icon;
	private String image;
	private String thumb;
	private String content;
	private int commentsCount;
	private int recsCount;
	private int position;
	private String nextPhoto;
	private String prevPhoto;
	private String albumID;
	private String albumTitle;
	private Date published;
	private DoubanUser user;
	
	public String toString(){
		return "id : " + id + "\n"
			+ "alternate : " + alternate + "\n"
			+ "icon : " + icon + "\n"
			+ "image : " + image + "\n"
			+ "thumb : " + thumb + "\n"
			+ "content : " + content + "\n"
			+ "commentsCount : " + commentsCount + "\n"
			+ "recsCount : " + recsCount + "\n"
			+ "position : " + position + "\n"
			+ "nextPhoto : " + nextPhoto + "\n"
			+ "prevPhoto : " + prevPhoto + "\n"
			+ "albumID : " + albumID + "\n"
			+ "albumTitle : " + albumTitle + "\n"
			+ "published : " + published + "\n"
			+ "user : " + user + "\n";
	}
	
	public DoubanUser getUser() {
		return user;
	}

	public void setUser(DoubanUser user) {
		this.user = user;
	}

	public Date getPublished() {
		return published;
	}
	
	public void setPublished(Date published) {
		this.published = published;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}
	public int getRecsCount() {
		return recsCount;
	}
	public void setRecsCount(int recsCount) {
		this.recsCount = recsCount;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getNextPhoto() {
		return nextPhoto;
	}
	public void setNextPhoto(String nextPhoto) {
		this.nextPhoto = nextPhoto;
	}
	public String getPrevPhoto() {
		return prevPhoto;
	}
	public void setPrevPhoto(String prevPhoto) {
		this.prevPhoto = prevPhoto;
	}
	public String getAlbumID() {
		return albumID;
	}
	public void setAlbumID(String albumID) {
		this.albumID = albumID;
	}
	public String getAlbumTitle() {
		return albumTitle;
	}
	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}
	
	
}
