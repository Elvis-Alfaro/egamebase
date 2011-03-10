package diablo.douban.accessor.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DoubanBroadcast implements Serializable{
	private String id;
	private DoubanUser user;	
	private String title;
	private String category;
	private String content;
	private Date time;
	private Map<String, String> map = new HashMap<String, String>();
	
	public static DoubanBroadcast parseBroadcast(Node entryNode) {
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
					b.setUser(DoubanUser.parseUser(node));
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
