package com.dunnzilla.mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public DBHelper(Context c, String name, CursorFactory cf, int version) {
		super(c, name, cf, version);
	}
//	private static final String TAG = "DBHelper";
	private static final String sql_CREATE_TABLES =
		"CREATE TABLE " + DBConst.TABLE + " ( "
		+ DBConst.f_ID + " integer PRIMARY KEY AUTOINCREMENT, "
		+ DBConst.f_CONTACT_ID + " integer NOT NULL, " 
		+ DBConst.f_DATETIME_C + " datetime DEFAULT CURRENT_TIMESTAMP, " 
		+ DBConst.f_DATETIME_M + " datetime DEFAULT CURRENT_TIMESTAMP, " 
		+ DBConst.f_DATETIME_START + " datetime DEFAULT CURRENT_TIMESTAMP, "
		+ DBConst.f_DATETIME_STOP + " datetime, "
		+ DBConst.f_DATETIME_NEXT + " datetime, "
		+ DBConst.f_PERIOD + " integer NOT NULL, " 
		+ DBConst.f_URI_ACTION + " text, "
		+ DBConst.f_NOTE + " text "
		+ ");";

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(sql_CREATE_TABLES);
		}
		catch(SQLiteException e) {
			//Log.w("Create table exception: ", e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Create upgrade plan so no data is lost.
	} 
}
