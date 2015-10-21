package com.hutuchong.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import mobi.domore.mcdonalds.R;

public class AdUtils {
	//
	public static int PAGE_LIST = 0;
	public static int PAGE_DETAIL = 1;
	public static int showAdCount = 0;
	public static boolean ishackad = false;

	/**
	 * 
	 * @param activity
	 * @param showAd
	 * @param pos
	 */
	public static void setShowAd(final Activity activity, String strShowAd,
			int posType, int adMoreId) {
		FrameLayout llView = (FrameLayout) activity.findViewById(R.id.ad_panel);
		if (llView == null)
			return;
		// Debug.d("strShowAd:" + strShowAd);
		// strShowAd = "41";
		if (!TextUtils.isEmpty(strShowAd) && TextUtils.isDigitsOnly(strShowAd)) {
			int showAd = Integer.parseInt(strShowAd);
			//
			int layout = R.layout.ad_panel_wooboo;
			boolean isshow = false;
			if (showAd == 1 || showAd == 10 || showAd == 11 || showAd == 111) {
				layout = R.layout.ad_panel_admob;
				if (showAd == 1 || (showAd == 11 && posType == PAGE_LIST)
						|| (showAd == 111 && posType == PAGE_DETAIL))
					isshow = true;

			} else if (showAd == 2 || showAd == 20 || showAd == 21
					|| showAd == 211) {
				layout = R.layout.ad_panel_wooboo;
				if (showAd == 2 || (showAd == 21 && posType == PAGE_LIST)
						|| (showAd == 211 && posType == PAGE_DETAIL))
					isshow = true;

			} else if (showAd == 3 || showAd == 30 || showAd == 31
					|| showAd == 311) {
				layout = R.layout.ad_panel_youmi;
				if (showAd == 3 || (showAd == 31 && posType == PAGE_LIST)
						|| (showAd == 311 && posType == PAGE_DETAIL))
					// AdManager.init("c7776fe83b9f7a0f", "9021d3bcfcc1868a",
					// 20, true,"1.1");
					isshow = true;

			} else if (showAd == 4 || showAd == 40 || showAd == 41
					|| showAd == 411) {
				layout = R.layout.ad_panel_casee;
				if (showAd == 4 || (showAd == 41 && posType == PAGE_LIST)
						|| (showAd == 411 && posType == PAGE_DETAIL))
					isshow = true;

			} else if (showAd == 5 || showAd == 50 || showAd == 51
					|| showAd == 511) {
				layout = R.layout.ad_panel_mobus;
				if (showAd == 5 || (showAd == 51 && posType == PAGE_LIST)
						|| (showAd == 511 && posType == PAGE_DETAIL))
					isshow = true;
			} else if (showAd == 6 || showAd == 60 || showAd == 61
					|| showAd == 611) {
				layout = R.layout.ad_panel_smartmad;
				if (showAd == 6 || (showAd == 61 && posType == PAGE_LIST)
						|| (showAd == 611 && posType == PAGE_DETAIL))
					isshow = true;
			} else if (showAd == 7 || showAd == 70 || showAd == 71
					|| showAd == 711) {
				// layout = R.layout.ad_panel_adtouch;
				if (showAd == 7 || (showAd == 71 && posType == PAGE_LIST)
						|| (showAd == 711 && posType == PAGE_DETAIL))
					isshow = true;
			}
			//
			if (!isshow /* || isMaxClicked */) {
				llView.setVisibility(View.GONE);
			} else {
				llView.setVisibility(View.VISIBLE);
			}
			//
			if (llView.findViewById(R.id.adview) == null) {
				LayoutInflater vi = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout adView = (LinearLayout) vi.inflate(layout, null);
				llView.addView(adView, 0);
			}
		}
	}
}
