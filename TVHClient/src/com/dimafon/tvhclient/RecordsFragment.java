package com.dimafon.tvhclient;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.dimafon.tvhclient.action.ActionUtil;
import com.dimafon.tvhclient.adapter.RecordsArrayAdapter;
import com.dimafon.tvhclient.model.Recording;

/**
 * A list fragment representing a list of Recordings. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link RecordingDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RecordsFragment extends ListFragment implements OnClickListener,
		OnItemLongClickListener, ActionMode.Callback {

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

	private ActionMode mActionMode;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onRecoringSelected(long id);

		public void onRecordingExpanded(Recording obj);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onRecoringSelected(long id) {
		}

		@Override
		public void onRecordingExpanded(Recording obj) {

		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RecordsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getListAdapter() == null) {
			setListAdapter(new RecordsArrayAdapter(getActivity(),
					R.layout.recording_line, this, new ArrayList<Recording>()));
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		populateList();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
		getListView().setOnItemLongClickListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
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
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		Recording ch = (Recording) getListAdapter().getItem(position);
		if (ch != null)
			mCallbacks.onRecoringSelected(ch.id);
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

	@Override
	public void onClick(View v) {
		mCallbacks.onRecordingExpanded((Recording) v.getTag());
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.recording, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		RecordsArrayAdapter adapter = (RecordsArrayAdapter) getListAdapter();
		Collection<Long> recordings = adapter.getSelectedRecordings();
		if (!recordings.isEmpty()) {
			switch (item.getItemId()) {
			case R.id.action_play:
				ActionUtil.playRecording(this, recordings.iterator().next());
				mode.finish();
				return true;
			case R.id.action_delete:
				ActionUtil.deleteCancelRecordings(getActivity(), recordings);
				mode.finish();
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode = null;
		((RecordsArrayAdapter) getListAdapter()).clearSelection();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		RecordsArrayAdapter adapter = (RecordsArrayAdapter) getListAdapter();
		adapter.animateSelection(position, view);
		boolean checked = adapter.changeSelection(position);
		int size = adapter.getSelectedRecordings().size();
		if (checked) {
			if (mActionMode == null) {
				mActionMode = getActivity().startActionMode(this);
				getListView().setSelected(true);
			}
			if (size == 2) {
				showActionItem(R.id.action_play, false);
			}
		} else {
			if (mActionMode != null)
				if (size == 0) {
					mActionMode.finish();
				} else if (size == 1) {
					showActionItem(R.id.action_play, true);
				}
		}
		if (mActionMode != null) {
			mActionMode.setTitle(size + " selected");
		}
		return true;
	}

	private void showActionItem(int id, boolean b) {
		MenuItem item = mActionMode.getMenu().findItem(id);
		if (item != null) {
			item.setVisible(b);
		}
	}

	public void populateList() {
		if (getActivity() != null) {
			TVHClientApplication app = (TVHClientApplication) getActivity()
					.getApplication();
			RecordsArrayAdapter recAdapter = getRecordingsAdapter();
			if (recAdapter != null) {
				recAdapter.clear();
				recAdapter.addAll(app.getRecordings());
				recAdapter.notifyDataSetChanged();
				recAdapter.sort();
			}
		}
	}

	private RecordsArrayAdapter getRecordingsAdapter() {
		return (RecordsArrayAdapter) getListAdapter();
	}

	public void addRecording(Recording rec) {
		RecordsArrayAdapter recAdapter = getRecordingsAdapter();
		if (recAdapter != null) {
			recAdapter.add(rec);
			recAdapter.notifyDataSetChanged();
			recAdapter.sort();
		}
	}

	public void removeRecording(Recording rec) {
		RecordsArrayAdapter recAdapter = getRecordingsAdapter();
		if (recAdapter != null) {
			recAdapter.remove(rec);
			recAdapter.notifyDataSetChanged();
		}
	}

	public void updateRecording(Recording rec) {
		RecordsArrayAdapter recAdapter = getRecordingsAdapter();
		if (recAdapter != null) {
			recAdapter.notifyDataSetChanged();
		}
	}

}
