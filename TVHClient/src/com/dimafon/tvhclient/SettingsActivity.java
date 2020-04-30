package com.dimafon.tvhclient;

import com.dimafon.tvhclient.htsp.HTSService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;


public class SettingsActivity extends PreferenceActivity {

    public  static final String PASSWORD_PREF = "passwordPref";
	public static final String USERNAME_PREF = "usernamePref";
	public static final String SERVER_HOST_PREF = "serverHostPref";
	public static final String SERVER_PORT_PREF = "serverPortPref";
	private int oldPort;
    private String oldHostname,oldUser,oldPw;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencesm);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.settings);

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
    
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean theme = prefs.getBoolean("lightThemePref", false);
//        setTheme(theme ? android.R.style.Theme_Light : android.R.style.Theme);
//
//        requestWindowFeature(Window.FEATURE_LEFT_ICON);
//        super.onCreate(savedInstanceState);
//
//        addPreferencesFromResource(R.xml.preferences);
//        setTitle(getString(R.string.app_name) + " - " + getString(R.string.menu_settings));
//        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.logo_72);
//    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        oldHostname = prefs.getString(SERVER_HOST_PREF, "");
        oldPort = Integer.parseInt(prefs.getString(SERVER_PORT_PREF, ""));
        oldUser = prefs.getString(USERNAME_PREF, "");
        oldPw = prefs.getString(PASSWORD_PREF, "");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean reconnect = false;
        reconnect |= !oldHostname.equals(prefs.getString(SERVER_HOST_PREF, ""));
        reconnect |= oldPort != Integer.parseInt(prefs.getString(SERVER_PORT_PREF, ""));
        reconnect |= !oldUser.equals(prefs.getString(USERNAME_PREF, ""));
        reconnect |= !oldPw.equals(prefs.getString(PASSWORD_PREF, ""));

        if (reconnect) {
            Log.d("SettingsActivity", "Connectivity settings chaned, forcing a reconnect");
            Intent intent = new Intent(SettingsActivity.this, HTSService.class);
            intent.setAction(HTSService.ACTION_CONNECT);
            intent.putExtra("hostname", prefs.getString(SERVER_HOST_PREF, ""));
            intent.putExtra("port", Integer.parseInt(prefs.getString(SERVER_PORT_PREF, "")));
            intent.putExtra("username", prefs.getString(USERNAME_PREF, ""));
            intent.putExtra("password", prefs.getString(PASSWORD_PREF, ""));
            intent.putExtra("force", true);
            startService(intent);
        }
    }
}
