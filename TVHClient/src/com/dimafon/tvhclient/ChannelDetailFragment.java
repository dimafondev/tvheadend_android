package com.dimafon.tvhclient;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.dimafon.tvhclient.action.ActionUtil;
import com.dimafon.tvhclient.adapter.ProgrammeArrayAdapter;
import com.dimafon.tvhclient.model.Channel;
import com.dimafon.tvhclient.model.Programme;
import com.dimafon.tvhclient.model.Recording;

/**
 * A list fragment representing a list of Channels. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ChannelDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ChannelDetailFragment extends ListFragment {

	public static final String ARG_ITEM_ID = "item_id";
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	protected Channel channel;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChannelDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			channel = ((TVHClientApplication) getActivity().getApplication())
					.getChannel(getArguments().getLong(ARG_ITEM_ID));
			if (channel != null) {
				if( !(getActivity() instanceof ChannelListActivity && ((ChannelListActivity)getActivity()).isTwoPane())){
					getActivity().getActionBar().setTitle(channel.name);
				}
				if (getListAdapter() == null)
					setListAdapter(new ProgrammeArrayAdapter(getActivity(),
							R.layout.programme_line, new ArrayList<Programme>(
									channel.epg)));
			}
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		ProgrammeArrayAdapter adapter = (ProgrammeArrayAdapter) getListAdapter();
		Programme prog = adapter.getItem(position);
		ActionUtil.switchRecording(getActivity(),
				Collections.singletonList(prog));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void addProgramm(Programme p) {
		ProgrammeArrayAdapter prAdapter = (ProgrammeArrayAdapter) getListAdapter();
		if (prAdapter != null) {
			prAdapter.add(p);
			prAdapter.sort();
			prAdapter.notifyDataSetChanged();
		}
	}

	public void removeProgramm(Programme p) {
		ProgrammeArrayAdapter prAdapter = (ProgrammeArrayAdapter) getListAdapter();
		if (prAdapter != null) {
			prAdapter.remove(p);
			prAdapter.notifyDataSetChanged();
		}
	}

	public void updateProgramm(Programme p) {
		ProgrammeArrayAdapter prAdapter = (ProgrammeArrayAdapter) getListAdapter();
		if (prAdapter != null) {
			prAdapter.notifyDataSetChanged();
		}
	}

	public void updateForRecording(Recording rec) {
		ProgrammeArrayAdapter prAdapter = (ProgrammeArrayAdapter) getListAdapter();
		if (prAdapter != null) {
			if(channel!=null && channel.equals(rec.channel)){
				prAdapter.notifyDataSetChanged();
			}
		}
	}

}
