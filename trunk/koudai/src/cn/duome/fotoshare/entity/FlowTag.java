package cn.duome.fotoshare.entity;

import android.content.Context;

public class FlowTag {
	private int flowIndex;
	private String fileName;
	public final int what = 1;
	private Context context;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getFlowIndex() {
		return flowIndex;
	}

	public void setFlowIndex(int flowIndex) {
		this.flowIndex = flowIndex;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private int ItemWidth;

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}

	private int ItemHeight;

	public int getItemHeight() {
		return ItemHeight;
	}

	public void setItemHeight(int itemHeight) {
		ItemHeight = itemHeight;
	}
}
