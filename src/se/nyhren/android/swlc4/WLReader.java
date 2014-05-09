package se.nyhren.android.swlc4;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.nyhren.android.swlc4.store.DBX;
import se.nyhren.android.swlc4.vo.SA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class WLReader {
	private String uc = "";
	private JSONArray aas = null;
	private JSONArray ans = null ;
	@SuppressLint("SimpleDateFormat")
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final String TAG = "SWLC"; 

	public WLReader(Context ctx) throws IOException {
		DBX dbx = new DBX(ctx);
		String response = dbx.fetch();
		Log.d("swlc", response);
		uc = response;
		Log.d(TAG,"Read " + uc);
			JSONObject data = null;
			try {
				data = new JSONObject(uc);
				aas = (JSONArray) data.get("artistAlbums");
				ans = (JSONArray) data.get("news");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
	}
	
	public List<SA> getArtistAlbums() {
		ArrayList<SA> aa = new ArrayList<SA>();
		for (int i = 0; i < aas.length(); i++) {
			SA sa = new SA();
			JSONObject o = null;
			try {
				 o= (JSONObject) aas.get(i);
			sa.setArtist((String) o.get("artist"));
			sa.setAlbum((String) o.get("album"));
				sa.setCreated(sdf.parse((String) o.get("added")).getTime());
			} catch (java.text.ParseException e) {
				Log.e(TAG,"failed parsing created " + o.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aa.add(sa);
		}
		return aa;
	}
	
	public List<SA> getArtistNews() {
		ArrayList<SA> an = new ArrayList<SA>();
		for (int i = 0; i < ans.length(); i++) {
			SA sa = new SA();
			JSONObject o = null;
			try {
				o = (JSONObject) ans.get(i);
			sa.setArtist((String) o.get("artist"));
				sa.setCreated(sdf.parse((String) o.get("added")).getTime());
			} catch (java.text.ParseException e) {
				Log.w(TAG,"failed parsing created " + o.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			an.add(sa);
		}
		return an;
	}
}
