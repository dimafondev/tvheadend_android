package com.dimafon.tvhclient.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.dimafon.tvhclient.ExternalPlaybackActivity;
import com.dimafon.tvhclient.R;
import com.dimafon.tvhclient.TVHClientApplication;
import com.dimafon.tvhclient.htsp.HTSService;
import com.dimafon.tvhclient.model.Channel;
import com.dimafon.tvhclient.model.Programme;
import com.dimafon.tvhclient.model.Recording;

public class ActionUtil {

	public static void deleteCancelRecordings(final Activity context,
			Collection<Long> ids) {
		TVHClientApplication app = (TVHClientApplication) context
				.getApplication();
		final Collection<Long> toDeleteIds = new ArrayList<Long>();
		for (Long id : ids) {
			Recording rec = app.getRecording(id);
			if (rec.isRecording() || rec.isScheduled()) {
				final Intent intent = new Intent(context, HTSService.class);
				intent.putExtra("id", id);
				intent.setAction(HTSService.ACTION_DVR_CANCEL);
				context.startService(intent);
			} else {
				toDeleteIds.add(id);
			}
		}
		if (!toDeleteIds.isEmpty()) {
			new AlertDialog.Builder(context)
					.setTitle(R.string.menu_record_remove)
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									for (Long id : toDeleteIds) {
										final Intent intent = new Intent(
												context, HTSService.class);
										intent.putExtra("id", id);
										intent.setAction(HTSService.ACTION_DVR_DELETE);
										context.startService(intent);
									}
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// nothing to do
								}
							}).show();
		}
	}

	public static void playRecording(Fragment fragment, long id) {
		TVHClientApplication app = (TVHClientApplication) fragment
				.getActivity().getApplication();
		Recording rec = app.getRecording(id);
		if (rec != null && !rec.isScheduled()) {
			Intent intent = new Intent(fragment.getActivity(),
					ExternalPlaybackActivity.class);
			intent.putExtra("dvrId", id);
			fragment.startActivity(intent);
		} else {
			Toast.makeText(fragment.getActivity(), R.string.no_play,
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void startRecording(Activity activity,
			Collection<Programme> programmes) {
		for (Programme programme : programmes) {
			Intent intent = new Intent(activity, HTSService.class);
			if (programme.recording == null) {
				intent.setAction(HTSService.ACTION_DVR_ADD);
				intent.putExtra("eventId", programme.id);
				intent.putExtra("channelId", programme.channel.id);
			}
			activity.startService(intent);
		}
	}

	public static void cancelRecording(Activity activity,
			Collection<Programme> programmes) {
		for (Programme programme : programmes) {
			Intent intent = new Intent(activity, HTSService.class);
			Recording recording = programme.recording;
			if (recording != null) {
				if (recording.isRecording() || recording.isScheduled()) {
					intent.setAction(HTSService.ACTION_DVR_CANCEL);
					intent.putExtra("id", recording.id);
				} else {
					intent.setAction(HTSService.ACTION_DVR_DELETE);
					intent.putExtra("id", recording.id);
				}
			}
			activity.startService(intent);
		}
	}

	public static void switchRecording(Activity activity,
			Collection<Programme> programmes) {
		for (Programme programme : programmes) {
			Intent intent = new Intent(activity, HTSService.class);
			Recording recording = programme.recording;
			if (recording != null) {
				programme.recording = null;
				if (recording.isRecording() || recording.isScheduled()) {
					intent.setAction(HTSService.ACTION_DVR_CANCEL);
					intent.putExtra("id", recording.id);
				} else {
					intent.setAction(HTSService.ACTION_DVR_DELETE);
					intent.putExtra("id", recording.id);
				}
			} else {
				intent.setAction(HTSService.ACTION_DVR_ADD);
				intent.putExtra("eventId", programme.id);
				intent.putExtra("channelId", programme.channel.id);
			}
			activity.startService(intent);
		}
	}

	public static void loadProgramms(Context context, Channel channel) {
		Programme p = null;

		Iterator<Programme> it = channel.epg.iterator();
		long nextId = 0;

		while (it.hasNext()) {
			p = it.next();
			if (p.id != nextId && nextId != 0) {
				break;
			}
			nextId = p.nextId;
		}
		if (p == null)
			return;

		if (nextId == 0) {
			nextId = p.nextId;
		}
		if (nextId == 0) {
			nextId = p.id;
		}
		Intent intent = new Intent(context, HTSService.class);
		intent.setAction(HTSService.ACTION_GET_EVENTS);
		intent.putExtra("eventId", nextId);
		intent.putExtra("channelId", channel.id);
		intent.putExtra("count", 10);
		context.startService(intent);

	}

}
