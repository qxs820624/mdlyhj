package cn.coolworks.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Paint;
import android.text.TextUtils;
import cn.coolworks.entity.LineInfo;

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
	 * �и�str�ַ�
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String[] split(String bufferstr, String regex) {

		if (bufferstr == null)
			return null;
		Vector<String> split = new Vector<String>();

		while (true) // ����������ϻ�õ���ݲ�������д���
		{
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
					current = current.substring(pos + 2);// 2:��Ϊ�˱�����/��ͷ

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
	 * �ֶα����ַ�
	 * 
	 * @param InfoLines
	 *            ����ֶε��ַ��б�
	 * @param infostr
	 *            ��Ҫ�ֶε��ַ�
	 * @param font
	 *            ����
	 * @param len
	 *            �п�
	 */
	static public void doLine(Vector<LineInfo> InfoLines, String infostr,
			Paint paint, int lineWidth, int startX, int lineCount) {
		// Debug.d("infostr:" + infostr);
		// Ϊ�ַ���У��Ա�����ʾ
		if (infostr == null || InfoLines == null)
			return;
		int breakIndex = 0;
		char[] chars = infostr.toCharArray();
		while (breakIndex < chars.length) {
			int len = paint.breakText(chars, breakIndex, chars.length
					- breakIndex, lineWidth, null);
			LineInfo line = new LineInfo();
			line.content = new String(chars, breakIndex, len);
			InfoLines.addElement(line);
			breakIndex += len;
		}
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
	 * @param pubdate
	 * @return
	 */
	static public String comDate(String pubdate) {
		String dateTip = "";
		//
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(new Date(Long.parseLong(pubdate)));
		//
		Calendar calendar2 = Calendar.getInstance();
		calendar1.add(Calendar.SECOND, 60);
		if (calendar1.compareTo(calendar2) > 0) {
			calendar1.add(Calendar.SECOND, -60);
			dateTip = ((calendar2.getTimeInMillis() - calendar1
					.getTimeInMillis())) / 1000 + "秒之前";
		} else {
			calendar1.add(Calendar.SECOND, -60);
			calendar1.add(Calendar.MINUTE, 60);
			if (calendar1.compareTo(calendar2) > 0) {
				calendar1.add(Calendar.MINUTE, -60);
				dateTip = ((calendar2.getTimeInMillis() - calendar1
						.getTimeInMillis())) / (60 * 1000) + "分钟之前";
			} else {
				calendar1.add(Calendar.MINUTE, -60);
				calendar1.add(Calendar.HOUR_OF_DAY, 24);
				if (calendar1.compareTo(calendar2) > 0) {
					calendar1.add(Calendar.HOUR_OF_DAY, -24);
					dateTip = ((calendar2.getTimeInMillis() - calendar1
							.getTimeInMillis())) / (60 * 60 * 1000) + "小时之前";
				}
			}
		}
		if (TextUtils.isEmpty(dateTip)) {
			calendar1.setTime(new Date(Long.parseLong(pubdate)));
			dateTip = calendar1.get(Calendar.YEAR) + "-"
					+ (calendar1.get(Calendar.MONTH) + 1) + "-"
					+ calendar1.get(Calendar.DAY_OF_MONTH) + " "
					+ calendar1.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar1.get(Calendar.MINUTE) + ":"
					+ calendar1.get(Calendar.SECOND);
		}
		return dateTip;
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
		long size = Long.parseLong(sizeStr);
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}
}
