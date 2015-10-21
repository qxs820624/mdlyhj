package com.hutuchong.app_game.util;

import mobi.domore.mcdonalds.R;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.angeeks.market.ManageActivity;
import com.angeeks.market.MarketCategoryActivity;
import com.angeeks.market.MarketHomeActivity;
import com.angeeks.market.MarketSearchActivity;
import com.hutuchong.app_game.MarketListActivity;
import com.hutuchong.util.Commond;
import com.hutuchong.util.ContantValue;

public class MarketCommond {
	//
	public static int[] resTabIds = { R.id.nav_tab_1, R.id.nav_tab_2, R.id.nav_tab_3,
			R.id.nav_tab_4, R.id.nav_tab_5 };
	static int[] resIconNormalIds = { R.drawable.icon_game_normal,
			R.drawable.icon_app_normal, R.drawable.icon_theme_normal,
			R.drawable.icon_wallpaper_normal, R.drawable.icon_book_normal };
	static int[] resIconSelectedIds = { R.drawable.icon_game_selected,
			R.drawable.icon_app_selected, R.drawable.icon_theme_selected,
			R.drawable.icon_wallpaper_selected, R.drawable.icon_book_selected };
	static String[] urls = { "http://market.moreapp.cn/3g/category.php?pid=1",
			"http://market.moreapp.cn/3g/category.php?pid=22",
			"http://market.moreapp.cn/3g/items.php?pid=52",
			"http://market.moreapp.cn/3g/items.php?pid=52",
			"http://market.moreapp.cn/3g/items.php?pid=8" };
	//
	String adUrl = "http://market.moreapp.cn/3g/items.php?pid=7";
	public static String homeTabUrls[] = {
			"http://market.moreapp.cn/3g/items.php?pid=7",
			"http://market.moreapp.cn/3g/items.php?pid=29",
			"http://market.moreapp.cn/3g/items.php?pid=8" };

	public static String listTabUrls[] = { "http://market.moreapp.cn/3g/items.php?pid=7",
			"http://market.moreapp.cn/3g/items.php?pid=29",
			"http://market.moreapp.cn/3g/items.php?pid=8" };

	/**
	 * 
	 * @param activity
	 * @param resId
	 */
	static public void switchNavBar(final Activity activity, int resId) {
		//
		int count = resTabIds.length;
		for (int i = 0; i < count; i++) {
			View vTab = activity.findViewById(resTabIds[i]);
			vTab.setTag(i);
			ImageView vIcon = (ImageView) vTab.findViewById(R.id.nav_icon);
			TextView vText = (TextView) vTab.findViewById(R.id.nav_text);
			if (resTabIds[i] == resId) {
				vTab.setBackgroundResource(R.drawable.nav_bar_focused);
				vIcon.setImageResource(resIconSelectedIds[i]);
				vText.setTextColor(0xFFFFFFFF);
			} else {
				vTab.setBackgroundResource(R.drawable.nav_background);
				vIcon.setImageResource(resIconNormalIds[i]);
				vText.setTextColor(0xFF000000);
				//
				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						int pos = (Integer) arg0.getTag();
						Intent intent = null;
						if (pos == 0 || pos == 1) {
							intent = new Intent(activity,
									MarketCategoryActivity.class);
						} else {
							intent = new Intent(activity,
									MarketListActivity.class);
						}
						int index = (Integer) arg0.getTag();
						intent.putExtra(ContantValue.EXTRA_URL, urls[index]);
						intent.putExtra(ContantValue.EXTRA_PAGEID,
								Commond.PAGE_CHANNEL);
						intent.putExtra("resid", arg0.getId());
						activity.startActivity(intent);
						activity.finish();
					}
				};
				//
				vTab.setOnClickListener(listener);
			}

		}
	}

	/**
	 * 
	 * @param activity
	 */
	static public void loadHomeTab(final Activity activity) {
		View v = activity.findViewById(R.id.btn_home);
		v.setBackgroundResource(R.drawable.nav_background);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, MarketHomeActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		};
		//
		v.setOnClickListener(listener);
	}

	/**
	 * 
	 * @param activity
	 */
	static public void loadAdminTab(final Activity activity, boolean isShow) {
		View v = activity.findViewById(R.id.btn_admin);
		if (isShow)
			v.setVisibility(View.VISIBLE);
		else {
			v.setVisibility(View.GONE);
			return;
		}
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, ManageActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		};
		//
		v.setOnClickListener(listener);
	}

	/**
	 * 
	 * @param activity
	 */
	static public void loadSearchTab(final Activity activity, boolean isShow) {
		View v = activity.findViewById(R.id.btn_search);
		if (isShow)
			v.setVisibility(View.VISIBLE);
		else {
			v.setVisibility(View.GONE);
			return;
		}
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, MarketSearchActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		};
		//
		v.setOnClickListener(listener);
	}

	/**
	 * 
	 * @param activity
	 */
	static public void loadBackBtn(final Activity activity, int resId,
			int bgDrawableId) {
		View v = activity.findViewById(resId);
		v.setBackgroundResource(bgDrawableId);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		};
		//
		v.setOnClickListener(listener);
	}
}