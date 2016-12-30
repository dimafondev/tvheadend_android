package com.dimafon.tvhclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.tvheadend.tvhguide.ExternalPlaybackActivity;
import org.tvheadend.tvhguide.htsp.HTSConnection;
import org.tvheadend.tvhguide.htsp.HTSService;
import org.tvheadend.tvhguide.model.Recording;

import com.dimafon.tvhclient.action.ActionUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Channel detail screen. This fragment is
 * either contained in a {@link ChannelListActivity} in two-pane mode (on
 * tablets) or a {@link ChannelDetailActivity} on handsets.
 */
public class RecordDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	private static Recording rec;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RecordDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			rec = ((TVHClientApplication) getActivity().getApplication()).getRecording(getArguments().getLong(ARG_ITEM_ID));
			if(rec!=null){
				getActivity().getActionBar().setTitle(rec.title);
			}
		}
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_record_detail, container, false);
		TextView text = (TextView) rootView.findViewById(R.id.rec_name);
		text.setText(rec.title);

		text = (TextView) rootView.findViewById(R.id.rec_summary);
		text.setText(rec.summary);
		if (rec.summary.length() == 0)
			text.setVisibility(TextView.GONE);

		text = (TextView) rootView.findViewById(R.id.rec_desc);
		text.setText(rec.description);

		text = (TextView) rootView.findViewById(R.id.rec_time);
		text.setText(DateFormat.getLongDateFormat(getActivity()).format(rec.start) + "   " + DateFormat.getTimeFormat(getActivity()).format(rec.start) + " - "
				+ DateFormat.getTimeFormat(getActivity()).format(rec.stop));
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (rec != null) {
			inflater.inflate(R.menu.recording, menu);
			if (rec.isRecording() || rec.isScheduled()) {
				menu.removeItem(R.id.action_play);
			}
		} else
			super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_play:
			ActionUtil.playRecording(this, rec.id);
			return true;
		case R.id.action_delete:
			ActionUtil.deleteCancelRecordings(getActivity(), Collections.singletonList(rec.id));
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
