package cn.duome.dkymarket.entity;

import android.graphics.Bitmap;

public class Mycommentinfo {
	String comment;
	String title;
	String header;

	public Mycommentinfo(String comment, String title, String header) {
		// TODO Auto-generated constructor stub
		this.comment = comment;
		this.title = title;
		this.header = header;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return "Mycommentinfo [comment=" + comment + ", title=" + title
				+ ", header=" + header + "]";
	}

}
