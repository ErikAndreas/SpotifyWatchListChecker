package se.nyhren.android.swlc4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.nyhren.android.swlc4.store.Findings;
import se.nyhren.android.swlc4.vo.SA;
import se.nyhren.android.swlc4.vo.SAResponse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class SpotifyPoller extends Service {

	private static final int id = 45678910;
	private List<SA> foundArtistAlbums;
	private List<SA> foundArtistNews;
	private static final String TAG = "SWLC";
	private String conText = "";

	public void onCreate() {
		Log.d(TAG, "onCreate");
		foundArtistAlbums = new ArrayList<SA>();
		foundArtistNews = new ArrayList<SA>();
		Log.d(TAG, "start checking");
		new BackgroundTask().execute(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void doNotify(Context ctx, CharSequence tickerTxt,
			CharSequence conTitle, CharSequence conText) {

		/*
		 * String url = "http://nyhren.se/SWL"; Intent notificationIntent = new
		 * Intent(Intent.ACTION_VIEW);
		 * notificationIntent.setData(Uri.parse(url));
		 */
		Intent notificationIntent = new Intent(ctx, Dialog.class);		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(ctx)
		        .setSmallIcon(R.drawable.icon)
		        .setContentTitle(conTitle)
		        .setTicker(tickerTxt)
		        .setContentText(conText);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(notificationIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(id, mBuilder.build());
	}

	private class BackgroundTask extends AsyncTask<Context, Void, Void> {
		private Context ctx;

		@Override
		protected Void doInBackground(Context... ctx) {
			conText = "";
			this.ctx = ctx[0];
			try {
				WLReader sr = new WLReader(getApplicationContext());
				List<SA> aa = sr.getArtistAlbums();
				// TODO make SR.get return SAResponse (composite) and check for
				// 403 and if so pause and call again
				for (int i = 0; i < aa.size(); i++) {
					// SA sa = SpotifyReader.getArtistAlbum(aa.get(i));
					SAResponse sar = SpotifyReader.getArtistAlbum(aa.get(i));
					if (sar.getR().getRc() == 403) {
						Log.i(TAG, "got 403, will calm down now and retry");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						i--;
					}
					if (null != sar.getSA() && sar.getSA().isFound()) {
						foundArtistAlbums.add(sar.getSA());
					}
				}
				List<SA> an = sr.getArtistNews();
				for (int i = 0; i < an.size(); i++) {
					SAResponse sar = SpotifyReader.getArtistNews(an.get(i));
					if (sar.getR().getRc() == 403) {
						Log.i(TAG, "got 403, will calm down now and retry");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						i--;
					}
					if (null != sar.getSA() && sar.getSA().isFound()) {
						foundArtistNews.add(sar.getSA());
					}
				}
				Log.d(TAG, "aa: " + foundArtistAlbums.size()
						+ " an: " + foundArtistNews.size());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (foundArtistAlbums.size() > 0) {
				conText += "" + foundArtistAlbums.size()
						+ " album(s). ";
			}
			if (foundArtistNews.size() > 0) {
				if (conText.length() > 0)
					conText += "\n";
				conText += "News for " + foundArtistNews.size()
						+ " artist(s).";
			}
			Log.d(TAG, "done checking");
			return null;
		}

		protected void onPostExecute(Void v) {
			Log.d(TAG, "onPostExecute");
			stopSelf();
			String userCountry = getApplicationContext().getResources()
					.getConfiguration().locale.getCountry();
			List<SA> list = foundArtistAlbums;
			list.addAll(foundArtistNews);
			int findings = 0;
			for (final SA sa : list) {
				for (final SA ni : sa.getNews()) {
					if ("worldwide".equals(ni.getAvailability())
							|| ni.getAvailability().contains(userCountry)) {
						findings++;
					} else {
						Log.d(TAG,
								"skipping " + ni.getArtist() + "/"
										+ ni.getAlbum() + " "
										+ ni.getAvailability());
					}
				}
			}
			if (findings > 0) {
				doNotify(this.ctx, "SWL Checker found stuff on Watchlist",
						"SWL Checker findings", "Found " + findings
								+ " item(s) on the list.");
				Findings.set(getApplicationContext(), list);
			} else {
				Log.d(TAG, "no findings avail to user");
			}

		}
	}

}
