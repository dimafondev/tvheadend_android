package com.dimafon.tvhclient.adapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dimafon.tvhclient.R;
import com.dimafon.tvhclient.model.Channel;
import com.dimafon.tvhclient.model.Programme;

public class ChannelArrayAdapter extends ArrayAdapter<Channel> {
	private static final String tag = "ChannelArrayAdapter";
	private OnClickListener btnClickListenr;

	public ChannelArrayAdapter(Activity context, int textViewResourceId, OnClickListener btnClickListenr) {
		super(context, textViewResourceId, new ArrayList<Channel>());
		this.btnClickListenr = btnClickListenr;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		boolean isCreated = false;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.channel_line, parent, false);
			isCreated = true;
		}

		Channel channel = getItem(position);

		TextView channelName = (TextView) row.findViewById(R.id.tv_ch_name);
		TextView currentEpg = (TextView) row.findViewById(R.id.tv_current);
		TextView next1Epg = (TextView) row.findViewById(R.id.tv_nex1);
		TextView next2Epg = (TextView) row.findViewById(R.id.tv_next2);
		ProgressBar progress = (ProgressBar) row.findViewById(R.id.tv_progress);
		ImageButton expandBtn = (ImageButton) row.findViewById(R.id.expand_channel_btn);
		expandBtn.setTag(channel);
		expandBtn.setVisibility(channel.epg.isEmpty() ? View.GONE : View.VISIBLE);
		if (isCreated)
			expandBtn.setOnClickListener(btnClickListenr);

		currentEpg.setText("");
		next1Epg.setText("");
		next2Epg.setText("");
		progress.setProgress(0);

		String name = channel.name;
		channelName.setText(name);
		channelName.invalidate();

		AdapterUtil.updateIcon(row, name, channel.iconBitmap, channel.isRecording() ? R.drawable.ic_record : 0,false);

		String lastTime = null;

		Iterator<Programme> it = channel.epg.iterator();
		if (!channel.isTransmitting && it.hasNext()) {
			currentEpg.setText(R.string.ch_no_transmission);
		} else {
			Programme p = updateNextProgramm(it, currentEpg, lastTime);
			if (p != null) {
				double duration = (p.stop.getTime() - p.start.getTime());
				double elapsed = new Date().getTime() - p.start.getTime();
				double percent = elapsed / duration;
				progress.setVisibility(View.VISIBLE);
				progress.setProgress((int) Math.floor(percent * 10000));
				lastTime = getLastTime(p);
			} else {
				progress.setVisibility(ImageView.GONE);
			}
		}

		lastTime = getLastTime(updateNextProgramm(it, next1Epg, lastTime));
		updateNextProgramm(it, next2Epg, lastTime);
		return row;
	}

	private Programme iterateOverPast(Iterator<Programme> it) {
		long now = System.currentTimeMillis();
		while(it.hasNext()){
			Programme p = it.next();
			if(p.stop.getTime() > now){
				return p;
			}
		}
		return null;
	}

	private String getLastTime(Programme p) {

		return p != null ? DateFormat.getTimeFormat(getContext()).format(p.stop) : null;
	}

	public void sort() {
		sort(new Comparator<Channel>() {
			public int compare(Channel ch1, Channel ch2) {
				return ch1.compareTo(ch2);
			}
		});		 
	}

	private Programme updateNextProgramm(Iterator<Programme> it, TextView text, String lastTime) {
		Programme p = iterateOverPast(it);
	
		if (p!=null) {
			text.setText(DateFormat.getTimeFormat(getContext()).format(p.start) + " " + p.title);
			text.setVisibility(View.VISIBLE);
		} else {
			if (lastTime != null){
				text.setText(lastTime + " ...");
				text.setVisibility(View.VISIBLE);
			}else text.setVisibility(View.GONE);

		}
		text.invalidate();
		return p;
	}

}