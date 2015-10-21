package cn.duome.fotoshare.entity;

import java.util.Hashtable;

public class RequestDataEntity {
	int pageID;
	Hashtable<Object, Object> items;

	public RequestDataEntity() {
		items = new Hashtable<Object, Object>(0);
	}

	public Hashtable<Object, Object> getItems() {
		return items;
	}

	public void setItems(Hashtable<Object, Object> items) {
		this.items = items;
	}

	public int getPageID() {
		return pageID;
	}

	public void setPageID(int pageID) {
		this.pageID = pageID;
	}
}