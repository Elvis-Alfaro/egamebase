package diablo.douban.accessor.pojo;

import java.io.Serializable;
import java.util.Date;

/*
 <entry xmlns="http://www.w3.org/2005/Atom" xmlns:db="http://www.douban.com/xmlns/" xmlns:gd="http://schemas.google.com/g/2005" xmlns:opensearch="http://a9.com/-/spec/opensearchrss/1.0/">
<id>http://api.douban.com/note/8001851</id>
<title>ABOUT ME</title>
<author>
<link href="http://api.douban.com/people/1057620" rel="self"/>
<link href="http://www.douban.com/people/aka/" rel="alternate"/>
<link href="http://t.douban.com/icon/u1057620-26.jpg" rel="icon"/>
<name>���ֵĴ�ͷ���ͷ���ͷ���ͷ��</name>
<uri>http://api.douban.com/people/1057620</uri>
</author>
<published>2008-08-15T16:32:20+08:00</published>
<updated>2008-08-15T16:32:20+08:00</updated>
<link href="http://api.douban.com/note/8001851" rel="self"/>
<link href="http://www.douban.com/note/8001851/" rel="alternate"/>
<summary>��ʧȥ�����������Ҫ�����Լ��ĺã�  �Ҵ�����Ѱ���καܷ�ۣ�20�����������ÿ��ѡ���������Լ����ұ���������Ķ�Ҫ��ǿ����Ϊ�ҵļ�ǿ�����˺ܶ࣬ȴ��������Ȼû�к�ڹ��κΡ� �ҴӲ����������Լ��Ǽ���ΰ����£�û��������Ҫ��������ȫ�ģ������������Լ��ĸ�����Ϊ���ľ������ݣ����ָ���ίʵ���ڳ�������.......</summary>
<content>��ʧȥ�����������Ҫ�����Լ��ĺã�  �Ҵ�����Ѱ���καܷ�ۣ�20�����������ÿ��ѡ���������Լ����ұ���������Ķ�Ҫ��ǿ����Ϊ�ҵļ�ǿ�����˺ܶ࣬ȴ��������Ȼû�к�ڹ��κΡ� �ҴӲ����������Լ��Ǽ���ΰ����£�û��������Ҫ��������ȫ�ģ������������Լ��ĸ�����Ϊ���ľ������ݣ����ָ���ίʵ���ڳ�������.......</content>
<db:attribute name="privacy">public</db:attribute>
<db:attribute name="can_reply">yes</db:attribute>
</entry>
 */
public class DoubanNote implements Serializable{
	private String id;
	private String title;
	private DoubanUser author;
	private Date published, updated;
	private String summary;
	private String content;
	private String privacy;
	private boolean canReply;
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
	public DoubanUser getAuthor() {
		return author;
	}
	public void setAuthor(DoubanUser author) {
		this.author = author;
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
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	public boolean isCanReply() {
		return canReply;
	}
	public void setCanReply(boolean canReply) {
		this.canReply = canReply;
	}
}
