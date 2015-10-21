/**

 * 
 */
package com.jw.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import cn.duome.fotoshare.utils.Debug;

/**
 * @author Jose
 * 
 */
public class ConnectPoolManager extends Thread {

	boolean bRun = false;

	final static String KEY_HEADER = "K";//
	final static int MAX_VALUE = 5;// 最大连接数;
	final static int MIN_VALUE = 0;// 最小连接数;
	private boolean p_ProxyMode = false;// 联网模式:true为cmwap,false为cmnet

	// private Vector<Connect> p_ConnectPool;
	private Hashtable p_ConnectPool;// /连接池 Key: URL Value:Connect
	// private Hashtable p_ConnectKeyList;// 连接池中Key索引 Key : "k"+index(0-9) //
	// Value:URL

	private Vector<String> p_WaitTable;// 等待队列表

	// private int p_ActiveIndex = 0;//当前激活索引值
	private String p_ActiveKey = null;

	// private}
	public ConnectPoolManager(boolean isPproxy) {
		p_ProxyMode = isPproxy;
		p_WaitTable = new Vector();
		p_ConnectPool = new Hashtable(MAX_VALUE);
		// p_ConnectKeyList = new Hashtable(MAX_VALUE);
		start();
	}

	public boolean isP_ProxyMode() {
		return p_ProxyMode;
	}

	public void setP_ProxyMode(boolean pProxyMode) {
		p_ProxyMode = pProxyMode;
		//
		Enumeration en = p_ConnectPool.keys();
		while (en.hasMoreElements()) // 轮询连接池内所有连接
		{
			String key = (String) en.nextElement();
			Connect connect = (Connect) p_ConnectPool.get(key);
			connect.setProxyMode(p_ProxyMode);
		}
	}

	/**
	 * 添加一个新的连接
	 */
	private boolean replace(String oldKey, String newKey, Connect value) {

		p_ConnectPool.remove(oldKey);
		p_ConnectPool.put(newKey, value);
		return true;
	}

