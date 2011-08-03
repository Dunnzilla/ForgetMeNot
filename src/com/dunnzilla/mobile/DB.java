package com.dunnzilla.mobile;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DB {
	private SQLiteDatabase		m_DB;
	private final Context		m_Context;
	private final DBHelper		m_DBHelper;
	private static final String TAG = "com.dunnzilla.mobile.DB";
	
	
	public DB(Context c) {
		m_Context = c;
		m_DBHelper = new DBHelper(m_Context, DBConst.DBNAME, null, DBConst.VERSION);
		open();
	}
	public Cursor selectAll() {
		Cursor c;
		/* My eyes, they bleed: */
		c = m_DB.query(DBConst.TABLE, null, null, null, null, null, null);
		return c;
	}
	public Cursor selectID(long id) {
		return m_DB.query(DBConst.TABLE, null, ("_id = " + id), null, null, null, null);
	}
	public Cursor selectDue() {
		return m_DB.query(DBConst.TABLE, null, "datetime_next <= datetime('NOW')", null, null, null, null);
	}
	public void delete(Reminder r) {
		delete(r.getID());
	}
	public void delete(int _id) {
		try {
			open();
			m_DB.delete(DBConst.TABLE, "_id=" + Integer.toString(_id), null);
		} catch (SQLiteException e) {
			Log.w(TAG, e.getMessage());
		}
	}
	// TODO make an upsert()
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
			// TODO f_DATETIME_STOP


			m_DB.insert(DBConst.TABLE, null, cv);

		} catch (SQLiteException e) {
			Log.w(TAG, e.getMessage());
		}
	}
	public void open() throws SQLiteException {
		try {
			m_DB = m_DBHelper.getWritableDatabase();
		}
		catch(SQLiteException ex) {
			Log.v("Error calling getWritableDatabase()", ex.getMessage());
			/** @todo The Android Cookbook has us doing this, and I'm not
			 *  sure why.  Upon failure to open a writeable DB, we just
			 *  return the results of an open-for-read DB without even
			 *  checking the return value.
			 *  Doing it their way for now in case it's some clever
			 *  Android thing.
			 */
			m_DB = m_DBHelper.getReadableDatabase();
		}
	}
	public void close() {
		m_DB.close();
	}
}
