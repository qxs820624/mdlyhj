package com.hutuchong.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class Data {
	static private String favfilename = "favlist";
	static private ArrayList<String> favlist;
	static private String votefilename = "votelist";
	static private ArrayList<String> votelist;
	static private String tabfilename = "tablinks";
	static private Hashtable<String, String[]> tabList;

	/**
	 * 得到收藏夹列表
	 * 
	 * @param context
	 * @return
	 */
	static public ArrayList<String> getFavList(Context context) {
		if (favlist != null)
			return favlist;
		favlist = new ArrayList<String>();
		try {
			FileInputStream fis = context.openFileInput(favfilename);
			if (fis != null) {
				int len = fis.read();
				while (len > 0) {
					byte[] buff = new byte[len];
					fis.read(buff);
					favlist.add(new String(buff));
					len = fis.read();
				}
				fis.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return favlist;
	}

	/**
	 * 保存收藏列表
	 * 
	 * @param context
	 * @return
	 */
	static public boolean saveFavList(Context context) {
		try {
			FileOutputStream fos = context.openFileOutput(favfilename,
					Context.MODE_PRIVATE);
			if (fos != null) {
				for (String item : favlist) {
					byte[] buf = item.getBytes();
					int len = buf.length;
					fos.write(len);
					if (buf.length > 0) {
						fos.write(item.getBytes());
					}
				}
				fos.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 得到推荐列表
	 * 
	 * @param context
	 * @return
	 */
	static public ArrayList<String> getVoteList(Context context) {
		if (votelist != null)
			return votelist;
		votelist = new ArrayList<String>();
		try {
			FileInputStream fis = context.openFileInput(votefilename);
			if (fis != null) {
				int len = fis.read();
				while (len > 0) {
					byte[] buff = new byte[len];
					fis.read(buff);
					votelist.add(new String(buff));
					len = fis.read();
				}
				fis.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return votelist;
	}

	/**
	 * 推荐收藏列表
	 * 
	 * @param context
	 * @return
	 */
	static public boolean saveVoteList(Context context) {
		try {
			FileOutputStream fos = context.openFileOutput(votefilename,
					Context.MODE_PRIVATE);
			if (fos != null) {
				for (String item : votelist) {
					byte[] buf = item.getBytes();
					int len = buf.length;
					fos.write(len);
					if (buf.length > 0) {
						fos.write(item.getBytes());
					}
				}
				fos.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 保存urls列表
	 * 
	 * @param context
	 * @return
	 */
	static public boolean saveTabList(Context context, String xmlStr) {
		try {
			FileOutputStream fos = context.openFileOutput(tabfilename,
					Context.MODE_PRIVATE);
			if (fos != null) {
				fos.write(xmlStr.getBytes());
				fos.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取urls列表
	 * 
	 * @param inStream
	 * @return
	 */
	public static Hashtable<String, String[]> getTabList(Context context) {
		if (tabList != null && tabList.size() > 0)
			return tabList;
		tabList = new Hashtable<String, String[]>();
		try {
			InputStream is = null;
			// 首先判断cache中是否存在
			try {
				is = context.openFileInput(tabfilename);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				is = context.getResources().getAssets().open(tabfilename);
			}
			//
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser(); // 创建解析器
			XMLContentHandler handler = new XMLContentHandler();
			saxParser.parse(is, handler);
			is.close();
			tabList = handler.getTabs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tabList;
	}
}

/**
 * 
 * @author sunxml
 * 
 */
class XMLContentHandler extends DefaultHandler {
	public Hashtable<String, String[]> getTabs() {
		return tabs;
	}

	public void setTabs(Hashtable<String, String[]> tabs) {
		this.tabs = tabs;
	}

	private Hashtable<String, String[]> tabs;
	private String currentName;
	private String currentTitle;
	private String currentUrl;
	private String tagName = null;// 当前解析的元素标签

	/*
	 * 接收文档的开始的通知。
	 */
	@Override
	public void startDocument() throws SAXException {
		tabs = new Hashtable<String, String[]>();
	}

	/*
	 * 接收字符数据的通知。
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (tagName != null) {
			String data = new String(ch, start, length);
			if (tagName.equals("item")) {
				currentUrl = data;
			}
		}
	}

	/*
	 * 接收元素开始的通知。 参数意义如下： namespaceURI：元素的命名空间 localName ：元素的本地名称（不带前缀） qName
	 * ：元素的限定名（带前缀） atts ：元素的属性集合
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("item")) {
			currentName = atts.getValue("name");
			currentTitle = atts.getValue("title");

		}
		this.tagName = localName;
	}

	/*
	 * 接收文档的结尾的通知。 参数意义如下： uri ：元素的命名空间 localName ：元素的本地名称（不带前缀） name
	 * ：元素的限定名（带前缀）
	 */
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (localName.equals("item")) {
			String[] value = new String[2];
			value[0] = currentTitle;
			value[1] = currentUrl;
			tabs.put(currentName, value);
		}
		this.tagName = null;
	}
}