package com.dunnzilla.mobile;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBReminder extends DB {
	
	private static final String TAG = "com.dunnzilla.mobile.DBReminder";
	
	public DBReminder(Context c) {
		super(c);
	}
	// ======================================================================
	// Reminder-specific
	// ======================================================================
	public void insert(Reminder r) {
		try {
			open();
			ContentValues cv = new ContentValues();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

			cv.put(DBConst.f_CONTACT_ID, r.getContactID());
			cv.put(DBConst.f_DATETIME_START, dateFormat.format(r.getDateStart()));
			cv.put(DBConst.f_PERIOD, r.getPeriod());
			cv.put(DBConst.f_URI_ACTION, r.getActionURI());
			cv.put(DBConst.f_NOTE, r.getNote());
			cv.put(DBConst.f_DATETIME_STOP, dateFormat.format(r.getDateStop()));
			cv.put(DBConst.f_DATETIME_NEXT, dateFormat.format(r.getDateNext()));

			db.insert(DBConst.TABLE, null, cv);

		} catch (SQLiteException e) {
			Log.w(TAG, e.getMessage());
		}
	}
	public void delete(Reminder r) {
		delete(r.getID());
	}
	public int update(Reminder r, ContentValues contentvals) {
		String where = DBConst.f_ID + "=?";
		String[] whereArgs = { Integer.toString(r.getID()) };
		return db.update(DBConst.TABLE, contentvals, where, whereArgs);
	}
	
	public void set_datetime_next(Reminder r, String _newVal) {		
		String[] args = { new Integer(r.getID()).toString() };
		String query =
			"UPDATE " + DBConst.TABLE
		  + " SET "   + DBConst.f_DATETIME_NEXT + "=" + _newVal
		  + " WHERE " + DBConst.f_ID +"=?";
		Log.i(TAG, query);
		Cursor cu = db.rawQuery(query, args);
		cu.moveToFirst();
		cu.close();		
	}
	public Cursor selectAllActiveByDue() {
		Cursor c;
		String orderBy = DBConst.f_DATETIME_NEXT + " asc";
		// My eyes, they bleed:
		c = db.query(DBConst.TABLE, null, null, null, null, null, orderBy);
		return c;
	}
}
