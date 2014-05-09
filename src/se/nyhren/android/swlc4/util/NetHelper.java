package se.nyhren.android.swlc4.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;

import se.nyhren.android.swlc4.vo.Response;

import android.util.Log;
// TODO: consider http://developer.android.com/reference/java/net/ResponseCache.html
// instead of own 'caching'
public class NetHelper {
	private static final String TAG = "SWLC";
	private static Map<String, Long> lastModified = new HashMap<String, Long>();
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	private static Map<String, String> content = new HashMap<String, String>();

	public static Response getURIContents(String url) throws IOException {
		Response r = new Response();
		String myString = null;
		/* Define the URL we want to load data from. */
		URL myURL = new URL(url);
		r.setUrl(url);
		InputStream is = null;
		int rc = 0;
		HttpURLConnection ucon = (HttpURLConnection) myURL.openConnection();
		if (null != lastModified.get(myURL.toString())
				&& 0 != lastModified.get(myURL.toString())) {
			Log.d(TAG,
					"setting if mod "
							+ sdf.format(new Date(lastModified.get(myURL
									.toString()))));
			ucon.addRequestProperty("If-Modified-Since",
					sdf.format(new Date(lastModified.get(myURL.toString()))));
		}
		rc = ucon.getResponseCode();
		r.setRc(rc);
		Log.d(TAG, myURL.getQuery() + " response code:" + rc + " last mod "
				+ ucon.getLastModified());
		if (rc == 304 && null != content.get(myURL.toString())) {
			Log.d(TAG, "cache hit for " + myURL.toString());
			r.setBody(content.get(myURL.toString()));
			return r;
		}
		if (rc == 200) {
			lastModified.put(myURL.toString(), ucon.getLastModified());
			is = ucon.getInputStream();
		}

		if (rc == 200) {
			BufferedInputStream bis = new BufferedInputStream(is, 8128);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			myString = new String(baf.toByteArray());
			if (rc == 200) {
				content.put(myURL.toString(), myString);
				r.setBody(myString);
			}
		}
		return r;
	}
}
