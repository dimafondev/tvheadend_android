package com.dimafon.tvhclient.slidingmenu.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimafon.tvhclient.R;
import com.dimafon.tvhclient.slidingmenu.model.NavDrawerItem;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

	private Context context;

	public NavDrawerListAdapter(Context context, List<NavDrawerItem> navDrawerItems) {
		super(context, 0, navDrawerItems);
		this.context = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
		NavDrawerItem item = getItem(position);
		int icon = item.getIcon();
		if(icon!=0){
			imgIcon.setVisibility(View.VISIBLE);
			imgIcon.setImageResource(icon);
		}else{
			imgIcon.setVisibility(View.INVISIBLE);
		}
		txtTitle.setText(item.getTitle());
		float[] hsv  = item.getColor();
		if(hsv!=null){
			txtTitle.setTextColor(Color.HSVToColor( hsv ));
		}
		
		// displaying count
		// check whether it set visible or not
		if (getItem(position).getCounterVisibility()) {
			txtCount.setText(getItem(position).getCount());
			txtCount.setVisibility(View.VISIBLE);
			txtCount.invalidate();
		} else {
			// hide the counter view
			txtCount.setVisibility(View.GONE);
		}

		return convertView;
	}

}