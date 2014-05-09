package se.nyhren.android.swlc4.store;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import android.app.Activity;
import android.content.Context;

public class DBX {
	private DbxAccountManager mAccountManager;
	private DbxAccount mAccount;
	public static final int REQUEST_LINK_TO_DBX = 0; // This value is up to you

	public DBX(Context ctx) {
		mAccountManager = DbxAccountManager.getInstance(ctx, "xxxx",
				"yyyy");
	}

	public void connect(Activity cb) {
		mAccountManager.startLink(cb, REQUEST_LINK_TO_DBX);
	}
	
	public void unlink() {
		mAccountManager.unlink();
	}

	public boolean isConnected() {
		return mAccountManager.hasLinkedAccount();
	}

	public String fetch() {
		mAccount = mAccountManager.getLinkedAccount();
		DbxDatastore store = null;
		try {
			store = DbxDatastore.openDefault(mAccount);
			store.sync();
			DbxTable tbl = store.getTable("swl");
			DbxRecord r = tbl.get("1");
			String data = r.getString("data");			
			return data;
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (null != store) store.close();
		}
	}
}
