package com.dimafon.tvhclient.adapter;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.tvheadend.tvhguide.R.string;
import org.tvheadend.tvhguide.model.Programme;
import org.tvheadend.tvhguide.model.SeriesInfo;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dimafon.tvhclient.R;
import com.dimafon.tvhclient.anim.FlipAnimation;
import com.dimafon.tvhclient.anim.SwapViews;
import com.dimafon.tvhclient.slidingmenu.NavigationUtil;

public class AdapterUtil {
	public static float convertDpToPixel(float dp, Context context){
		 return dp * context.getResources().getDisplayMetrics().density;
		}

	public static void updateIcon(View row, String name, Bitmap iconBitmap, int stateIconId, boolean selected) {
		ImageView selectIcon = (ImageView) row.findViewById(R.id.tv_selected);
		ImageView channelIcon = (ImageView) row.findViewById(R.id.tv_logo);
		TextView letterText = (TextView) row.findViewById(R.id.tv_logot);
		ImageView stateIcon = (ImageView) row.findViewById(R.id.tv_state);
		if (iconBitmap != null) {
			letterText.setVisibility(View.INVISIBLE);
			channelIcon.setImageDrawable(new BitmapDrawable(iconBitmap));
			channelIcon.setVisibility(View.VISIBLE);
			channelIcon.setVisibility(selected?View.INVISIBLE:View.VISIBLE);
			channelIcon.invalidate();
			channelIcon.setTag(true);
			letterText.setTag(false);
		} else {
			channelIcon.setVisibility(View.INVISIBLE);
			letterText.setText(name.substring(0, 1));
			letterText.setBackgroundColor(NavigationUtil.getColorForText(name));
			letterText.setVisibility(selected?View.INVISIBLE:View.VISIBLE);
			letterText.invalidate();
			channelIcon.setTag(false);
			letterText.setTag(true);
		}
		selectIcon.setVisibility(selected?View.VISIBLE:View.INVISIBLE);
		selectIcon.invalidate();
		if (stateIconId > 0) {
			stateIcon.setImageResource(stateIconId);
		} else {
			stateIcon.setImageDrawable(null);
		}
		stateIcon.invalidate();
	}

	public static String buildSeriesInfoString(SeriesInfo info, Resources resources) {
		if (info == null)
			return "";
		if (info.onScreen != null && info.onScreen.length() > 0)
			return info.onScreen;

		String s = "";
		String season = resources.getString(string.pr_season);
		String episode = resources.getString(string.pr_episode);
		String part = resources.getString(string.pr_part);

		if (info.onScreen.length() > 0) {
			return info.onScreen;
		}

		if (info.seasonNumber > 0) {
			if (s.length() > 0)
				s += ", ";
			s += String.format("%s %02d", season.toLowerCase(Locale.getDefault()), info.seasonNumber);
		}
		if (info.episodeNumber > 0) {
			if (s.length() > 0)
				s += ", ";
			s += String.format("%s %02d", episode.toLowerCase(Locale.getDefault()), info.episodeNumber);
		}
		if (info.partNumber > 0) {
			if (s.length() > 0)
				s += ", ";
			s += String.format("%s %d", part.toLowerCase(Locale.getDefault()), info.partNumber);
		}

		if (s.length() > 0) {
			s = s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1);
		}

		return s;
	}

	public static boolean isExpandable(final TextView v) {
		Layout layout = v.getLayout();
		if (layout != null) {
			int lines = layout.getLineCount();
			if (lines > 0) {
				return layout.getEllipsisCount(lines - 1) > 0;
			}
		}

		return false;
	}

	public static void expand(final TextView v) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int start = v.getHeight();
		int lines = v.getLayout().getLineCount();
		final int lineHight = v.getMeasuredHeight() / lines;
		final int targetHeight = lineHight * (v.getMeasuredWidth() / v.getWidth());
		final int diff = targetHeight - start;
		v.setMaxLines(100);
		v.getLayoutParams().height = start;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = (int) (diff * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		a.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				v.post(new Runnable() {

					@Override
					public void run() {
						v.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
						v.invalidate();
					}
				});
			}
		});

		// 1dp/ms
		a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final TextView v) {
		final int initialHeight = v.getMeasuredHeight();
		int lines = v.getLayout().getLineCount();
		final int targetHight = 2 * initialHeight / lines;
		final int diff = initialHeight - targetHight;
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = initialHeight - (int) ((diff) * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		a.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				v.post(new Runnable() {

					@Override
					public void run() {
						v.setMaxLines(2);
						v.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
						v.invalidate();
					}
				});
			}
		});

		// 1dp/ms
		a.setDuration((int) (diff / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void applyRotation(final View v1, final View v2) {
		final float centerX = v1.getWidth() / 2.0f;
		final float centerY = v1.getHeight() / 2.0f;
		final FlipAnimation rotation = new FlipAnimation(0, 90, centerX, centerY);
		rotation.setDuration(500);
		rotation.setFillAfter(false);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new SwapViews(true, v1, v2));
		v1.startAnimation(rotation);

	}
	
	public static int getStateImage(Programme p) {
		if (p.recording != null) {
			if (p.recording.error != null) {
				return R.drawable.ic_err;
			} else if ("completed".equals(p.recording.state)) {
				return R.drawable.ic_ok;
			} else if ("invalid".equals(p.recording.state) || "missed".equals(p.recording.state)) {
				return R.drawable.ic_err;
			} else if ("recording".equals(p.recording.state)) {
				return R.drawable.ic_rec;
			} else if ("scheduled".equals(p.recording.state)) {
				return R.drawable.ic_clock;
			}
		}
		return 0;
	}
	
	public static String getTimeSpan(Programme p, Context context2) {
		return DateFormat.getTimeFormat(context2).format(p.start) + " - " + DateFormat.getTimeFormat(context2).format(p.stop);
	}

	public static CharSequence getDateString(Programme p,Context context){
		long sTime = p.start.getTime();
		if (DateUtils.isToday(sTime)) {
			return "";
		} else {
			long currentTime = System.currentTimeMillis();
			if (sTime < currentTime + 1000 * 60 * 60 * 24 * 2 && sTime > currentTime - 1000 * 60 * 60 * 24 * 2) {
				return DateUtils.getRelativeTimeSpanString(sTime, currentTime, DateUtils.DAY_IN_MILLIS);
			} else if (sTime < currentTime + 1000 * 60 * 60 * 24 * 6 && sTime > currentTime - 1000 * 60 * 60 * 24 * 2) {
				return new SimpleDateFormat("EEEE",Locale.getDefault()).format(sTime);
			} else {
				return DateFormat.getDateFormat(context).format(p.start);
			}
		}

	}

	public static void updateExpandableDescription(boolean expanded, final TextView description, final CheckBox expandBtn, String pDesc) {
		if (pDesc != null && pDesc.length() > 0) {
			description.setText(pDesc);
			description.setMaxLines(expanded ? 100 : 2);
			description.setVisibility(TextView.VISIBLE);
			description.invalidate();
			if (!expanded)
				description.post(new Runnable() {

					@Override
					public void run() {
						if (AdapterUtil.isExpandable(description)) {
							expandBtn.setVisibility(View.VISIBLE);
						} else {
							expandBtn.setVisibility(View.GONE);
						}
					}
				});
		} else {
			description.setText("");
			description.setVisibility(TextView.GONE);
			expandBtn.setVisibility(View.GONE);
			description.invalidate();
		}
	}
}
