package com.dimafon.tvhclient;

import com.dimafon.tvhclient.model.Programme;
import com.dimafon.tvhclient.model.Recording;

import android.app.Activity;

public class ActivityUtil {
	public static void hadleProgrammeChanges(String action, final Object obj,final ChannelDetailFragment fragment, Activity activity) {
		if(fragment==null) return;
        if (action.equals(ApplicationModel.ACTION_PROGRAMME_ADD)) {
            activity.runOnUiThread(new Runnable() {

                public void run() {
                    Programme p = (Programme) obj;
                    if (fragment.channel != null && p.channel.id == fragment.channel.id) {
                        fragment.addProgramm(p);
                    }
                }
            });
        } else if (action.equals(ApplicationModel.ACTION_PROGRAMME_DELETE)) {
        	activity.runOnUiThread(new Runnable() {

                public void run() {
                	fragment.removeProgramm((Programme) obj);
                }
            });
        } else if (action.equals(ApplicationModel.ACTION_PROGRAMME_UPDATE)) {
        	activity.runOnUiThread(new Runnable() {

                public void run() {
                    Programme p = (Programme) obj;
                    if (fragment.channel != null && p.channel.id == fragment.channel.id) {
                        fragment.updateProgramm(p);
                    }
                }
            });
        } else if (action.equals(ApplicationModel.ACTION_DVR_UPDATE)) {
        	activity.runOnUiThread(new Runnable() {

                public void run() {
                    Recording rec = (Recording) obj;
                    fragment.updateForRecording(rec);
                }
            });
        }else if (action.equals(ApplicationModel.ACTION_DVR_DELETE)) {
        	activity.runOnUiThread(new Runnable() {

                public void run() {
                    Recording rec = (Recording) obj;
                    fragment.updateForRecording(rec);
                }
            });
        }
	}

}
