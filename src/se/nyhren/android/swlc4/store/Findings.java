package se.nyhren.android.swlc4.store;

import java.util.List;

import se.nyhren.android.swlc4.util.Serializer;
import se.nyhren.android.swlc4.vo.SA;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Findings {
	@SuppressWarnings("unchecked")
	public static List<SA> get(Context ctx) {
		Store db = new Store(ctx);
		SQLiteDatabase sdb = db.getWritableDatabase();
		Cursor cursor = sdb.rawQuery("select data from findings", null);
		byte[] data = null;
		if (cursor.moveToFirst()) {
			data = cursor.getBlob(0);
		}
		if (null != cursor)
			cursor.close();
		if (null != sdb)
			sdb.close();
		if (null != db)
			db.close();
		if (data != null) {
			return (List<SA>) Serializer.deserializeObject(data);
		} else {
			return null;
		}
	}

	public static void set(Context ctx, List<SA> list) {
		Store db = new Store(ctx);
		SQLiteDatabase sdb = db.getWritableDatabase();
		sdb.delete("findings", null, null);
		ContentValues values = new ContentValues();
		values.put("data", Serializer.serializeObject(list));
		sdb.insert("findings", null, values);
		if (null != sdb)
			sdb.close();
		if (null != db)
			db.close();
	}
}
