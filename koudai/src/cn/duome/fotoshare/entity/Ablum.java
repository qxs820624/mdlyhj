package cn.duome.fotoshare.entity;

import java.util.ArrayList;

public class Ablum {
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	Item item = new Item();

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	String count;
	public ArrayList<Item> items = new ArrayList<Item>();
}