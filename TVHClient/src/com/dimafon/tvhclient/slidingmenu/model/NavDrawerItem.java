package com.dimafon.tvhclient.slidingmenu.model;

import org.tvheadend.tvhguide.model.ChannelTag;

public class NavDrawerItem {
	private String title;
	private int icon;
	private String count = "0";
	private boolean isCounterVisible = false;
	private float[] color;
	private ChannelTag channelTag;

	public NavDrawerItem() {
	}

	public NavDrawerItem(String title, int icon) {
		this.title = title;
		this.icon = icon;
	}

	public NavDrawerItem(ChannelTag channelTag, int icon, boolean isCounterVisible, String count,float[] color) {
		this.title = channelTag.name;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
		this.color=color;
		this.channelTag = channelTag;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public String getCount() {
		return this.count;
	}

	public boolean getCounterVisibility() {
		return this.isCounterVisible;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setCounterVisibility(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	public float[] getColor() {
		return color;
	}

	public ChannelTag getChannelTag() {
		return channelTag;
	}


}