	private boolean add(String key) {
		java.net.URL url = null;
		try {
			url = new java.net.URL(key);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String key = url.getHost();
		Connect value = new Connect(url, p_ProxyMode);

		// synchronized(this)
		{

			// int index = p_ConnectKeyList.size() - 1;
			// p_ConnectKeyList.put(new String(KEY_HEADER + index), key);
			p_ConnectPool.put(key, value);
			// p_ActiveKey = key;
		}

		return true;
	}

	/**
	 * 删除一个指定的连接;
	 */
	private void remove(String key) {
		// String key = url.getHost();
		java.net.URL url = null;
		try {
			url = new java.net.URL(key);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized (this) {
			//
			// sunxml 2010/2/20 delete begin
			// ((Connect) p_ConnectPool.get(key)).cancelHTTP();
			// sunxml 2010/2/20 delete end
			((Connect) p_ConnectPool.get(key)).stopThread();
			// ((Connect)p_ConnectPool.get(key)).stop();
			p_ConnectPool.remove(key);
			// p_ConnectPool.clear();
			System.gc();
		}
	}

	/**
	 * 轮询连接池;
	 */
	private boolean poll(String key) {
		Connect value = null;
		// String key = url.getHost();
		Debug.d("ConnectPool：p_ConnectPool.size()=" + p_ConnectPool.size());
		if (p_ConnectPool.size() == MIN_VALUE) // 空连接池状态
		{
			Debug.d("ConnectPool：MIN_VALUE=" + MIN_VALUE);
			// Debug.PrintMsg("p_ConnectPool.size() == MIN_VALUE");
			return add(key);
		}
		if (p_ConnectPool.size() >= MIN_VALUE
				&& p_ConnectPool.size() < MAX_VALUE) // 连接池没有满的情况下;
		{
			// Debug.PrintMsg("p_ConnectPool.size() >= MIN_VALUE && p_ConnectPool.size() < MAX_VALUE");
			String searchValue = freeConnect(key);
			// value =(Connect) p_ConnectPool.get(searchValue);
			if (searchValue == null) // 连接池没有满的情况下,没有相同host的连接
			{
				return add(key);
			} else {
				value = (Connect) p_ConnectPool.get(searchValue);
				if (value.finished()) // 在有相同host的连接的情况下该连接状态为 空闲
				{
					// remove(searchValue);
					return replace(searchValue, key, value);
				} else // 在有相同host的连接的情况下该连接状态为 繁忙
				{
					return false;
				}
			}

		} else if (p_ConnectPool.size() == MAX_VALUE) // 连接池已满的情况
		{
			Debug.d("ConnectPool：MAX_VALUE=" + MAX_VALUE);
			int i = 0;
			// value = (Connect) p_ConnectPool.get(key);
			String keyIndex = null;
			Enumeration en = p_ConnectPool.keys();
			while (en.hasMoreElements()) // 轮询连接池内所有连接的状态
			{
				// keyIndex = new String(KEY_HEADER + i);
				key = (String) en.nextElement();
				value = (Connect) p_ConnectPool.get(key);
				if (value.finished()) // 如果有空闲的连接.则清理出该连接的空间,程序会轮询所有的连接,逐个清理连接
				{
					remove(key);
					// p_ConnectKeyList.remove(keyIndex);
				}
				// i++;
			}
			// Debug.PrintMsg("p_ConnectPool.size() == MAX_VALUE"
			// + p_ConnectPool.size());

			return false;

		}
		return false;
	}

	/**
	 * 查找可用连接;
	 */

	public String freeConnect(String key) {
		java.net.URL url = null, urlKey = null;
		String point = null;
		try {
			urlKey = new URL(key);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		java.util.Enumeration eo = p_ConnectPool.keys();

		while (eo != null && eo.hasMoreElements()) {
			try {
				url = new URL(point = (String) eo.nextElement());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (urlKey.getHost().equalsIgnoreCase(url.getHost())) {
				if (((Connect) p_ConnectPool.get(point)).finished())
					return point;
			}
		}
		return null;
	}

	private Connect find(String key) {
		// String key = url.getHost();
		Connect result;
		synchronized (this) {
			// p_WaitTable.put(key, url);
			// if()
			p_WaitTable.add(key);
			try {
				notify();
			} catch (Exception e) {

			}

		}
		while (((result = (Connect) p_ConnectPool.get(key))) == null) {
			// Debug.PrintMsg("Waiting for Create a Connect");
			// Debug.d("", "Waiting for Create a Connect");
		}
		// Connect result=(Connect) p_ConnectPool.get(url)
		// result= (Connect) p_ConnectPool.get(key);
		return result;
		// Debug.PrintMsg("create a Null connect");
		// return null;
	}

	public byte[] read(ProgressListener l, String strUrl, UploadData upload) {
		System.out.println("read strUrl 1:"+strUrl);
		return find(strUrl).read(l, strUrl, upload);
	}

	// public
	public byte[] read(ProgressListener l, String strUrl) {
		System.out.println("read strUrl 2:"+strUrl);
		return find(strUrl).read(l, strUrl);
	}

	public byte[] read(String strUrl, byte[] data) {
		System.out.println("read strUrl 3:"+strUrl);
		return find(strUrl).read(strUrl, data);
	}

	private void cancelHTTP() {
		if (p_ActiveKey == null)
			return;
		Connect c = (Connect) p_ConnectPool.get(p_ActiveKey);
		if (c != null)
			c.cancelHTTP();
	}

	/**
	 * 
	 */
	public void releaseAll() {
		release();
		Enumeration e = p_ConnectPool.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Connect c = (Connect) p_ConnectPool.get(key);
			c.clearBuffer();
			c.cancelHTTP();
			// c.stopThread();
		}
	}

	public void release() {

		bRun = false;
		p_WaitTable.removeAllElements();
		// for()

	}

	public int getProgress() {
		return ((Connect) p_ConnectPool.get(p_ActiveKey)).getProgress();
	}

	public void run() {
		bRun = true;

		while (bRun) {
			if (p_WaitTable.isEmpty()) {// Nothing to process
				synchronized (this) {
					try {
						wait();
						continue;
					} catch (Exception e) {
						;
					}
				}
			} else {
				try {
					// Debug.PrintMsg("Create Begin!"+(String)
					// p_WaitTable.firstElement());
					if (poll(((String) p_WaitTable.firstElement()))) {
						// p_WaitTable.;
						p_WaitTable.remove(0);
						// Debug.PrintMsg("Create filish!"+p_WaitTable.size());
					}
					Thread.sleep(10);
				} catch (Exception e) {

				}
			}

		}
	}

	/**
	 * 轮询连接池内部线程类;
	 */

}
