package com.jw.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import cn.coolworks.util.Debug;

import com.hutuchong.util.Cookie;

///#define DIRECTFIRST

public class Connect extends Thread {
	// private String strTestCMWAPUrl = "http://202.152.178.239/oem/mz.wml";
	private String STRINGUSERAGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	private String Accept = "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
	private ProgressListener progressListener;
	private Object tag;

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public Connect(java.net.URL url, boolean proxyMode) {
		bRun = true;
		// strTestCMWAPUrl = strTestUrl;
		this.proxyMode = proxyMode;
		// this.nCurrentStatus=currentState;
		p_Url = url;
		this.start();
	}

	/**
	 * 
	*/
	public void setProxyMode(boolean proxy) {
		this.proxyMode = proxy;
	}

	public int getProgress() {
		return nProgress;
	}

	/**
	 * 
	 */
	private void setProgress(int progress) {
		nProgress = progress;
		if (this.progressListener != null) {
			// Debug.d("setProgress:" + progress);
			this.progressListener.onProgress(this.tag, this.nProgress);
		}
	}

	/**
	 * 
	 */
	private void setError(String msg) {
		Debug.d("setError:" + msg);
		if (this.progressListener != null) {
			// Debug.d("setProgress:" + progress);
			this.progressListener.onError(msg);
		}
	}

	/**
	 * 
	 */
	private void setBaseUrl(String url) {
		if (this.progressListener != null) {
			// Debug.d("setProgress:" + progress);
			this.progressListener.onBaseUrl(url);
		}
	}

	boolean bCancel = false;

	public void cancelHTTP() {
		Debug.d("cancelHTTP", "cancelHTTP:" + bCancel);
		synchronized (this) {
			// bRun=false;
			if (p_Connect != null) {
				try {
					bCancel = true;
					// synchronized(this)
					{
						notify();
					}

					setProgress(0);
					new CMCancelThread(p_Connect, is, os);
				} catch (Exception e) {
					// #if DEBUG
					// # System.out.println("Cancel failed:"+e.toString());
					// #endif
				}
			}
		}
	}

	public byte[] read(ProgressListener l, String url) {
		this.setProgressListener(l);
		while (bCancel) {
			setProgress(0);
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}

		byte[] result = null;

		httpRequest(url, null);

		while (!finished(url)) {
			// Debug.d("finished","finished");
			if (!this.isQuickConnectPossible())
				break;
			if (bCancel)
				break;
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}
		result = getResult(url);
		bFinished = true;
		return result;
	}

