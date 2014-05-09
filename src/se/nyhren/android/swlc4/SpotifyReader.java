package se.nyhren.android.swlc4;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.nyhren.android.swlc4.util.NetHelper;
import se.nyhren.android.swlc4.vo.Response;
import se.nyhren.android.swlc4.vo.SA;
import se.nyhren.android.swlc4.vo.SAResponse;

import android.annotation.SuppressLint;
import android.util.Log;

public class SpotifyReader {
	// private final static String s =
	// "{\"info\": {\"num_results\": 3, \"limit\": 100, \"offset\": 0, \"query\": \"heartwork\", \"type\": \"album\", \"page\": 1}, \"albums\": [{\"name\": \"Heartwork\", \"popularity\": \"0.58636\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"5055006509717\"}], \"href\": \"spotify:album:7D1UMHzEIHNhwa0gyqmya9\", \"artists\": [{\"href\": \"spotify:artist:5lhaM01nwvsMZpmPY2HVER\", \"name\": \"Carcass\"}], \"availability\": {\"territories\": \"worldwide\"}}, {\"artists\": [{\"href\": \"spotify:artist:5FqwBNDYkX1ldKXgMQtEAV\", \"name\": \"Guitardani\"}], \"href\": \"spotify:album:52IEIAzOIBSrZxnuMB0CDT\", \"availability\": {\"territories\": \"AD AE AF AG AI AL AM AN AO AQ AR AS AT AU AW AX AZ BA BB BD BE BF BG BH BI BJ BM BN BO BR BS BT BV BW BY BZ CA CC CD CF CG CH CI CK CL CM CN CO CR CU CV CX CY CZ DE DJ DK DM DO DZ EC EE EG EH ER ES ET FI FJ FK FM FO FR GA GB GD GE GF GG GH GI GL GM GN GP GQ GR GS GT GU GW GY HK HM HN HR HT HU ID IE IL IN IO IQ IR IS IT JM JO JP KE KG KH KI KM KN KP KR KW KY KZ LA LB LC LI LK LR LS LT LU LV LY MA MC MD ME MG MH MK ML MM MN MO MP MQ MR MS MT MU MV MW MX MY MZ NA NC NE NF NG NI NL NO NP NR NU NZ OM PA PE PF PG PH PK PL PM PN PR PS PT PW PY QA RE RO RS RU RW SA SB SC SD SE SG SH SI SJ SK SL SM SN SO SR ST SV SY SZ TC TD TF TG TH TJ TK TL TM TN TO TR TT TV TW TZ UA UG UM US UY UZ VA VC VE VG VI VN VU WF WS YE YT ZA ZM ZW\"}, \"name\": \"Heartwork EP\", \"popularity\": \"0.02078\"}, {\"name\": \"Heartwork\", \"popularity\": \"0.00120\", \"external-ids\": [{\"type\": \"upc\", \"id\": \"111117707826\"}], \"href\": \"spotify:album:0P3oulz6uBKZNNjeeqKi3m\", \"artists\": [{\"href\": \"spotify:artist:7qKoy46vPnmIxKCN6ewBG4\", \"name\": \"Butch Walker\"}], \"availability\": {\"territories\": \"AE AM AR AT AU AZ BD BE BG BH BO BR BW BY CH CL CN CY CZ DE DK DZ EE EG ES FI FR GB GE GR HK HU ID IE IL IN IS IT JO KG KR KW KZ LB LT LU LV MA MD MT MX NA NG NL NO OM PH PK PL PS PT PY QA RO RU SA SE SG SI SK SY TJ TM TN TR UA US UY UZ ZA\"}}]}";
	private static final String TAG = "SWLC";

