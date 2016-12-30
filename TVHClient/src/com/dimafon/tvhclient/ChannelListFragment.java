package com.dimafon.tvhclient;

import org.tvheadend.tvhguide.model.Channel;
import org.tvheadend.tvhguide.model.ChannelTag;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.dimafon.tvhclient.adapter.ChannelArrayAdapter;

/**
 * A list fragment representing a list of Channels. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ChannelDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ChannelListFragment extends ListFragment implements OnClickListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(long id);

		public void onItemExpanded(Object obj);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(long id) {
		}

		@Override
		public void onItemExpanded(Object obj) {

		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChannelListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getListAdapter()==null){
			setListAdapter(new ChannelArrayAdapter(getActivity(), R.layout.channel_line, this));
		}
		populateList();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Channel ch = (Channel) getListAdapter().getItem(position);
		if (ch != null)
			mCallbacks.onItemSelected(ch.id);
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
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public void onClick(View v) {
		mCallbacks.onItemExpanded(v.getTag());
	}

	public void add(Channel ch) {
		ChannelArrayAdapter chAdapter = getChannelAdapter();
		if (chAdapter != null) {
			chAdapter.add(ch);
			chAdapter.notifyDataSetChanged();
			chAdapter.sort();
		}
	}

	private ChannelArrayAdapter getChannelAdapter() {
		return (ChannelArrayAdapter) getListAdapter();
	}

	public void remove(Channel ch) {
		ChannelArrayAdapter chAdapter = getChannelAdapter();
		if (chAdapter != null) {
			chAdapter.remove(ch);
			chAdapter.notifyDataSetChanged();
		}
	}

	public void update(Channel channel) {
		ChannelArrayAdapter chAdapter = getChannelAdapter();
		if (chAdapter != null) {
			//			chAdapter.updateView(getListView(), channel);
			chAdapter.notifyDataSetChanged();
		}
	}

	public void populateList() {
		if (getActivity() != null) {
			TVHClientApplication app = (TVHClientApplication) getActivity().getApplication();
			ChannelArrayAdapter chAdapter = getChannelAdapter();
			if (chAdapter != null) {
				chAdapter.clear();
				ChannelTag currentTag = app.getCurrentTag();
				for (Channel ch : app.getChannels()) {
					if (currentTag == null || ch.hasTag(currentTag.id)) {
						chAdapter.add(ch);
					}
				}
				chAdapter.sort();
				chAdapter.notifyDataSetChanged();
			}
		}
	}
}
