package com.dunnzilla.mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DB {
	private SQLiteDatabase		m_DB;
	private final Context		m_Context;
	private final DBHelper		m_DBHelper;
	
	
	public DB(Context c) {
		m_Context = c;
		m_DBHelper = new DBHelper(m_Context, DBConst.DBNAME, null, DBConst.VERSION);
		
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
