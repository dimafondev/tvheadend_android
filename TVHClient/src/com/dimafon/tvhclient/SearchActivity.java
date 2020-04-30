package com.dimafon.tvhclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.dimafon.tvhclient.action.ActionUtil;
import com.dimafon.tvhclient.adapter.AdapterUtil;
import com.dimafon.tvhclient.htsp.HTSListener;
import com.dimafon.tvhclient.htsp.HTSService;
import com.dimafon.tvhclient.model.Channel;
import com.dimafon.tvhclient.model.Programme;
import com.dimafon.tvhclient.model.Recording;

public class SearchActivity extends ListActivity implements HTSListener,
		OnClickListener {

	private SearchResultAdapter srAdapter;
	private SparseArray<String> contentTypes;
	private Pattern pattern;
	private Channel channel;
	private SearchView searchView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		contentTypes = ApplicationModel.getContentTypes(this);

		List<Programme> srList = new ArrayList<Programme>();
		srAdapter = new SearchResultAdapter(this, srList);
		srAdapter.sort();
		setListAdapter(srAdapter);

		onNewIntent(getIntent());
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (!Intent.ACTION_SEARCH.equals(intent.getAction())
				|| !intent.hasExtra(SearchManager.QUERY)) {
			return;
		}

		ApplicationModel app = (ApplicationModel) getApplication();

		Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
		if (appData != null) {
			channel = app.getChannel(appData.getLong("channelId"));
		} else {
			channel = null;
		}

		srAdapter.clear();
		String query = intent.getStringExtra(SearchManager.QUERY);
		pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
		intent = new Intent(SearchActivity.this, HTSService.class);
		intent.setAction(HTSService.ACTION_EPG_QUERY);
		intent.putExtra("query", query);
		if (channel != null) {
			intent.putExtra("channelId", channel.id);
		}

		startService(intent);

		if (channel == null) {
			for (Channel ch : app.getChannels()) {
				for (Programme p : ch.epg) {
					if (p.title != null && pattern.matcher(p.title).find()) {
						srAdapter.add(p);
					}
				}
			}
		} else {
			for (Programme p : channel.epg) {
				if (p.title != null && pattern.matcher(p.title).find()) {
					srAdapter.add(p);
				}
			}
		}
		getActionBar().setTitle(
				this.getString(android.R.string.search_go) + ": " + query);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ApplicationModel app = (ApplicationModel) getApplication();
		app.addListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ApplicationModel app = (ApplicationModel) getApplication();
		app.removeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = (MenuItem) menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Programme prog = (Programme) srAdapter.getItem(position);
		ActionUtil.switchRecording(this, Collections.singletonList(prog));
		srAdapter.notifyDataSetChanged();
	}

	public void onMessage(String action, final Object obj) {
		if (action.equals(ApplicationModel.ACTION_PROGRAMME_ADD)) {
			runOnUiThread(new Runnable() {

				public void run() {
					Programme p = (Programme) obj;
					if (pattern != null && p.title!=null && pattern.matcher(p.title).find()) {
						srAdapter.add(p);
						srAdapter.notifyDataSetChanged();
						srAdapter.sort();
					}
				}
			});
		} else if (action.equals(ApplicationModel.ACTION_PROGRAMME_DELETE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					Programme p = (Programme) obj;
					srAdapter.remove(p);
					srAdapter.notifyDataSetChanged();
				}
			});
		} else if (action.equals(ApplicationModel.ACTION_PROGRAMME_UPDATE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					Programme p = (Programme) obj;
					srAdapter.updateView(getListView(), p);
				}
			});
		} else if (action.equals(ApplicationModel.ACTION_DVR_UPDATE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					Recording rec = (Recording) obj;
					for (Programme p : srAdapter.list) {
						if (rec == p.recording) {
							srAdapter.updateView(getListView(), p);
							return;
						}
					}
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		TextView tv = (TextView) v.getTag();
		boolean checked = ((CheckBox) v).isChecked();
		if (checked) {
			AdapterUtil.expand(tv);
		} else
			AdapterUtil.collapse(tv);
		TVHClientApplication app = (TVHClientApplication) getApplication();
		app.setExpanded((Programme) tv.getTag(), checked);
	}

	private class ViewWarpper {

		TextView title;
		TextView channel;
		TextView time;
		TextView date;
		TextView description;
		View row;
		CheckBox expandBtn;

		public ViewWarpper(View base) {
			row = base;
			title = (TextView) base.findViewById(R.id.p_name);
			channel = (TextView) base.findViewById(R.id.p_series_info);
			description = (TextView) base.findViewById(R.id.p_desc);
			expandBtn = (CheckBox) row.findViewById(R.id.expand_prog_btn);
			expandBtn.setOnClickListener(SearchActivity.this);

			time = (TextView) base.findViewById(R.id.p_time);
			date = (TextView) base.findViewById(R.id.p_date);
		}

		public void repaint(Programme p) {
			Channel ch = p.channel;

			title.setText(p.title);
			title.invalidate();

			String s = AdapterUtil.buildSeriesInfoString(p.seriesInfo,
					getResources());
			if (s.length() > 0) {
				channel.setText(ch.name + " / " + s);
			} else {
				String contentType = contentTypes.get(p.contentType, "");
				if (contentType.length() > 0) {
					channel.setText(ch.name + " / " + contentType);
				} else {
					channel.setText(ch.name);
				}
			}
			channel.invalidate();

			date.setText(AdapterUtil.getDateString(p, date.getContext()));
			date.invalidate();

			time.setText(AdapterUtil.getTimeSpan(p, time.getContext()));
			time.invalidate();

			AdapterUtil.updateIcon(row, p.channel.name, p.channel.iconBitmap,
					AdapterUtil.getStateImage(p), false);

			TVHClientApplication app = (TVHClientApplication) getApplication();
			boolean expanded = app.isExpanded(p);
			expandBtn.setTag(description);
			expandBtn.setChecked(expanded);
			description.setTag(p);
			AdapterUtil.updateExpandableDescription(expanded, description,
					expandBtn, p.description);

		}
	}

	class SearchResultAdapter extends ArrayAdapter<Programme> {

		Activity context;
		List<Programme> list;

		SearchResultAdapter(Activity context, List<Programme> list) {
			super(context, R.layout.search_result_widget, list);
			this.context = context;
			this.list = list;
		}

		public void sort() {
			sort(new Comparator<Programme>() {

				public int compare(Programme x, Programme y) {
					return x.compareTo(y);
				}
			});
		}

		public void updateView(ListView listView, Programme programme) {
			for (int i = 0; i < listView.getChildCount(); i++) {
				View view = listView.getChildAt(i);
				int pos = listView.getPositionForView(view);
				Programme pr = (Programme) listView.getItemAtPosition(pos);

				if (view.getTag() == null || pr == null) {
					continue;
				}

				if (programme.id != pr.id) {
					continue;
				}

				ViewWarpper wrapper = (ViewWarpper) view.getTag();
				wrapper.repaint(programme);
				break;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewWarpper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(R.layout.search_result, null, false);

				wrapper = new ViewWarpper(row);
				row.setTag(wrapper);

			} else {
				wrapper = (ViewWarpper) row.getTag();
			}

			Programme p = getItem(position);
			wrapper.repaint(p);
			return row;
		}
	}
}
