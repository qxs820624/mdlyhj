package com.example.bean;

public class MusicBean {
	private int chartCode = 0;
	private String chartName = "";

	public MusicBean(int chartCode, String chartName) {
		super();
		this.chartCode = chartCode;
		this.chartName = chartName;
	}

	public MusicBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getChartCode() {
		return chartCode;
	}

	public void setChartCode(int chartCode) {
		this.chartCode = chartCode;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "chartCode:" + this.chartCode + ",chartName:" + this.chartName;
	}
}
