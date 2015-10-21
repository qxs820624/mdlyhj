package cn.duome.fotoshare.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Paint;
import android.text.TextUtils;

/**
 * 
 * @author sunxml
 * 
 */
public class StringUtil {
	/**
	 * 
	 * @param url
	 * @param name
	 * @param value
	 * @return
	 */
	public static String appendNameValue(String url, String name, String value) {
		if (!url.contains("&" + name + "=") && !url.contains("?" + name + "=")) {
			if (url.indexOf('?') > 0) {
				url += "&" + name + "=" + value;
			} else {
				url += "?" + name + "=" + value;
			}
		}
		return url;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String toN(String str) {
		if (TextUtils.isEmpty(str))
			return str;
		String result = str.toLowerCase();
		String tag = "<br />";
		str = str.replace("&nbsp;", " ");
		str = str.replace("<div>", tag);
		str = str.replace("</div>", tag);

		String[] strs = str.split(tag);
		int len = strs.length;
		if (len > 1) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < len; i++) {
				sb = sb.append(strs[i]);
				sb = sb.append('\r');
				sb = sb.append('\n');
			}
			result = sb.toString();
		}
		result = result.replace(tag, "");// 可能在结尾会出现，所以替换
		return result;
	}

	/**
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidateEmail(String email) {
		boolean isExist = false;
		Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
		Matcher m = p.matcher(email);
		boolean b = m.matches();
		if (b) {
			isExist = true;
		} else {
		}
		return isExist;
	}

	/**
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean isValidatePassword(String pwd) {
		boolean isExist = false;
		Pattern p = Pattern.compile("[0-9A-Za-z_]*");
		Matcher m = p.matcher(pwd);
		boolean b = m.matches();
		if (b) {
			isExist = true;
		} else {
		}
		return isExist;
	}

	/**
	 * 
	 * @return
	 */
	public static String getLocaleLanguage() {
		Locale l = Locale.getDefault();
		return String.format("%s-%s", l.getLanguage(), l.getCountry());
	}

	/**
	 * 
	 * @param oldStr
	 * @return
	 */
	public static String replaceBR(String oldStr) {
		String[] titles = oldStr.split("<br/>");
		StringBuffer sb = new StringBuffer();
		int len = titles.length;
		for (int i = 0; i < len; i++) {
			sb.append(titles[i]);
			if (i != len - 1)
				sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param aY
	 * @param aHeight
	 * @param aPaint
	 * @return
	 */
	public static int getBaseLine(int aY, int aHeight, Paint aPaint) {
		return (int) (aY + (aHeight - aPaint.getTextSize()) / 2 - aPaint
				.ascent());
	}

	/**
	 * 
	 * @param aHeight
	 * @param aPaint
	 * @return
	 */
	public static int getBaseLine(int aHeight, Paint aPaint) {
		return (int) ((aHeight - aPaint.getTextSize()) / 2 - aPaint.ascent());
	}

	/**
	 * 
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String[] split(String bufferstr, String regex) {

		if (bufferstr == null)
			return null;
		Vector<String> split = new Vector<String>();

		while (true) {
			int index = bufferstr.indexOf(regex);

			if (index == -1) {
				if (bufferstr != null && !bufferstr.equals(""))
					split.addElement(bufferstr);
				// log.debug("bufferstr=" +bufferstr);s
				break;
			}
			split.addElement(bufferstr.substring(0, index));
			// log.debug("Str=" + bufferstr.substring(0, index));
			bufferstr = bufferstr.substring(index + 1, bufferstr.length());
			// log.debug("bufferstr=" +bufferstr);
		}
		String[] s = new String[split.size()];

		split.copyInto(s);

		return s;

	}

	/**
	 * 
	 * @param aUrl
	 * @return
	 */
	public static String getHost(String aUrl) {
		String host = aUrl;
		int index = aUrl.indexOf('/', 7);
		if (index > 0) {
			host = host.substring(0, index);
		}
		host += "/";
		return host;
	}

	/**
	 * 
	 * @param aUrl
	 * @param current
	 * @return
	 */
	public static String setUrl(String aUrl, String current) {
		String url = aUrl;
		current = current.trim();
		if (current.startsWith("http://") || current.startsWith("file://")) {
			url = current;
		} else if (current.startsWith("#")) {
			url = url + current;
		} else {
			if (current.startsWith("/")) {
				int begin = url.indexOf('/', 7);
				if (begin > 0) {
					url = url.substring(0, begin);
				}
			} else if (current.startsWith("../")) {
				int pos = current.indexOf("../");
				int index = url.lastIndexOf('/');
				if (index >= 0)
					url = url.substring(0, index);
				while (pos >= 0) {
					current = current.substring(pos + 2);

					index = url.lastIndexOf('/');
					if (index > 0)
						url = url.substring(0, index);

					pos = current.indexOf("../");
				}
			} else {
				int begin = url.lastIndexOf('/');
				if (begin > 7) {
					url = url.substring(0, begin + 1);
				} else
					url += "/";
			}
			url = url + current;
		}
		return url;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	static public String filterRNSapce(String text) {
		// Debug.d("filterRNSapce:" + text);
		//
		char[] chars = text.toCharArray();
		int i = 0;
		// System.out.println("text:" + text);
		while (i < chars.length
				&& (chars[i] == '\r' || chars[i] == '\n' || chars[i] == ' ')) {
			i++;
		}
		// System.out.println("i=" + i);
		if (i > 0)
			text = text.substring(i);
		//
		chars = text.toCharArray();
		i = chars.length - 1;
		while (i >= 0
				&& (chars[i] == '\r' || chars[i] == '\n' || chars[i] == ' ')) {
			i--;
		}
		// System.out.println("i=" + i);
		if (i > 0 && i < chars.length - 1)
			text = text.substring(0, i);
		// System.out.println("text:" + text);
		return text;
	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 */
	static private long getDate(String dateStr, boolean isShort) {
		long millis = -1;
		Date date = null;
		if (TextUtils.isEmpty(dateStr))
			return millis;
		if (TextUtils.isDigitsOnly(dateStr)) {
			date = new Date(Long.parseLong(dateStr));
		} else {
			String formatStr = "yyyy-MM-dd HH:mm:ss";
			if (isShort) {
				formatStr = "yyyy-MM-dd";
			}
			DateFormat format = new SimpleDateFormat(formatStr);
			try {
				date = format.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			millis = calendar.getTimeInMillis();
		}
		return millis;
	}

	/**
	 * 
	 * @param pubdate
	 * @return
	 */
	static public String comDate(String baseDate, String pubDate) {
		long baseMillis = getDate(baseDate, false);
		if (baseMillis == -1)
			return pubDate;

		long pubMillis = getDate(pubDate, false);
		if (pubMillis == -1)
			return pubDate;

		//
		String str = strDate(baseMillis, pubMillis);
		return str == null ? pubDate : str;
	}

	/**
	 * 
	 * @param pubdate
	 * @return
	 */
	static public String comShortDate(String pubDate) {
		Calendar calendar = Calendar.getInstance();
		long baseMillis = calendar.getTimeInMillis();

		long pubMillis = getDate(pubDate, true);
		if (pubMillis == -1)
			return pubDate;

		//
		String str = strDate(baseMillis, pubMillis);
		return str == null ? pubDate : str;
	}

	/**
	 * 
	 * @param pubdate
	 * @return
	 */
	static public String strDate(long baseMillis, long pubMillis) {
		long millis = baseMillis - pubMillis;
		millis = millis / 1000;
		// System.out.println("millis:" + millis);
		if (millis < 60)
			return millis + "秒前";
		else {
			if (millis < 60 * 60)
				return (int) (millis / 60) + "分钟前";
			else {
				if (millis < 24 * 60 * 60)
					return (int) (millis / (60 * 60)) + "小时前";
				else {
					if (millis < 7 * 24 * 60 * 60) {
						int days = (int) (millis / (24 * 60 * 60));
						if (days == 1)
							return "昨天";
						else if (days == 2)
							return "前天";
						Calendar calendar1 = Calendar.getInstance();
						calendar1.setTimeInMillis(baseMillis);
						int d1 = calendar1.get(Calendar.DAY_OF_WEEK);
						if (days >= d1) {
							Calendar calendar2 = Calendar.getInstance();
							calendar2.setTimeInMillis(pubMillis);
							int d2 = calendar2.get(Calendar.DAY_OF_WEEK);
							return "上周" + weekStr(d2);
						}
						return days + "天前";
					} else {
						if (millis < 4 * 7 * 24 * 60 * 60) {
							int weeks = (int) (millis / (7 * 24 * 60 * 60));
							if (weeks == 1) {
								String strWeek = "上周";
								Calendar calendar = Calendar.getInstance();
								calendar.setTimeInMillis(pubMillis);
								int d = calendar.get(Calendar.DAY_OF_WEEK);
								strWeek += weekStr(d);
								return strWeek;
							}
							return weeks + "周前";
						} else {
							if (millis < 29030400/* (12 * 4 * 7 * 24 * 60 * 60) */) {
								return (int) (millis / (4 * 7 * 24 * 60 * 60))
										+ "月前";
							} else {
								int y = (int) (millis / 29030400/*
																 * (12 * 4 * 7 *
																 * 24 * 60 * 60)
																 */);
								if (y <= 0)
									return null;
								return y + "年前";
							}
						}
					}
				}
			}
		}
	}

	static public String weekStr(int week) {
		switch (week) {
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		case 7:
			return "日";
		}
		return week + "";
	}

	/**
	 * 
	 * @param inputString
	 * @return
	 */
	static public String html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
																										// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
																									// }
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}

	/**
	 * 
	 * @param pubdate
	 * @return
	 */
	static public boolean isToday(String pubdate) {
		if (TextUtils.isEmpty(pubdate))
			return false;
		//
		SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date pubDate = myFmt.parse(pubdate);

			Date date = new Date();
			if (date.getYear() == pubDate.getYear()
					&& date.getMonth() == pubDate.getMonth()
					&& date.getDate() == pubDate.getDate()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		return false;
	}

	/**
	 * 
	 * @param sizeStr
	 * @return
	 */
	public static String readableFileSize(String sizeStr) {
		try {
			if (!TextUtils.isDigitsOnly(sizeStr))
				return sizeStr;
			long size = Long.parseLong(sizeStr);
			if (size <= 0)
				return "0";
			final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
			int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
			return new DecimalFormat("#,##0.#").format(size
					/ Math.pow(1024, digitGroups))
					+ " " + units[digitGroups];
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
