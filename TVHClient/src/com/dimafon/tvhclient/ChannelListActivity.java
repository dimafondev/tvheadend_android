package com.dimafon.tvhclient;

import org.tvheadend.tvhguide.PlaybackActivity;
import org.tvheadend.tvhguide.TVHGuideApplication;
import org.tvheadend.tvhguide.htsp.HTSListener;
import org.tvheadend.tvhguide.htsp.HTSService;
import org.tvheadend.tvhguide.model.Channel;
import org.tvheadend.tvhguide.model.ChannelTag;
import org.tvheadend.tvhguide.model.Recording;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.dimafon.tvhclient.action.ActionUtil;
import com.dimafon.tvhclient.adapter.ChannelArrayAdapter;
import com.dimafon.tvhclient.slidingmenu.NavigationUtil;
import com.dimafon.tvhclient.slidingmenu.model.NavDrawerItem;

/**
 * An activity representing a list of Channels. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ChannelDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ChannelListFragment} and the item details (if present) is a
 * {@link ChannelDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ChannelListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ChannelListActivity extends Activity implements
		ChannelListFragment.Callbacks, RecordsFragment.Callbacks,
		NavigationDrawerFragment.NavigationDrawerCallbacks, HTSListener {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private ChannelListFragment mChannelListFragment;
	ProgressDialog pDialog;
	private ChannelTag currentTag;
	private RecordsFragment mRecordfragment;
	private SearchView searchView;
	private ChannelDetailFragment mProgrammsfragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// FragmentManager.enableDebugLogging(true);

		setContentView(R.layout.activity_channel_list);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		if (findViewById(R.id.channel_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			// mChannelListFragment = ((ChannelListFragment)
			// getFragmentManager()
			// .findFragmentById(R.id.channel_list));
			// mChannelListFragment.setActivateOnItemClick(true);
		}
		// mNavigationDrawerFragment.select
		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link ChannelListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(long id) {
		Channel ch = ((TVHClientApplication) getApplication()).getChannel(Long
				.valueOf(id));
		if (ch != null) {
			Intent intent = new Intent(this, PlaybackActivity.class);
			intent.putExtra("channelId", ch.id);
			startActivity(intent);
		}
	}

	@Override
	public void onItemExpanded(Object obj) {
		if (obj instanceof Channel) {
			long id = ((Channel) obj).id;
			if (mTwoPane) {
				// In two-pane mode, show the detail view in this activity by
				// adding or replacing the detail fragment using a
				// fragment transaction.
				Bundle arguments = new Bundle();
				arguments.putLong(ChannelDetailFragment.ARG_ITEM_ID, id);
				mProgrammsfragment = new ChannelDetailFragment();
				mProgrammsfragment.setArguments(arguments);
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.channel_detail_container,
								mProgrammsfragment).commit();

			} else {
				// In single-pane mode, simply start the detail activity
				// for the selected item ID.
				Intent detailIntent = new Intent(this,
						ChannelDetailActivity.class);
				detailIntent.putExtra(ChannelDetailFragment.ARG_ITEM_ID, id);
				startActivity(detailIntent);
			}
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(NavDrawerItem item) {

		if (NavigationUtil.SETTING_ITEM == item) {
			startActivity(new Intent(this, SettingsActivity.class));
			return;
		} else {
			ActionBar bar = getActionBar();
			bar.setTitle(item.getTitle());
			int hsvToColor = item.getColor() != null ? Color.HSVToColor(item
					.getColor()) : 0xff004600;
			bar.setBackgroundDrawable(new ColorDrawable(hsvToColor));
		}
		currentTag = item.getChannelTag();
		((TVHClientApplication) getApplication()).setCurrentTag(currentTag);
		if (NavigationUtil.RECORDS_ITEM == item) {
			if (mRecordfragment == null) {
				mRecordfragment = new RecordsFragment();
			}
			getFragmentManager().beginTransaction()
					.replace(R.id.container, mRecordfragment).commit();
			updateDefaultDetails(mRecordfragment);
		} else {
			if (mChannelListFragment == null) {
				mChannelListFragment = new ChannelListFragment();
			}
			if (!mChannelListFragment.isVisible()) {
				getFragmentManager().beginTransaction()
						.replace(R.id.container, mChannelListFragment).commit();
			} else {
				mChannelListFragment.populateList();
			}
			updateDefaultDetails(mChannelListFragment);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.global, menu);
			restoreActionBar();
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			MenuItem searchItem = (MenuItem) menu.findItem(R.id.action_search);
			searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			TVHGuideApplication app = (TVHGuideApplication) getApplication();
			connect(true);
			setLoading(app.isLoading());
		}
		return super.onOptionsItemSelected(item);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setTitle(mTitle);
	}

	@Override
	public void onRecoringSelected(long id) {
		ActionUtil.playRecording(mRecordfragment, id);
	}

	@Override
	public void onRecordingExpanded(Recording obj) {
		if (obj != null) {
			long id = obj.id;
			if (mTwoPane) {
				// In two-pane mode, show the detail view in this activity by
				// adding or replacing the detail fragment using a
				// fragment transaction.
				Bundle arguments = new Bundle();
				arguments.putLong(ChannelDetailFragment.ARG_ITEM_ID, id);
				RecordDetailFragment fragment = new RecordDetailFragment();
				fragment.setArguments(arguments);
				getFragmentManager().beginTransaction()
						.replace(R.id.channel_detail_container, fragment)
						.commit();

			} else {
				// In single-pane mode, simply start the detail activity
				// for the selected item ID.
				Intent detailIntent = new Intent(this,
						RecordDetailActivity.class);
				detailIntent.putExtra(ChannelDetailFragment.ARG_ITEM_ID, id);
				startActivity(detailIntent);
			}
		}

	}

	void connect(boolean force) {
		if (force) {
			// TODO: chAdapter.clear();
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String hostname = prefs.getString("serverHostPref", "localhost");
		int port = Integer.parseInt(prefs.getString("serverPortPref", "9982"));
		String username = prefs.getString("usernamePref", "");
		String password = prefs.getString("passwordPref", "");

		Intent intent = new Intent(ChannelListActivity.this, HTSService.class);
		intent.setAction(HTSService.ACTION_CONNECT);
		intent.putExtra("hostname", hostname);
		intent.putExtra("port", port);
		intent.putExtra("username", username);
		intent.putExtra("password", password);
		intent.putExtra("force", force);

		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TVHGuideApplication app = (TVHGuideApplication) getApplication();
		app.addListener(this);
		connect(false);
		setLoading(app.isLoading());
	}

	private void setLoading(boolean loading) {
		if (loading) {
			if (pDialog == null) {
				pDialog = new ProgressDialog(this);
				pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// pDialog.setCancelable(true);
			}
			pDialog.show();
		} else {
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				pDialog = null;
			}

			mNavigationDrawerFragment.updateTags();
			if (mChannelListFragment != null) {
				mChannelListFragment.populateList();
			}
			if (mRecordfragment != null) {
				mRecordfragment.populateList();
			}
			updateDefaultDetails(null);

		}
	}

	private void updateDefaultDetails(Fragment fragment) {
		if (mTwoPane) {
			if (fragment == null) {
				if (mChannelListFragment != null
						&& mChannelListFragment.isVisible()) {
					fragment = mChannelListFragment;
				} else if (mRecordfragment != null
						&& mRecordfragment.isVisible()) {
					fragment = mRecordfragment;
				}
			}
			if (fragment != null) {
				ListAdapter la = mChannelListFragment.getListAdapter();
				if (la.getCount() > 0) {
					Object obj = la.getItem(0);
					if (obj instanceof Recording) {
						onRecordingExpanded((Recording) obj);
					} else
						onItemExpanded(obj);
				}
			}
		}
	}

	@Override
	public void onMessage(final String action, final Object obj) {
		if (action.equals(TVHGuideApplication.ACTION_LOADING)) {

			runOnUiThread(new Runnable() {

				public void run() {
					boolean loading = (Boolean) obj;
					setLoading(loading);
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_CHANNEL_ADD)) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (mChannelListFragment != null) {
						mChannelListFragment.add((Channel) obj);
					}
					mNavigationDrawerFragment.updateTagsCounter();
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_CHANNEL_DELETE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (mChannelListFragment != null) {
						mChannelListFragment.remove((Channel) obj);
					}
					mNavigationDrawerFragment.updateTagsCounter();
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_CHANNEL_UPDATE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (mChannelListFragment != null) {
						mChannelListFragment.update((Channel) obj);
					}
					mNavigationDrawerFragment.updateTagsCounter();
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_TAG_ADD)
				|| action.equals(TVHGuideApplication.ACTION_TAG_DELETE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					mNavigationDrawerFragment.updateTags();
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_TAG_UPDATE)) {
			// NOP
		} else if (action.equals(TVHGuideApplication.ACTION_DVR_ADD)) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (mRecordfragment != null)
						mRecordfragment.addRecording((Recording) obj);
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_DVR_DELETE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (mRecordfragment != null)
						mRecordfragment.removeRecording((Recording) obj);
				}
			});
		} else if (action.equals(TVHGuideApplication.ACTION_DVR_UPDATE)) {
			runOnUiThread(new Runnable() {

				public void run() {
					if (mRecordfragment != null)
						mRecordfragment.updateRecording((Recording) obj);
				}
			});
		} else if (mTwoPane) {
			ActivityUtil.hadleProgrammeChanges(action, obj, mProgrammsfragment,
					this);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		TVHGuideApplication app = (TVHGuideApplication) getApplication();
		app.removeListener(this);
	}

	public boolean isTwoPane() {
		return mTwoPane;
	}

}