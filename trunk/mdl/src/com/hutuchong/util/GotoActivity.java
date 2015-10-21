package com.hutuchong.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import mobi.domore.mcdonalds.R;

public class GotoActivity {
	/**
	 * 
	 * @param activity
	 * @param flg
	 * @param title
	 * @param link
	 * @param desc
	 */
	public static void gotoActivity(Context context, String action,
			String title, String link, String desc) {
		Intent i = new Intent();
		String pkg = context.getPackageName();
		if ("marketlist".equals(action)) {
			// i = new Intent(activity, MarketListActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_game.MarketListActivity");
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("marketlist_1".equals(action)) {
			// i = new Intent(activity, MarketListActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_game.MarketListActivity_1");
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("foto".equals(action)) {
			// i = new Intent(activity, ImageListActivity.class);
			i.setClassName(pkg, "com.hutuchong.jpmm.ImageListActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("fotoitem".equals(action)) {
			// i = new Intent(activity, DdupImageActivity.class);
			i.setClassName(pkg, "com.hutuchong.jpmm.DdupImageActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_FEED);
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("fotogrid".equals(action)) {
			// i = new Intent(activity, ImageGridActivity.class);
			i.setClassName(pkg, "com.hutuchong.jpmm.ImageGridActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_FEED);
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("news".equals(action)) {
			// i = new Intent(activity, NewsActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_news.NewsActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("newsitem".equals(action)) {
			// i = new Intent(activity, NewsDetailActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_news.NewsDetailActivity");
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("ddstory".equals(action)) {
			// i = new Intent(activity, StoryActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_story.StoryActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("ddstoryitem".equals(action)) {
			// i = new Intent(activity, StoryDetailActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_story.StoryDetailActivity");
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("music".equals(action)) {
			// i = new Intent(activity, MusicListActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_music.MusicListActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("musicitem".equals(action)) {
			// i = new Intent(activity, MusicItemActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_music.MusicItemActivity");
			i.putExtra(ContantValue.EXTRA_NEED_STOPSERVICE, true);
		} else if ("wallpaper".equals(action)) {
			// i = new Intent(activity, DdupImageGridActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_ddup.DdupImageGridActivity");
			i.putExtra(ContantValue.EXTRA_PAGEID, Commond.PAGE_CHANNEL);
		} else if ("wallpaperitem".equals(action)) {
			// i = new Intent(activity, DdupImageActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_ddup.DdupImageActivity");
		} else if ("sendmsg".equals(action)) {
			// i = new Intent(activity, ChatMsgActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_chat.ChatMsgActivity");
		} else if ("web".equals(action)) {
			// i = new Intent(activity, DdupWebActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_ddup.DdupWebActivity");
		} else if ("webdialog".equals(action)) {
			// i = new Intent(activity, WebDialog.class);
			i.setClassName(pkg, "com.hutuchong.util.WebDialog");
			i.putExtra("message", desc);
		} else if ("text".equals(action)) {
			// i = new Intent(activity, DdupTextActivity.class);
			i.setClassName(pkg, "com.hutuchong.app_ddup.DdupTextActivity");
		} else {
			i.setAction(action);
		}
		i.putExtra(ContantValue.EXTRA_TITLE, title);
		i.putExtra(ContantValue.EXTRA_URL, link);

		context.startActivity(i);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean gotoNavMore(Context context) {
		if (!TextUtils.isEmpty(Commond.copyright)) {
			String[] values = Commond.copyright.split("\\|");
			if (values.length == 2) {
				String action = values[0];
				String link = values[1];
				gotoActivity(context, action,
						context.getString(R.string.company), link, null);
			} else {
				return false;
			}

		} else {
			gotoMore(context);
		}
		return true;
	}

	/**
	 * 
	 * @param context
	 */
	public static void gotoMore(Context context) {
		gotoActivity(context, "marketlist_1",
				context.getString(R.string.company), ContantValue.ddMoreUrl,
				null);
	}

	/**
	 * 
	 * @param context
	 * @param url
	 */
	public static void gotoWebDialog(Context context, String url) {
		Intent i = new Intent(context, WebDialog.class);
		i.putExtra("url", url);
		context.startActivity(i);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean gotoAbout(Context context) {
		Intent i = new Intent(context, AboutActivity.class);
		context.startActivity(i);
		return true;
	}
}