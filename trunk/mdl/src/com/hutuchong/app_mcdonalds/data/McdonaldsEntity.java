package com.hutuchong.app_mcdonalds.data;

import org.gnu.stealthp.rsslib.RSSChannel;
import org.gnu.stealthp.rsslib.RSSItem;

public class McdonaldsEntity {
	private Object tag;
	private boolean selected = false;
	private int num = 0;
	private float price = 0.0f;
	private float save = 0.0f;
	private String id;
	private String name;
	public String[] names;

	//
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	//
	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getSave() {
		return save;
	}

	public void setSave(float save) {
		this.save = save;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		StringBuffer str = new StringBuffer();
		String[] strs = name.split("\\+");
		names = new String[strs.length];
		for (int i = 0; i < strs.length; i++) {
			names[i] = strs[i];
			str = str.append(strs[i]);
			if (i < strs.length - 1) {
				str = str.append('\n');
				str = str.append('+');
			}
		}
		this.name = str.toString();
	}

	/**
	 * 
	 */
	static public String[] totalPrice(RSSChannel channel) {
		float totalPrice = 0.0f;
		float savePrice = 0.0f;
		if (channel != null)
			for (RSSItem item : channel.getItems()) {
				McdonaldsEntity entity = (McdonaldsEntity) item.getTag();
				if (entity != null && entity.isSelected()) {
					totalPrice += entity.getPrice() * entity.getNum();
					savePrice += entity.getSave() * entity.getNum();
				}
			}
		String[] prices = new String[2];
		prices[0] = Float.toString(totalPrice);
		prices[1] = Float.toString(savePrice);
		return prices;
	}
}