package com.dimafon.tvhclient.intent;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import org.tvheadend.tvhguide.SearchResultActivity;
import org.tvheadend.tvhguide.model.Channel;

public class SearchEpgIntent extends Intent {

    public SearchEpgIntent(Context ctx, String query) {
        super(ctx, SearchResultActivity.class);
        setAction(Intent.ACTION_SEARCH);
        putExtra(SearchManager.QUERY, query);
    }

    public SearchEpgIntent(Context ctx, Channel ch, String query) {
        this(ctx, query);
        putExtra("channelId", ch.id);
    }
}
