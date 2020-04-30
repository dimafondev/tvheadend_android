package com.dimafon.tvhclient.adapter;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dimafon.tvhclient.R;
import com.dimafon.tvhclient.TVHClientApplication;
import com.dimafon.tvhclient.ApplicationModel;
import com.dimafon.tvhclient.action.ActionUtil;
import com.dimafon.tvhclient.model.Programme;

public class ProgrammeArrayAdapter extends ArrayAdapter<Programme> implements OnClickListener {
	private static final String tag = "ProgrammeArrayAdapter";
	private Context context;
	private SparseArray<String> contentTypes;
	private TVHClientApplication app;

	public ProgrammeArrayAdapter(Activity context, int textViewResourceId, List<Programme> programmes) {
		super(context, textViewResourceId, programmes);
		this.context = context;
		contentTypes = ApplicationModel.getContentTypes(context);
		app = ((TVHClientApplication) context.getApplication());
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		boolean isCreated = false;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.programme_line, parent, false);
			isCreated = true;
		}

		final Programme p = getItem(position);
		boolean expanded = app.isExpanded(p);

		TextView title = (TextView) row.findViewById(R.id.p_name);
		final TextView description = (TextView) row.findViewById(R.id.p_desc);
		description.setTag(p);
//		TextView seriesInfo = (TextView) row.findViewById(R.id.p_series_info);

//		TextView time = (TextView) row.findViewById(R.id.p_time);
		TextView date = (TextView) row.findViewById(R.id.p_date);
		ImageButton loadBtn = (ImageButton) row.findViewById(R.id.load_progs);

		final CheckBox expandBtn = (CheckBox) row.findViewById(R.id.expand_prog_btn);
		expandBtn.setTag(description);
		expandBtn.setChecked(expanded);
		if (isCreated)
			expandBtn.setOnClickListener(this);

		title.setText(p.title);
		AdapterUtil.updateIcon(row, p.title, null, AdapterUtil.getStateImage(p), false);

		title.invalidate();

		String s = AdapterUtil.buildSeriesInfoString(p.seriesInfo, context.getResources());
		if (s.length() == 0) {
			s = contentTypes.get(p.contentType);
		}

//		seriesInfo.setText(s);
//		seriesInfo.invalidate();
//
//		date.setText(AdapterUtil.getDateString(p, context));
//		date.invalidate();
//
//		time.setText(AdapterUtil.getTimeSpan(p, getContext()));
//		time.invalidate();
		
		date.setText(Html.fromHtml("<font color='#cc0000'>"+AdapterUtil.getDateString(p, context)+" "+AdapterUtil.getTimeSpan(p, getContext())+"</font> | <font color='#0099cc'>"+s+"</font>"));
		date.invalidate();
		//last position
		if((getCount()-1)==position){
			loadBtn.setVisibility(View.VISIBLE);
			loadBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ActionUtil.loadProgramms(v.getContext(),p.channel);
				}
			});
		}else{
			loadBtn.setVisibility(View.GONE);
		}

		AdapterUtil.updateExpandableDescription(expanded, description, expandBtn, p.description);
		return row;
	}


	@Override
	public void onClick(View v) {
		TextView tv = (TextView) v.getTag();
		boolean checked = ((CheckBox) v).isChecked();
		if (checked) {
			AdapterUtil.expand(tv);
		} else
			AdapterUtil.collapse(tv);
		app.setExpanded((Programme) tv.getTag(), checked);
	}

    public void sort() {
        sort(new Comparator<Programme>() {

            public int compare(Programme x, Programme y) {
                return x.compareTo(y);
            }
        });
    }

}