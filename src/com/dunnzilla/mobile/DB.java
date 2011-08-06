package com.dunnzilla.mobile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DB {
	protected SQLiteDatabase	db;
	private final Context		context;
	private final DBHelper		dbHelper;
	private static final String TAG = "com.dunnzilla.mobile.DB";
	
	
	public DB(Context c) {
		context = c;
		dbHelper = new DBHelper(context, DBConst.DBNAME, null, DBConst.VERSION);
		open();
	}
	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		}
		catch(SQLiteException ex) {
			Log.v("Error calling getWritableDatabase()", ex.getMessage());
			/** TODO The Android Cookbook has us doing this, and I'm not
			 *  sure why.  Upon failure to open a writeable DB, we just
			 *  return the results of an open-for-read DB without even
			 *  checking the return value.
			 *  Doing it their way for now in case it's some clever
			 *  Android thing.
			 */
			db = dbHelper.getReadableDatabase();
		}
	}
	public void close() {
		db.close();
	}
	public void delete(int _id) {
		try {
			open();
			db.delete(DBConst.TABLE, "_id=" + Integer.toString(_id), null);
		} catch (SQLiteException e) {
			Log.w(TAG, e.getMessage());
		}
	}
	public Cursor selectAll() {
		Cursor c;
		// My eyes, they bleed:
		c = db.query(DBConst.TABLE, null, null, null, null, null, null);
		return c;
	}
	public Cursor selectID(long id) {
		return db.query(DBConst.TABLE, null, ("_id = " + id), null, null, null, null);
	}
	public Cursor selectDue() {
		return db.query(DBConst.TABLE, null, "datetime_next <= datetime('NOW')", null, null, null, null);
	}
	// TODO make an upsert()
}
