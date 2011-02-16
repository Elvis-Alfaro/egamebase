package diablo.douban.accessor.pojo;

import java.util.List;

public class DoubanAuthData {
	private static List<DoubanAuthData> dat;
	private static DoubanAuthData current;
	
	public static DoubanAuthData getCurrent(){
		return current;
	}
	public static void setCurrent(DoubanAuthData d){
		current = d;
	}
	
	public DoubanAuthData(){}
	
	public DoubanAuthData(int id, String userid, String username,
			String token, String secret, String icon) {
		this.id = id;
		this.userid = userid;
		this.username = username;
		this.token = token;
		this.secret = secret;
		this.icon = icon;
	}
	
	
	public static DoubanAuthData getById(int id){
		for(DoubanAuthData d : dat){
			if(d.getId() == id){
				return d;
			}
		}
		return null;
	}
	
	public static DoubanAuthData getByUid(String curUid) {
		for(DoubanAuthData d : dat){
			if(d.getUserid().equals(curUid)){
				current = d;
				return current;
			}
		}
		return null;
	}
	
	public static List<DoubanAuthData> getAuthData() {
		return dat;
	}
	public static void setAuthData(List<DoubanAuthData> dat) {
		DoubanAuthData.dat = dat;
	}
	
	public static void add(DoubanAuthData d){
		if(dat!=null){
			dat.add(d);
		}
	}
	
	private int id;
	private String userid;
	private String username;
	private String icon;
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	private String token;
	private String secret;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}

	

}