	@SuppressLint("DefaultLocale")
	public static SAResponse getArtistAlbum(SA sa) {
		String s = "";
		SAResponse sar = new SAResponse();
		try {
			// 'http://ws.spotify.com/search/1/album.json?q='+album.replace(/&/g,'%26')+'%20AND%20artist:%22'+artist.replace(/&/g,'%26')+'%22'
			String url = "http://ws.spotify.com/search/1/album.json?q="
					+ URLEncoder.encode(sa.getAlbum(), "utf-8")
					+ "%20AND%20artist:%22"
					+ URLEncoder.encode(sa.getArtist(), "utf-8") + "%22";
			Response r = NetHelper.getURIContents(url);
			s = r.getBody();
			sar.setR(r);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (sar.getR().getBody() != null) {
			//JSONParser parser = new JSONParser();
			/*
			 * JS style data.info.num_results data.albums.length
			 * data.albums[j].artists[0].name data.albums[j].href
			 * data.albums[j].name
			 */
			try {
				//Object o = parser.parse(s);
				JSONObject data = new JSONObject(s);
				JSONObject info = (JSONObject) data.get("info");
				Log.d(TAG, "results for artist album (" + sa.getAlbum() + ") "
						+ info.get("num_results"));
				JSONArray albums = (JSONArray) data.get("albums");
				String artistName = "";
				String albumName = "";
				String albumHref = "";
				String availability = "";
				List<SA> news = new ArrayList<SA>();
				for (int i = 0; i < albums.length(); i++) {
					artistName = (String) ((JSONObject) ((JSONArray) ((JSONObject) albums
							.get(i)).get("artists")).get(0)).get("name");
					albumName = (String) ((JSONObject) albums.get(i))
							.get("name");
					albumHref = (String) ((JSONObject) albums.get(i))
							.get("href");
					availability = ((String) ((JSONObject) ((JSONObject) albums
							.get(i)).get("availability")).get("territories"));
					if (artistName.toLowerCase().equals(
							sa.getArtist().toLowerCase())) {
						Log.d(TAG, "artist name: " + artistName);
						Log.d(TAG, "album name: " + albumName);
						Log.d(TAG, "album href: " + albumHref);
						Log.d(TAG, "avail: " + availability);
						sa.setFound(true);
						SA newsItem = new SA();
						newsItem.setArtist(artistName);
						newsItem.setAlbum(albumName);
						albumHref = albumHref.split(":")[2];
						newsItem.setHref(albumHref);
						newsItem.setAvailability(availability);
						newsItem.setCreated(sa.getCreated());
						Log.d(TAG, "found it!");
						news.add(newsItem);
					}
				}
				sa.setNews(news);
				sar.setSA(sa);
				/*
				 * JSONObject artists = (JSONObject) albums.get(0); JSONArray
				 * artist = (JSONArray) artists.get("artists"); JSONObject
				 * artistProps = (JSONObject) artist.get(0); String artistName =
				 * (String) artistProps.get("name");
				 */
			} catch (JSONException e) {
				Log.e(TAG, "parse fail", e);
			}
		}
		return sar;
	}

	@SuppressLint("DefaultLocale")
	public static SAResponse getArtistNews(SA sa) {
		Log.d(TAG, "start reading spotify artist news");
		SAResponse sar = new SAResponse();
		String s = "";
		try {
			String url = "http://ws.spotify.com/search/1/album.json?q=tag:new%20AND%20artist:%22"
					+ URLEncoder.encode(sa.getArtist(), "utf-8") + "%22";
			Response r = NetHelper.getURIContents(url);
			s = r.getBody();
			sar.setR(r);
		} catch (IOException e1) {
			Log.e(TAG,"fail get spotify news",e1);
		}
		if (sar.getR().getBody() != null) {
			//JSONParser parser = new JSONParser();
			try {
				//Object o = parser.parse(s);
				JSONObject data = new JSONObject(s);
				JSONObject info = (JSONObject) data.get("info");
				Log.d(TAG, "results for artist news (" + sa.getArtist() + "): "
						+ info.get("num_results"));
				JSONArray albums = (JSONArray) data.get("albums");
				String artistName = "";
				String albumName = "";
				String albumHref = "";
				String availability = "";
				List<SA> news = new ArrayList<SA>();
				for (int i = 0; i < albums.length(); i++) {
					artistName = (String) ((JSONObject) ((JSONArray) ((JSONObject) albums
							.get(i)).get("artists")).get(0)).get("name");
					albumName = (String) ((JSONObject) albums.get(i))
							.get("name");
					albumHref = (String) ((JSONObject) albums.get(i))
							.get("href");
					availability = ((String) ((JSONObject) ((JSONObject) albums
							.get(i)).get("availability")).get("territories"));
					if (artistName.toLowerCase().equals(
							sa.getArtist().toLowerCase())) {
						Log.d(TAG, "artist name: " + artistName);
						Log.d(TAG, "album name: " + albumName);
						Log.d(TAG, "album href: " + albumHref);
						Log.d(TAG, "avail: " + availability);
						sa.setFound(true);
						SA newsItem = new SA();
						newsItem.setArtist(artistName);
						newsItem.setAlbum(albumName);
						albumHref = albumHref.split(":")[2];
						newsItem.setHref(albumHref);
						newsItem.setAvailability(availability);
						newsItem.setCreated(sa.getCreated());
						Log.d(TAG, "found it!");
						news.add(newsItem);
					}
				}
				sa.setNews(news);
				sar.setSA(sa);
			} catch (JSONException e) {
				Log.e(TAG, "parse fail", e);
			}
		}
		Log.d(TAG, "done spotify artist news");
		return sar;
	}
}
