package com.hutuchong.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlRegexpUtil {
	private final static String regxpForHtml = "<([^>]*)>";

	private final static String regxpForImgTag = "<\\s*img\\s+([^>]*)\\s*>";

	private final static String regxpForImaTagSrcAttrib = "src=\"([^\"]+)\"";

	/**
	 * 
	 */
	public HtmlRegexpUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public String replaceTag(String input) {
		if (!hasSpecialChars(input)) {
			return input;
		}
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		for (int i = 0; i <= input.length() - 1; i++) {
			c = input.charAt(i);
			switch (c) {
			case '<':
				filtered.append("&lt;");
				break;
			case '>':
				filtered.append("&gt;");
				break;
			case '"':
				filtered.append("&quot;");
				break;
			case '&':
				filtered.append("&amp;");
				break;
			default:
				filtered.append(c);
			}

		}
		return (filtered.toString());
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public boolean hasSpecialChars(String input) {
		boolean flag = false;
		if ((input != null) && (input.length() > 0)) {
			char c;
			for (int i = 0; i <= input.length() - 1; i++) {
				c = input.charAt(i);
				switch (c) {
				case '>':
					flag = true;
					break;
				case '<':
					flag = true;
					break;
				case '"':
					flag = true;
					break;
				case '&':
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String filterHtml(String str) {
		Pattern pattern = Pattern.compile(regxpForHtml);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String filterHtmlTag(String str) {
		return str.replaceAll("<([^>]*)>", "");
	}

	/**
	 * 
	 * @param str
	 * @param tag
	 * @return
	 */
	public static String fiterHtmlTag(String str, String tag) {
		String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
		Pattern pattern = Pattern.compile(regxp);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		while (result1) {
			matcher.appendReplacement(sb, "");
			result1 = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * @param str
	 * @param beforeTag
	 * @param tagAttrib
	 * @param startTag
	 * @param endTag
	 * @return
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag) {
		String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
		String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";
		Pattern patternForTag = Pattern.compile(regxpForTag);
		Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
		Matcher matcherForTag = patternForTag.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result = matcherForTag.find();
		while (result) {
			StringBuffer sbreplace = new StringBuffer();
			Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
					.group(1));
			if (matcherForAttrib.find()) {
				matcherForAttrib.appendReplacement(sbreplace, startTag
						+ matcherForAttrib.group(1) + endTag);
			}
			matcherForTag.appendReplacement(sb, sbreplace.toString());
			result = matcherForTag.find();
		}
		matcherForTag.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceImgTag(String str/* , ArrayList<String> imageurls */) {
		String patt = "<\\s*img\\s+([^>]*)\\s*src=\"([^\"]+)\"([^>]*)>";
		Pattern p = Pattern.compile(patt);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		boolean result = m.find();
		while (result) {
			i++;
			//
			String imageurl = m.group(2);
			// imageurls.add(imageurl);
			//
			m.appendReplacement(sb, "<br/>[img]" + imageurl + "[/img]<br/>");
			result = m.find();
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceLine(String str) {
		str = str.replaceAll("<br>", '\n' + "");
		str = str.replaceAll("<br/>", '\n' + "");
		str = str.replaceAll("<p>", '\n' + "");
		str = str.replaceAll("<tr>", '\n' + "");
		str = str.replaceAll("<td>", " ");
		str = str.replaceAll("</td>", " ");
		str = str.replaceAll("&nbsp;", " ");
		return str;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String getBody(String str) {
		String desc = str;
		// 过滤html标签
		String body = "<body>";
		int begin = desc.indexOf(body);
		if (begin >= 0) {
			int end = desc.indexOf("</body>");
			desc = desc.substring(begin + body.length(), end);
		}
		desc = HtmlRegexpUtil.filterHtmlTag(desc);
		return desc;
	}
}
