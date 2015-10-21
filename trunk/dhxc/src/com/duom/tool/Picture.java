package com.duom.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Picture {
private int id;
private String zhuti;
private String dizhi;
private String xinqing;
private String route;
private String path;
private int music;
private int bg;

public Picture( String zhuti , String dizhi ,int music , int bg , String path , String route ,String xinqing){	
	this.zhuti = zhuti;
	this.xinqing = xinqing;
	this.music = music;
	this.dizhi = dizhi;
	this.route = route;
	this.path = path;
	this.bg = bg;
  }
public Picture( String route ){	

	this.route = route;

  }
//public Picture( int id , String zhuti , String dizhi ,String date , int music, String xinqing){
//	this.id= id;
//	this.zhuti = zhuti;
//	this.xinqing = xinqing;
//	this.dizhi = dizhi;
//	this.date = date;
//  }
public int getId() {
	return id;
}
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public int getBg() {
	return bg;
}
public void setBg(int bg) {
	this.bg = bg;
}
public void setId(int id) {
	this.id = id;
}
public String getZhuti() {
	return zhuti;
}
public void setZhuti(String zhuti) {
	this.zhuti = zhuti;
}
public String getDizhi() {
	return dizhi;
}
public void setDizhi(String dizhi) {
	this.dizhi = dizhi;
}
public String getXinqing() {
	return xinqing;
}
public void setXinqing(String xinqing) {
	this.xinqing = xinqing;
}

public void setRoute(String route) {
	this.route = route;
}
public int getMusic() {
	return music;
}
public void setMusic(int music) {
	this.music = music;
}

//public String getDate() {
//	try {
//		return new SimpleDateFormat("yyyy年MM月dd日").parse(date);
//	} catch (ParseException e) {
//		return null;
//	}
//}
public String getRoute() {
	return route;
}
@Override
public String toString() {
	return "Picture [id=" + id + ", zhuti=" + zhuti + ", dizhi=" + dizhi
			+ ", xinqing=" + xinqing + ", route=" + route + ", bofang=" + music
			+ "]";
}
}
