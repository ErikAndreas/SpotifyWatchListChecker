package se.nyhren.android.swlc4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BootScheduler extends BroadcastReceiver {
	
	private static final String TAG = "TAG"; 

	public void onReceive(Context context, Intent rIntent) {
		if (rIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			int pf = prefs.getInt("pollFreq", -1);
			boolean on = prefs.getBoolean("scheduleOnOff", false);
			if (on) schedule(context,pf);
		}
	}
	
	public static void run(Context context) {
		Log.d(TAG,"SpotifyPoller scheduled to run now");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SpotifyPoller.class);
		PendingIntent pIntent = PendingIntent.getService(context, 0,
				intent, 0);
		am.set(AlarmManager.RTC, System.currentTimeMillis(), pIntent);
	}
	
	public static void schedule(Context context, int pf) {
		Log.d(TAG,"SpotifyPoller scheduled");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SpotifyPoller.class);
		PendingIntent pIntent = PendingIntent.getService(context, 0,
				intent, 0);
		long interval = AlarmManager.INTERVAL_DAY;
		if (0 == pf) {
			interval = AlarmManager.INTERVAL_DAY;
		} else if (1 == pf) {
			interval = AlarmManager.INTERVAL_HALF_DAY;
		} else if (2 == pf) {
			interval = AlarmManager.INTERVAL_HOUR;
		} 
		Log.d(TAG,"will poll every " + interval/1000/60 + " mins");
		Toast.makeText(context, "Will poll WatchList every " + interval/1000/60 + " mins", Toast.LENGTH_SHORT).show();
		//long interval = 60*1000;
		long firstPoll = System.currentTimeMillis();
		am.setInexactRepeating(AlarmManager.RTC, firstPoll, interval,
				pIntent);
	}
	
	public static void unschedule(Context context) {
		Log.d(TAG,"SpotifyPoller unscheduled");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SpotifyPoller.class);
		PendingIntent pIntent = PendingIntent.getService(context, 0,
				intent, 0);	
		Toast.makeText(context, "WatchList polling cancelled", Toast.LENGTH_SHORT).show();
		am.cancel(pIntent);
	}

}
