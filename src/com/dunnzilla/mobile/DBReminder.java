package com.dunnzilla.mobile;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class DBReminder extends DB {
	
//	private static final String TAG = "com.dunnzilla.mobile.DBReminder";
	
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
			cv.put(DBConst.f_PERIOD, r.getPeriod());
			cv.put(DBConst.f_URI_ACTION, r.getActionURI());
			cv.put(DBConst.f_NOTE, r.getNote());
	    	if(Reminder.FEATURE_DATESTOP) {
				cv.put(DBConst.f_DATETIME_START, dateFormat.format(r.getDateStart()));
	    		cv.put(DBConst.f_DATETIME_STOP, dateFormat.format(r.getDateStop()));
	    	}
			cv.put(DBConst.f_DATETIME_NEXT, dateFormat.format(r.getDateNext()));
			cv.put(DBConst.f_PREF_CONTACT_TYPE, r.getContactType());

			db.insert(DBConst.TABLE, null, cv);
			close();

		} catch (SQLiteException e) {
			//Log.w(TAG, e.getMessage());
		}
	}
	public void delete(Reminder r) {
		delete(r.getID());
	}
	public int update(Reminder r) {
		ContentValues cv = new ContentValues();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		cv.put(DBConst.f_CONTACT_ID, r.getContactID());
		cv.put(DBConst.f_PERIOD, r.getPeriod());
		cv.put(DBConst.f_URI_ACTION, r.getActionURI());
		cv.put(DBConst.f_NOTE, r.getNote());
    	if(Reminder.FEATURE_DATESTOP) {
    		cv.put(DBConst.f_DATETIME_START, dateFormat.format(r.getDateStart()));
    		cv.put(DBConst.f_DATETIME_STOP, dateFormat.format(r.getDateStop()));
    	}
		cv.put(DBConst.f_DATETIME_NEXT, dateFormat.format(r.getDateNext()));
		cv.put(DBConst.f_PREF_CONTACT_TYPE, r.getContactType());


		return update(r, cv);
	}
	public int update(Reminder r, ContentValues contentvals) {
		String where = DBConst.f_ID + "=?";
		String[] whereArgs = { Integer.toString(r.getID()) };
		int rows_affected = db.update(DBConst.TABLE, contentvals, where, whereArgs);
		db.close();
		return rows_affected;
	}
	
	public void set_datetime_next(Reminder r, String _newVal) {		
		String[] args = { new Integer(r.getID()).toString() };
		String query =
			"UPDATE " + DBConst.TABLE
		  + " SET "   + DBConst.f_DATETIME_NEXT + "=" + _newVal
		  + " WHERE " + DBConst.f_ID +"=?";
		Cursor cu = db.rawQuery(query, args);
		cu.moveToFirst();
		cu.close();		
	}
	public Cursor getAllRemindersForContactID(int id) {
		return db.query(DBConst.TABLE, null, (DBConst.f_CONTACT_ID + " = " + id), null, null, null, null);
	}
	public Cursor selectAllContactsForPhoneNumber(Context ctx, String phoneNumber) {
		// What do we want?
		String []queryColumns = new String[] {
				PhoneLookup._ID,
				PhoneLookup.DISPLAY_NAME,
				PhoneLookup.NUMBER
		};
		// When do we want it?
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursorContactResults = ctx.getContentResolver().query(
				uri,
				queryColumns,
				null, null,
				null);

		// RAaaaaAAaaaH!!
		if( ! cursorContactResults.moveToFirst() ) {
			cursorContactResults.close();
			return null;
		}
		return cursorContactResults;		
	}

    protected void getContactsWhere(Intent _intent, String _where)
    {
    	/*
    	Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

    	while(people.moveToNext()) {
    	   int nameFieldColumnIndex = people.getColumnIndex(PhoneLookup.DISPLAY_NAME);
    	   String contact = people.getString(nameFieldColumnIndex);
    	   int numberFieldColumnIndex = people.getColumnIndex(PhoneLookup.NUMBER);
    	   String number = people.getString(numberFieldColumnIndex);
    	}

    	people.close();
    	*/
    }// getContactInfo
    
	public Cursor selectAllActiveByDue() {
		Cursor c;
		String orderBy = DBConst.f_DATETIME_NEXT + " asc";
		if( ! db.isOpen() ) {
			super.open();
		}
		// My eyes, they bleed:
		c = db.query(DBConst.TABLE, null, null, null, null, null, orderBy);
		return c;
	}
}
