package com.dimafon.tvhclient.intent;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import com.dimafon.tvhclient.SearchActivity;
import com.dimafon.tvhclient.model.Channel;

public class SearchEpgIntent extends Intent {

	public SearchEpgIntent(Context ctx, String query) {
		super(ctx, SearchActivity.class);
		setAction(Intent.ACTION_SEARCH);
		putExtra(SearchManager.QUERY, query);
	}

	public SearchEpgIntent(Context ctx, Channel ch, String query) {
		this(ctx, query);
		putExtra("channelId", ch.id);
	}
}
