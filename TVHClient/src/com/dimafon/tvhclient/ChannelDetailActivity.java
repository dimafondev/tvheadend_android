package com.dimafon.tvhclient;

import com.dimafon.tvhclient.htsp.HTSListener;
import com.dimafon.tvhclient.model.Programme;
import com.dimafon.tvhclient.model.Recording;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;


/**
 * An activity representing a single Channel detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ChannelListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ChannelDetailFragment}.
 */
public class ChannelDetailActivity extends Activity implements HTSListener {

    private ChannelDetailFragment fragment;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(ChannelDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(ChannelDetailFragment.ARG_ITEM_ID,-1));
            fragment = new ChannelDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.channel_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, ChannelListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void onMessage(String action, final Object obj) {
    	ActivityUtil.hadleProgrammeChanges(action, obj,fragment,this);
    }


}
