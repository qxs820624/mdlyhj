package com.example.musicutil;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

public class UIEvent {
	private static final String TAG = "UIEvent";

	private List<Handler> needToReflashList = null;

	private static UIEvent uiEvent = null;

	public static UIEvent getInstance() {
		if (uiEvent == null) {
			uiEvent = new UIEvent();
		}
		return uiEvent;
	}

	private UIEvent() {
		needToReflashList = new ArrayList<Handler>();
	}

	public void register(Handler item) {
		needToReflashList.add(item);
	}

	public void remove(Handler item) {
		needToReflashList.remove(item);
	}

	public void notifications() {
		if (needToReflashList == null && needToReflashList.isEmpty()) {
			return;
		} else {
			for (Handler item : needToReflashList) {
				item.sendEmptyMessage(1);
			}
		}
	}

	public void notifications(int flag) {
		if (needToReflashList == null && needToReflashList.isEmpty()) {
			return;
		} else {
			for (Handler item : needToReflashList) {
				item.sendEmptyMessage(flag);
			}
		}
	}

	public void notifications(int flag, int now, int max) {
		if (needToReflashList == null && needToReflashList.isEmpty()) {
			return;
		} else {
			for (Handler item : needToReflashList) {
				Message a = Message.obtain();
				a.what = flag;
				a.arg1 = now;
				a.arg2 = max;
				item.sendMessage(a);
			}
		}
	}
}
