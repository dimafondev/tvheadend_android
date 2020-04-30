package com.dimafon.tvhclient.slidingmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;

import com.dimafon.tvhclient.R;
import com.dimafon.tvhclient.TVHClientApplication;
import com.dimafon.tvhclient.model.Channel;
import com.dimafon.tvhclient.model.ChannelTag;
import com.dimafon.tvhclient.slidingmenu.model.NavDrawerItem;

public class NavigationUtil {

	private static final NavDrawerItem ALL_ITEM = new NavDrawerItem("TV Now", R.drawable.ic_menu_tv);
	public static final NavDrawerItem RECORDS_ITEM = new NavDrawerItem("Records", R.drawable.ic_menu_film);
	public static final NavDrawerItem SETTING_ITEM = new NavDrawerItem("Settings", R.drawable.ic_action_settings);
	final private static float HUES[] = new float[] { 0.0f, 180.0f, 270.0f, 90.0f, 306.0f, 126.0f, 216.0f, 36.0f, 234.0f, 54.0f, 324.0f, 144.0f, 342.0f, 162.0f, 18.0f, 198.0f,
			72.0f, 252.0f, 108.0f, 288.0f, };

	public static float[] getHSV(int index, int length) {
		return new float[] { HUES[index % HUES.length]/* div*num */, 1.0f, 0.5f };
	}

	private static List<Integer> getLongSequences(boolean[] indexSelector) {
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		int start = -1;
		for (int i = 1; i < indexSelector.length; i++) {
			if (!indexSelector[i]) {
				if (start < 0) {
					start = i;
				}
				if ((i + 1) == indexSelector.length && start > 0) {
					int diff = i - start;
					addToMap(map, start, i, diff);
				}
			} else {
				if (start > 0) {
					int end = i - 1;
					int diff = end - start;
					addToMap(map, start, end, diff);
					start = -1;
				}
			}
		}
		Integer max = Collections.max(map.keySet());
		if (max != null) {
			return map.get(max);
		}
		return Collections.EMPTY_LIST;
	}

	private static void addToMap(Map<Integer, List<Integer>> map, int start, int end, int diff) {
		List<Integer> list = map.get(diff);
		if (list == null) {
			list = new ArrayList<Integer>();
			map.put(diff, list);
		}
		list.add((start + end) / 2);
	}

	public static boolean updateNavItemCount(TVHClientApplication app,NavDrawerItem item){
		String count = countChannels(app,item.getChannelTag());
		if(count!=null && !count.equals(item.getCount())){
			item.setCount(count);
			return true;
		}
		return false;
	}
	public static List<NavDrawerItem> getNavigationItems(TVHClientApplication app) {
		ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();
		List<ChannelTag> tags = app.getChannelTags();
		navDrawerItems.add(ALL_ITEM);
		int i = 0;
		for (ChannelTag channelTag : tags) {
			NavDrawerItem item = new NavDrawerItem(channelTag, 0, true, countChannels(app,channelTag), NavigationUtil.getHSV(i++, tags.size()));
			navDrawerItems.add(item);
		}
		navDrawerItems.add(RECORDS_ITEM);
		navDrawerItems.add(SETTING_ITEM);
		return navDrawerItems;
	}

	private static String countChannels(TVHClientApplication app,ChannelTag channelTag) {
		int counter = 0;
		List<Channel> chanells = app.getChannels();
		for (Channel channel : chanells) {
			if(channel.tags.contains((int)channelTag.id)){
				counter++;
			}
		}
		return counter+"";
	}

	public static int getColorForText(String text) {
		int index = 0;
		for (byte b : text.getBytes()) {
			index += b;
		}
		return Color.HSVToColor(getHSV(Math.abs(index), 20));
	}

}
