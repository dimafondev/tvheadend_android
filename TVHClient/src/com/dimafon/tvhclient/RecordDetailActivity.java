package com.dimafon.tvhclient;

import org.tvheadend.tvhguide.TVHGuideApplication;
import org.tvheadend.tvhguide.htsp.HTSListener;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;



public class RecordDetailActivity extends Activity implements HTSListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putLong(ChannelDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(ChannelDetailFragment.ARG_ITEM_ID,-1));
            RecordDetailFragment fragment = new RecordDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.record_detail_container, fragment)
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
        TVHClientApplication app = (TVHClientApplication) getApplication();
        app.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TVHClientApplication app = (TVHClientApplication) getApplication();
        app.removeListener(this);
    }

	@Override
	public void onMessage(String action, Object obj) {
		// TODO Auto-generated method stub
		
	}
}
