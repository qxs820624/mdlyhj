package com.hutuchong.app_mcdonalds.data;

public class FilterEntity {
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private boolean selected = false;
	private String name;
}