package diablo.douban.accessor.pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * <entry xmlns="http://www.w3.org/2005/Atom" xmlns:db="http://www.douban.com/xmlns/" xmlns:gd="http://schemas.google.com/g/2005" xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/" xmlns:opensearch="http://a9.com/-/spec/opensearchrss/1.0/"> 
	<id>http://api.douban.com/album/28294228</id> 
	<title>居家厨男养成记录</title> 
	<author> 
		<link href="http://api.douban.com/people/2894865" rel="self"/> 
		<link href="http://www.douban.com/people/xaver222/" rel="alternate"/> 
		<link href="http://img3.douban.com/icon/u2894865-180.jpg" rel="icon"/> 
		<name>xaver222@日本</name> 
		<uri>http://api.douban.com/people/2894865</uri> 
	</author> 
	<published>2010-06-06T16:56:08+08:00</published> 
	<updated>2011-03-10T15:59:49+08:00</updated> 
	<link href="http://api.douban.com/album/28294228" rel="self"/> 
	<link href="http://www.douban.com/photos/album/28294228/" rel="alternate"/> 
	<link href="http://img3.douban.com/view/photo/albumcover/public/p811519208.jpg" rel="cover"/> 
	<link href="http://img3.douban.com/view/photo/thumb/public/p811519208.jpg" rel="cover_thumb"/> 
	<content>cooked by xaver222 的男子汉料理</content> 
	<db:attribute name="size">44</db:attribute> 
	<db:attribute name="privacy">public</db:attribute> 
	<db:attribute name="recs_count">51</db:attribute> 
</entry> 
 */
public class DoubanAlbum  implements Serializable{
	
	public static DoubanAlbum parseAlbum(Node entryNode){
		DoubanAlbum album = new DoubanAlbum();
		NodeList list = entryNode.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.hasChildNodes()) {

				String tag = node.getNodeName();
				String value = node.getFirstChild().getNodeValue();
				// Log.i("DoubanDiablo", tag + ": " + value);
				if (tag.equals("id")) {
					album.setId(value);
				} else if (tag.equals("author")) {
					album.setUser(DoubanUser.parseUser(node));
				} else if (tag.equals("published")) {
					try {
						album.setPublished(new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (tag.equals("updated")) {
					try {
						album.setUpdated(new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss'+08:00'").parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (tag.equals("content")) {
					album.setContent(value);
				} else if (tag.equals("db:attribute")) {
					if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("size")){
						album.setSize(Integer.parseInt(value));
					}else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("privacy")){
						album.setPrivacy(value);
					} else if(node.getAttributes().getNamedItem("name")
							.getNodeValue().equals("recs_count")){
						album.setRecsCount(Integer.parseInt(value));
					} 			
				}
			} else {
				NamedNodeMap attrs = node.getAttributes();
				if (node.getNodeName().equals("link")) {
					if(attrs.getNamedItem("rel").getNodeValue().equals("cover")){
						album.setCover(attrs.getNamedItem("href").getNodeValue());
					} else if(attrs.getNamedItem("rel").getNodeValue().equals("cover_thumb")){
						album.setCover_thumb(attrs.getNamedItem("href").getNodeValue());
					} else if(attrs.getNamedItem("rel").getNodeValue().equals("alternate")){
						album.setAlternate(attrs.getNamedItem("href").getNodeValue());
					}
							
				} 
			}
		}
		return album;
	}
	
	
	private String id;
	private String title;
	private String alternate;
	private String cover;
	private String cover_thumb;
	private String content;
	private int size;
	private String privacy;
	private int recsCount;
	private DoubanUser user;
	private Date published, updated;
	
	
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
	public String getAlternate() {
		return alternate;
	}
	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getCover_thumb() {
		return cover_thumb;
	}
	public void setCover_thumb(String coverThumb) {
		cover_thumb = coverThumb;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	public int getRecsCount() {
		return recsCount;
	}
	public void setRecsCount(int recsCount) {
		this.recsCount = recsCount;
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
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
