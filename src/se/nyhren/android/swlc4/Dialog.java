package se.nyhren.android.swlc4;

import java.util.List;

import se.nyhren.android.swlc4.store.Findings;
import se.nyhren.android.swlc4.vo.SA;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/* 
 * */
public class Dialog extends Activity {

	private static final String TAG = "SWLC";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Dialog created");
		setContentView(R.layout.dialog);
		String userCountry = getApplicationContext().getResources()
				.getConfiguration().locale.getCountry();
		Log.d(TAG, "user country: " + userCountry);
		LinearLayout ll = (LinearLayout) findViewById(R.id.spotifydialogroot);
		ll.removeAllViews();
		setTitle("WatchList Findings");

		final List<SA> list = Findings.get(getApplicationContext());
		if (null != list) {
			for (final SA sa : list) {
				for (final SA ni : sa.getNews()) {
					Log.d(TAG,
							"in dialog; " + ni.getArtist() + "/"
									+ ni.getAlbum());
					LinearLayout row = new LinearLayout(this);
					row.setPadding(10, 10, 10, 10);
					ImageView img = new ImageView(this);
					img.setLayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.MATCH_PARENT));
					if ("worldwide".equals(ni.getAvailability())
							|| ni.getAvailability().contains(userCountry)) {
						img.setImageResource(R.drawable.spotifylogo);

						img.setFocusable(true);
						img.setPadding(0, 0, 10, 0);
						img.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								// spotify:album:5Oa2WgO3Jfuw2IKYrZNzTi
								// http://open.spotify.com/album/5Oa2WgO3Jfuw2IKYrZNzTi
								String url = "spotify:album:" + ni.getHref();
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(url));
								startActivity(i);
							}
						});

						TextView txt = new TextView(this);
						txt.setLayoutParams(new ViewGroup.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.MATCH_PARENT));
						txt.setText(ni.getArtist() + "\n" + ni.getAlbum());
						row.addView(img);
						row.addView(txt);
						ll.addView(row);
					} else {
						Log.d(TAG,
								"skipping " + ni.getArtist() + "/"
										+ ni.getAlbum() + " "
										+ ni.getAvailability());
					}
				}
			}
		} else {
			TextView nf = new TextView(this);
			nf.setText("No findings yet");
			ll.addView(nf);
		}
		TextView topage = new TextView(this);
		topage.setText(Html
				.fromHtml("To <a href=\"https://nyhren.se/SWL\">WatchList</a>"));
		topage.setMovementMethod(LinkMovementMethod.getInstance());
		ll.addView(topage);
	}

	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

}