	public byte[] read(ProgressListener l, String url, UploadData upload) {
		this.setProgressListener(l);
		while (bCancel) {
			setProgress(0);

			try {
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}

		httpRequest(url, upload);

		while (!finished(url)) {
			if (!this.isQuickConnectPossible())
				break;

			if (bCancel)
				break;
			// #if DEBUG
			// # if( upload.nType == 2 && getProgress() != i )
			// # {
			// # i = getProgress();
			// # System.out.println("Upload:"+i);
			// # }
			// #endif
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}

		byte[] result = getResult(url);
		bFinished = true;
		return result;
	}

	public byte[] read(String url, byte[] data) {
		while (bCancel) {
			setProgress(0);

			try {
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}

		httpRequest(url, data);

		while (!finished(url)) {
			if (!this.isQuickConnectPossible())
				break;

			if (bCancel)
				break;

			try {
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}

		byte[] result = getResult(url);
		bFinished = true;
		return result;
	}

	public void stopThread() {
		bRun = false;
		while (bRunning) {
			try {
				synchronized (this) {
					notify();
				}
				Thread.sleep(10);
			} catch (Exception e) {
				;
			}
		}
		System.out.println("Connect.java exit");
	}

	// if not connected return 0
	// direct return 1
	// wap return 2
	public boolean isProxyMode() {
		return (p_Connect != null && p_Proxy == null);
	}

	public int getStatus() {
		if (nCurrentStatus == 0)
			return 0;
		else if (isProxyMode())
			return 2;
		else
			return 1;
	}

	int nCurrentStatus = 1; // == 0 Detect CMWAP/CMNET
	// == 1 Mode detected, serving the request
	boolean proxyMode = false; // == 0 CMWAP
	// == 1 CMNET

	boolean bRunning = false;
	boolean bRun;
	java.util.Hashtable inputTable = new java.util.Hashtable();
	java.util.Hashtable<String, byte[]> outputTable = new java.util.Hashtable<String, byte[]>();
	private static final int RETRYCOUNT = 3;

	public boolean isConnected() {
		return bConnected;
	}

	public boolean isQuickConnectPossible() {
		if (nTrying > 3)
			return false;
		else
			return true;
	}

	public void clearBuffer() {
		synchronized (this) {
			inputTable.clear();
		}
	}

	boolean bConnected = false;
	int nTrying = 0;

	// public void test()
	// {
	// byte[] result= null;
	// result=readData(strTestCMWAPUrl, false, null, null );
	// // String str=new String()
	// // com.joinwin.android.basic.Debug(result);
	// Debug.d(new String(result));
	// Debug.d(result.toString());
	// //
	// }

	public void run() {
		byte[] result;
		bRunning = true;
		bRun = true;

		nTrying = 0;
		bConnected = false;
		nCurrentStatus = 0;

		while (bRun) {

			{
				synchronized (this) {
					bConnected = true;
					nTrying = 0;
				}

				if (inputTable.isEmpty()) {// Nothing to process
					synchronized (this) {
						try {
							wait();
							continue;
						} catch (Exception e) {
							;
						}
					}
				} else {
					java.util.Enumeration e;
					synchronized (this) {
						e = inputTable.keys();
					}

					while (e != null && e.hasMoreElements()) {
						String strUrl;
						UploadData uploadData = null;
						byte[] postData = null;
						String strType = "";
						String toFile = null;
						int length = 0;
						synchronized (this) {
							this.tag = null;
							// this.setProgressListener(null);
							strUrl = (String) e.nextElement();
							Object o = inputTable.get(strUrl);
							if (o instanceof byte[]) {
								postData = (byte[]) o;
								strType = "application/octet-stream";
								length = postData.length;
							} else if (o instanceof UploadData) {
								uploadData = (UploadData) o;
								//
								this.tag = uploadData.tag;
								this.setProgressListener(uploadData.progressListener);
								//
								uploadData.processData();
								length = uploadData.contentLength;
								if (uploadData.nType == 2)
									strType = uploadData.strType
											+ "; boundary="
											+ uploadData.strBoundry;
								else if (uploadData.nType == 1) {
									postData = uploadData.data;
									length = uploadData.contentLength;
									strType = uploadData.strType;
								} else
									strType = uploadData.strType;

								// if(upload.strFilename != null )
								// {
								// toFile = upload.strFilename;
								// }
							} else
								postData = null;
						}

						int nCount = RETRYCOUNT;
						String strUrl302 = strUrl;
						do {
							synchronized (this) {
								bCancel = false;
							}
							// Debug.d("readData proxyMode:"
							// + String.valueOf(proxyMode));
							result = readWebData(strUrl302, proxyMode,
									uploadData, postData, strType, length);// ,
							// toFile);
							// Debug.d("bCancel=" + bCancel);
							if (httpResultCode == java.net.HttpURLConnection.HTTP_MOVED_TEMP) {
								strUrl302 = new String(result);
							} else if (result != null || httpResultCode != 0
									|| bCancel) {// httpResultCode != 0 means
								// Debug.d("httpResultCode=" + httpResultCode);
								// server error not network
								// error
								// Debug.d("****************************");
								// Debug.d(result);
								// Debug.d("****************************");
								// Debug.d("\n");
								synchronized (this) {
									if (result == null && !bCancel)
										outputTable.put(strUrl, new byte[0]);
									else if (!bCancel)
										outputTable.put(strUrl, result);
									// Debug.d("remove:" + strUrl);
									inputTable.remove(strUrl);
								}
								// Support Cancel
								synchronized (this) {
									bCancel = false;
								}
								break;
							}
							// Debug.d("nCount:" + nCount);
							nCount--;
							setError("重试网络连接:" + nCount);
						} while (nCount > 0);
						if (nCount <= 0) {// Max retry count achieved, re detect
							// network.
							nCurrentStatus = 0;
							setError("网络连接失败，请检查网络是否正常！");
							// Debug.d("nCount:" + nCount);
							synchronized (this) {
								outputTable.put(strUrl, new byte[0]);
								inputTable.remove(strUrl);
							}
						}
					}
				}
			}
		}

		bRunning = false;
		nTrying = 0;
		bConnected = false;
	}

	private int httpResultCode;

	int nProgress = 0;
	// HttpConnection conn = null;
	private java.net.HttpURLConnection p_Connect = null;
	private java.net.URL p_Proxy = null;
	private java.net.URL p_Url = null;

	InputStream is = null;
	OutputStream os = null;

	private byte[] readWebData(String url, boolean modeCmwap,
			UploadData uploadData, byte[] postData, String strType, int length)// ,
	// String
	// strName)
	{
		// sunxml added 01/31/2010
		Debug.d("url:" + url);

		byte[] result = null;
		bCancel = false;
		synchronized (this) {
			p_Connect = null;
			p_Proxy = null;
		}
		// modeCmwap=false;
		is = null;
		os = null;

		setProgress(0);

		int rc;
		try {
			// ///////////////////////////////
			String referer = null;
			String newUrl = url;
			String f = "[referer]";
			int b = url.indexOf(f);
			if (b > 0) {
				b += f.length();
				int e = url.indexOf("[/referer]", b);
				if (e > b) {
					referer = url.substring(b, e);
					//
					newUrl = url.substring(0, b - f.length());
				}
			}
			// //////////////////////////////
			p_Url = new URL(newUrl);
			String p_host = p_Url.getHost();
			if (p_Url.getPort() != -1)
				p_host += ":" + p_Url.getPort();
			System.out.println("p_host:" + p_host);
			if (modeCmwap) {
				p_Proxy = new URL("http://10.0.0.172:80" + p_Url.getFile());
			}
			synchronized (this) {
				if (p_Proxy != null)
					p_Connect = (java.net.HttpURLConnection) p_Proxy
							.openConnection();
				else
					p_Connect = (java.net.HttpURLConnection) p_Url
							.openConnection();
				p_Connect.setConnectTimeout(20 * 1000);// min*second*mills
				p_Connect.setReadTimeout(5 * 60 * 1000);
			}

			p_Connect.setRequestProperty("User-Agent", STRINGUSERAGENT);
			p_Connect.setRequestProperty("Connection", "Keep-Alive");
			p_Connect.setRequestProperty("Accept", Accept);
			if (modeCmwap) {
				p_Connect.setRequestProperty("X-online-Host", p_host);
			}
			String cookie = Cookie.cookies.get(p_host);
			if (!TextUtils.isEmpty(cookie)) {
				// Debug.d("p_Url.getHost():" + p_Url.getHost());
				p_Connect.setRequestProperty("Cookie", cookie);
			}
			if (!TextUtils.isEmpty(referer))
				p_Connect.setRequestProperty("Referer",
						URLDecoder.decode(referer));
			p_Connect.setRequestProperty("Host", p_host);

			//
			if ((postData != null && postData.length > 0)
					|| (uploadData != null && uploadData.headerData != null)) {
				// Debug.d("POST");
				p_Connect.setRequestMethod("POST");
				//
				// Debug.d("strType:" + strType);
				p_Connect.setRequestProperty("Content-Type", strType);
				// Debug.d("length:" + length);
				p_Connect.setRequestProperty("Content-Length",
						Integer.toString(length));
			} else {
				// Debug.d("GET");
				p_Connect.setRequestMethod("GET");
			}
			p_Connect.setDoOutput(true);
			p_Connect.setDoInput(true);

			if (postData != null && postData.length > 0) {
				os = p_Connect.getOutputStream();
				if (postData.length > 2000) {// Progress only for > 2000 and
					// segment
					int len = 100;
					for (int i = 0; i < postData.length;) {
						os.write(postData, i, len);
						if (bCancel) {
							setProgress(0);
							break;
						} else
							setProgress(i * 100 / postData.length);
						i += len;
					}

				} else {
					os.write(postData);
				}
				setProgress(100);
			} else if (uploadData != null && uploadData.headerData != null) {
				os = p_Connect.getOutputStream();
				// 写头数据
				os.write(uploadData.headerData);
				//
				if (TextUtils.isEmpty(uploadData.strFilename)) {
					if (uploadData.data != null && uploadData.data.length > 0) {
						os.write(uploadData.data);
					}
				} else {
					// 写文件数据
					FileInputStream fis = new FileInputStream(
							uploadData.strFilename);
					int len = 10000;
					byte[] buf = new byte[len];
					int c = uploadData.fileLen / len;
					int count = 0;
					int index = 0;
					while (-1 != (count = fis.read(buf))) {
						os.write(buf, 0, count);
						if (bCancel) {
							setProgress(0);
							break;
						} else
							setProgress(index * 100 / c);
						index++;
					}
					buf = null;
					fis.close();
					fis = null;
				}
				// 写尾部数据
				os.write(uploadData.endData);
			} else {// nProgress only for post
				setProgress(0);
			}
			// os.flush();
			// conn.setDoOutput(newValue)
			// p_Connect.
			rc = p_Connect.getResponseCode();
			Debug.d("getResponseCode:" + rc);
			httpResultCode = rc;
			// /////////////////////////////////////////
			// 支持cookie
			// /////////////////////////////////////////
			if (Cookie.cookies.containsKey(p_Url.getHost())) {
				Map<String, List<String>> aa = p_Connect.getHeaderFields();
				List<String> list = aa.get("set-cookie");
				if (list != null) {
					StringBuffer coo = new StringBuffer();
					for (String value : list) {
						String[] v = value.split(";");
						coo.append(v[0]);
						coo.append(";");
						Debug.d("set-cookie:" + value);
					}
					Cookie.cookies.put(p_Url.getHost(), coo.toString());
				}
			}
			// /////////////////////////////////////////
			// 支持cookie
			// /////////////////////////////////////////
			if (rc == java.net.HttpURLConnection.HTTP_MOVED_TEMP) {
				String loc = p_Connect.getHeaderField("location");
				Debug.d("loc:" + loc);
				if (!TextUtils.isEmpty(loc)) {
					if (loc.startsWith("/")) {
						loc = "http://" + p_Url.getHost() + loc;
					} else if (!loc.startsWith("http://")) {

						loc = "http://" + p_Url.getPath() + "/" + loc;
					}
					Debug.d("loc:" + loc);
				}
				//
				this.setBaseUrl(loc);
				//
				result = loc.getBytes();
			} else if (rc == java.net.HttpURLConnection.HTTP_OK) {
				// sunxml added 02/05/2010
				is = p_Connect.getInputStream();
				int totalLen = p_Connect.getContentLength();
				Debug.d("totalLen:" + totalLen);
				//
				OutputStream bos;
				boolean isFile = false;
				if (uploadData != null
						&& !TextUtils.isEmpty(uploadData.responseFileName)) {
					isFile = true;
					bos = new FileOutputStream(uploadData.responseFileName);
				} else
					bos = new ByteArrayOutputStream();
				int len;
				int progress = 0;
				byte[] buff = new byte[1024];
				while ((len = is.read(buff)) != -1) {
					if (bCancel) {
						setProgress(0);
						break;
					}
					if (totalLen > 0)
						setProgress(progress * 100 / totalLen);
					bos.write(buff, 0, len);
					progress += len;
				}
				buff = null;
				if (!bCancel) {
					String str = p_Connect.getHeaderField("Content-Encoding");
					// Debug.d("Content-Encoding:" + str);
					if (str != null && str.compareTo("gzip") == 0) {
						try {
							if (isFile) {
								result = uploadData.responseFileName.getBytes();
							} else {
								result = GZip
										.inflate(((ByteArrayOutputStream) bos)
												.toByteArray());
							}
						} catch (Exception e) {
							//
							setError("GZip Inflate error");
							// Debug.d("Inflate error");
							result = null;
						}
					} else // */
					{
						if (isFile) {
							result = uploadData.responseFileName.getBytes();
						} else {
							result = ((ByteArrayOutputStream) bos)
									.toByteArray();
						}
						// Debug.d("result.length:" + result.length);
					}
				}
				setProgress(100);
				bos.close();
				bos = null;
			} else {// Handle other HTTP code
				httpResultCode = rc;
				setError("服务器返回错误码：" + httpResultCode + ",请重试！");
			}
		} catch (Exception e) {
			// #if DEBUG
			// System.out.println(this.getClass().getName() + ":" +
			// e.toString());
			// # e.printStackTrace();
			// #endif
			//
			e.printStackTrace();
			// Debug.d("Exception url:" + url);
			// Debug.d("Exception message:" + e.getMessage());
			httpResultCode = 0;
		} finally {
			// if (is != null)
			// {
			// try
			// {
			// is.close();
			// }
			// catch(Exception e)
			// {
			// ;
			// }
			// is = null;
			// }
			// if (os != null)
			// {
			// try
			// {
			// os.close();
			// }
			// catch(Exception e)
			// {
			// ;
			// }
			// os = null;
			// }
			// if (p_Connect != null)
			// {
			// synchronized(this)
			// {
			// try
			// {
			// p_Connect.disconnect();
			// }
			// catch(Exception e)
			// {
			// ;
			// }
			// p_Connect = null;
			// }
			// }
		}

		return result;
	}

	public int getConnectTimeout() {
		return p_Connect.getConnectTimeout();
	}

	private void httpRequest(String strUrl, Object postData) {
		synchronized (this) {
			if (postData == null)
				inputTable.put(strUrl, new byte[0]);
			else
				inputTable.put(strUrl, postData);

			try {
				notify();
			} catch (Exception e) {
				;
			}
		}
	}

	private boolean bFinished = false;

	public boolean finished() {
		return bFinished;
	}

	private boolean finished(String strUrl) {
		boolean bRet = false;

		synchronized (this) {
			if (outputTable.containsKey(strUrl)) {
				// bFinished=true;
				bRet = true;
			} else {// Added by Simon on 6/27/2009 to trigged http read
				// System.out.println("Trig again");

				if (inputTable.containsKey(strUrl)) {
					// System.out.println("Trig in finish");
					this.notify();
				} else {
					// System.out.println("who call me?" + inputTable.size()
					// + "    " + strUrl);
					// bFinished=true;
					return true;
				}
			}
		}
		return bRet;
	}

	private byte[] getResult(String strUrl) {
		byte[] temp = null;
		synchronized (this) {
			if (outputTable.containsKey(strUrl)) {
				temp = (byte[]) outputTable.get(strUrl);
				outputTable.remove(strUrl);
			} else {
				inputTable.remove(strUrl);
			}
		}
		return temp;
	}

	public static String findString(String content, String startStr,
			int startPos) {
		int pos;
		if ((pos = content.indexOf(startStr, startPos)) == -1)
			pos = 0;
		return content.substring(pos + 1);
	}

	public static String findString(String mainStr, String from, String to) {
		int pos;
		if ((pos = mainStr.indexOf(from)) == -1)
			return "";
		pos += from.length();
		int j1;
		if ((j1 = mainStr.indexOf(to, pos + 1)) == -1)
			j1 = mainStr.length();
		return mainStr.substring(pos, j1);
	}

	public static String UTF8Decode(byte in[], int offset, int length) {
		StringBuffer buff = new StringBuffer();
		int max = offset + length;
		for (int i = offset; i < max; i++) {
			char c = 0;
			if ((in[i] & 0x80) == 0) {
				c = (char) in[i];
			} else if ((in[i] & 0xe0) == 0xc0) // 11100000
			{
				c |= ((in[i] & 0x1f) << 6); // 00011111
				i++;
				c |= ((in[i] & 0x3f) << 0); // 00111111
			} else if ((in[i] & 0xf0) == 0xe0) // 11110000
			{
				c |= ((in[i] & 0x0f) << 12); // 00001111
				i++;
				c |= ((in[i] & 0x3f) << 6); // 00111111
				i++;
				c |= ((in[i] & 0x3f) << 0); // 00111111
			} else if ((in[i] & 0xf8) == 0xf0) // 11111000
			{
				c |= ((in[i] & 0x07) << 18); // 00000111 (move 18, not 16?)
				i++;
				c |= ((in[i] & 0x3f) << 12); // 00111111
				i++;
				c |= ((in[i] & 0x3f) << 6); // 00111111
				i++;
				c |= ((in[i] & 0x3f) << 0); // 00111111
			} else {
				c = '?';
			}
			buff.append(c);
		}
		return buff.toString();
	}

	public static byte[] UTF8Encode(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int strlen = str.length();

			for (int i = 0; i < strlen; i++) {
				char t = str.charAt(i);
				int c = t & 0xffff;

				if (c >= 0 && c < 0x80) {
					bos.write((byte) (c & 0xff));
				} else if (c > 0x7f && c < 0x800) {
					bos.write((byte) (((c >>> 6) & 0x1f) | 0xc0));
					bos.write((byte) (((c >>> 0) & 0x3f) | 0x80));
				} else if (c > 0x7ff && c < 0x10000) {
					bos.write((byte) (((c >>> 12) & 0x0f) | 0xe0)); // <--
					// correction
					// (mb)
					bos.write((byte) (((c >>> 6) & 0x3f) | 0x80));
					bos.write((byte) (((c >>> 0) & 0x3f) | 0x80));
				} else if (c > 0x00ffff && c < 0xfffff) {
					bos.write((byte) (((c >>> 18) & 0x07) | 0xf0));
					bos.write((byte) (((c >>> 12) & 0x3f) | 0x80));
					bos.write((byte) (((c >>> 6) & 0x3f) | 0x80));
					bos.write((byte) (((c >>> 0) & 0x3f) | 0x80));
				}
			}
			bos.flush();
		} catch (Exception e) {
		}
		return bos.toByteArray();
	}

	static public String urlEncode(String sUrl) {
		System.out.println(sUrl);
		StringBuffer StrUrl = new StringBuffer();
		for (int i = 0; i < sUrl.length(); ++i) {
			char c = sUrl.charAt(i);
			switch (c) {
			case ' ':
				StrUrl.append("%20");
				break;
			case '+':
				StrUrl.append("%2b");
				break;
			case '\'':
				StrUrl.append("%27");
				break;
			case '/':
				StrUrl.append("%2F");
				break;
			case '.':
				StrUrl.append("%2E");
				break;
			case '<':
				StrUrl.append("%3c");
				break;
			case '>':
				StrUrl.append("%3e");
				break;
			case '#':
				StrUrl.append("%23");
				break;
			case '%':
				StrUrl.append("%25");
				break;
			case '&':
				StrUrl.append("%26");
				break;
			case '{':
				StrUrl.append("%7b");
				break;
			case '}':
				StrUrl.append("%7d");
				break;
			case '\\':
				StrUrl.append("%5c");
				break;
			case '^':
				StrUrl.append("%5e");
				break;
			case '~':
				StrUrl.append("%73");
				break;
			case '[':
				StrUrl.append("%5b");
				break;
			case ']':
				StrUrl.append("%5d");
				break;
			default:

				if (c < 128)
					StrUrl.append(sUrl.charAt(i));
				else {
					StrUrl.append(toUTF8(c));
				}
				break;
			}
		}
		return StrUrl.toString();
	}

	public static String toUTF8(char ch) {
		StringBuffer buffer = new StringBuffer();

		if (ch < 0x80) {
			buffer.append(ch);
		} else if (ch < '\u0800') {
			buffer.append("%" + toHexString((byte) (0xc0 | (ch >> 6))));
			buffer.append("%" + toHexString((byte) (0x80 | (ch & 0x3f))));
		} else {
			int i = ch;
			buffer.append("%" + toHexString((byte) (0xe0 | (ch >> 12))));
			buffer.append("%" + toHexString((byte) (0x80 | ((ch >> 6) & 0x3f))));
			buffer.append("%" + toHexString((byte) (0x80 | (ch & 0x3f))));
		}

		return buffer.toString();
	}

	public static String toHexString(byte by) {
		try {
			return Integer.toHexString((0x000000ff & by) | 0xffffff00)
					.substring(6);
		} catch (Exception e) {
			return null;
		}
	}

	public static String digestString(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// #if DEBUG
			// # System.out.println("MD5 digest is not supported!");
			// #endif
			return null;
		}

		md5.update(str.getBytes(), 0, str.length());
		byte[] hash = new byte[16];

		int hashSize = 0;
		try {
			hashSize = md5.digest(hash, 0, hash.length);
		} catch (DigestException e) {
			// #if DEBUG
			// # System.out.println("MD5 digest error!");
			// #endif
		}
		if (hashSize > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hash.length; ++i) {
				sb.append(Integer.toHexString(
						(0x000000ff & hash[i]) | 0xffffff00).substring(6));
			}

			return sb.toString();
		}
		return null;
	}
}
