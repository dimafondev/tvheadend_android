package com.dimafon.tvhclient.adapter;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tvheadend.tvhguide.model.Channel;
import org.tvheadend.tvhguide.model.Programme;
import org.tvheadend.tvhguide.model.Recording;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dimafon.tvhclient.R;

public class RecordsArrayAdapter extends ArrayAdapter<Recording> {
	private static final long SIX_DAYS = 1000*60*60*24*6;
	private static final long TWO_DAYS = 1000*60*60*24*2;
	private static final String tag = "RecordsArrayAdapter";
	private Context context;
	private OnClickListener btnClickListenr;
	private Set<Long> selector = new HashSet<Long>();

	public RecordsArrayAdapter(Activity context, int textViewResourceId, OnClickListener btnClickListenr, List<Recording> recordings) {
		super(context, textViewResourceId, recordings);
		this.context = context;
		this.btnClickListenr = btnClickListenr;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		boolean isCreated = false;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.recording_line, parent, false);
			isCreated = true;
		}
		if(position>getCount()) return row;
		Recording rec = getItem(position);
		
        TextView title = (TextView) row.findViewById(R.id.r_name);
        TextView channel = (TextView) row.findViewById(R.id.r_channel);

        TextView time = (TextView) row.findViewById(R.id.r_time);
        TextView date = (TextView) row.findViewById(R.id.r_date);
//        TextView message = (TextView) row.findViewById(R.id.rec_message);
        TextView desc = (TextView) row.findViewById(R.id.r_desc);

		ImageButton expandBtn = (ImageButton) row.findViewById(R.id.expand_rec_btn);
		expandBtn.setTag(rec);
		if (isCreated)
			expandBtn.setOnClickListener(btnClickListenr);
        Channel ch = rec.channel;

        title.setText(rec.title);
        title.invalidate();
        if(ch != null) {
        	channel.setText(ch.name);
            AdapterUtil.updateIcon(row, ch.name, ch.iconBitmap, getStateImage(rec),selector.contains(rec.id));
        } else {
        	channel.setText("");
            AdapterUtil.updateIcon(row, rec.title, null, getStateImage(rec),selector.contains(rec.id));
        }
        channel.invalidate();
        

        if (DateUtils.isToday(rec.start.getTime())) {
            date.setText(context.getString(R.string.today));
        } else if(rec.start.getTime() < System.currentTimeMillis() + TWO_DAYS &&
                  rec.start.getTime() > System.currentTimeMillis() - TWO_DAYS) {
            date.setText(DateUtils.getRelativeTimeSpanString(rec.start.getTime(),
                    System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS));
        } else if(rec.start.getTime() < System.currentTimeMillis() + SIX_DAYS &&
        		  rec.start.getTime() > System.currentTimeMillis() - TWO_DAYS
        		) {
        	date.setText(new SimpleDateFormat("EEEE").format(rec.start.getTime()));
        } else {
            date.setText(DateFormat.getDateFormat(date.getContext()).format(rec.start));
        }

        date.invalidate();

//        String msg = "";
//        message.invalidate();

        desc.setText(rec.description);
        desc.invalidate();


        time.setText(
                DateFormat.getTimeFormat(time.getContext()).format(rec.start)
                + " - "
                + DateFormat.getTimeFormat(time.getContext()).format(rec.stop));
        time.invalidate();

		return row;
	}


	private static int getStateImage(Recording rec) {
      if (rec.error != null) {
          return R.drawable.ic_err;
      } else if ("completed".equals(rec.state)) {
          return R.drawable.ic_ok;
      } else if ("invalid".equals(rec.state) || "missed".equals(rec.state)) {
          return R.drawable.ic_err;
      } else if ("recording".equals(rec.state)) {
          return R.drawable.ic_record;
      } else if ("scheduled".equals(rec.state)) {
          return R.drawable.ic_clock;
      }
		return 0;
	}

	private static String getStateString(Recording rec,Resources res) {
      if (rec.error != null) {
          return rec.error;
      } else if ("completed".equals(rec.state)) {
          return res.getString(R.string.pvr_completed);
      } else if ("invalid".equals(rec.state)) {
          return res.getString(R.string.pvr_invalid);
      } else if ("missed".equals(rec.state)) {
          return  res.getString(R.string.pvr_missed);
      } else if ("recording".equals(rec.state)) {
          return res.getString(R.string.pvr_recording);
      } else if ("scheduled".equals(rec.state)) {
          return res.getString(R.string.pvr_scheduled);
      }return null;

	}

	public boolean changeSelection(int position) {
		Recording rec = getItem(position);
		if(rec!=null){
			if(!selector.contains(rec.id)){
				selector.add(rec.id);
				return true;
			}
			else selector.remove(rec.id);
		}
		return false;
	}

	public void clearSelection(){
		if(!selector.isEmpty()){
			selector.clear();
			notifyDataSetChanged();
		}
	}
	
	public Collection<Long> getSelectedRecordings(){
		return selector;
	}



	public void animateSelection(int position, View view) {
		View selectImage = view.findViewById(R.id.tv_selected);
		
		View logoView =  view.findViewById(R.id.tv_logot);
		if(!(Boolean)logoView.getTag()){
			logoView =  view.findViewById(R.id.tv_logo);
		}
		if(selectImage.isShown()){
			AdapterUtil.applyRotation(selectImage, logoView);
		}else{
			AdapterUtil.applyRotation(logoView,selectImage);
		}
	}

    public void sort() {
        sort(new Comparator<Recording>() {

            public int compare(Recording x, Recording y) {
                return x.compareTo(y);
            }
        });
    }


}