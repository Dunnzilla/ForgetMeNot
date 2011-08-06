package com.dunnzilla.mobile;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.app.Activity;


public class Reminder {
	// --- Database fields: ---
	private int		ID;
	private int		contactID;
	private String	displayName;
    private String	actionURI;
	private String	note;
    private Date	dateStart,
    				dateStop;
    private int		period;
	// --- App fields ---
    private Bitmap contactIconBitmap;
    private static final String TAG = "Reminder";

    // ===========================================
    public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

    
    public Date getDateStart() {
		return dateStart;
	}
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	// -----------------------------
    public Reminder() {
    	contactID = 0;
    	contactIconBitmap = null;
    }
    public Reminder(DBReminder db, long reminderID) {
    	contactID = 0;
    	contactIconBitmap = null;
    	// TODO !! Should I store db in the Reminder class?  If so, does it matter that multiple classes
    	// have a DB handle open at once (or can they share?)
    	Cursor cu = db.selectID(reminderID);
		if (cu.moveToFirst()) {
			setFrom(cu);
		}    	
		cu.close();
    }
    public Reminder(Cursor c) {
    	setFrom(c);
    }
    public void setFrom(Cursor c) {
    	setID(c.getInt(c.getColumnIndex(DBConst.f_ID)));
    	setContactID(c.getInt(c.getColumnIndex(DBConst.f_CONTACT_ID)));
    	setNote(c.getString(c.getColumnIndex(DBConst.f_NOTE)));
    	setPeriod(c.getInt(c.getColumnIndex(DBConst.f_PERIOD)));
    	setActionURI(c.getString(c.getColumnIndex(DBConst.f_URI_ACTION)));
    	
    	String dstart = c.getString(c.getColumnIndex(DBConst.f_DATETIME_START));
    	String dstop = c.getString(c.getColumnIndex(DBConst.f_DATETIME_START));
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		try {
			d = f.parse(dstart);
	    	setDateStart(d);
			d = f.parse(dstop);
	    	setDateStop(d);
		} catch (ParseException e) {
			Log.w(TAG, e.getMessage());
		}
    }
    
    public boolean valid() {
    	if(contactID <= 0) {
    		return false;
    	}
    	return true;
    }
    
    public Uri updateFromContactsContract(Activity a) { 
		Uri uriPerson = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, getContactID());
		Cursor cursorPerson = a.managedQuery(uriPerson, null, null, null, null);
	
	    if( cursorPerson.moveToFirst()) {
	        do {
				// TODO try/catch
				/**
				 * TODO Use LOOKUP_KEY instead of _ID? (LOOKUP_KEY is An opaque
				 * value that contains hints on how to find the contact if its
				 * row id changed as a result of a sync or aggregation.
				 * http://developer.android.com/reference/android/provider/ContactsContract.Contacts.html
				 **/
	        	
	     	   setContactID(cursorPerson.getInt(cursorPerson.getColumnIndex(ContactsContract.Contacts._ID)));
	     	   setDisplayName(cursorPerson.getString(cursorPerson.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
	     	   
	           InputStream streamPhoto = ContactsContract.Contacts.openContactPhotoInputStream(a.getContentResolver(), uriPerson);
	           if (streamPhoto != null) {
	        	   setContactIconBitmap(BitmapFactory.decodeStream(streamPhoto));
	           }
	       }  while(cursorPerson.moveToNext());
	    }
	    cursorPerson.close();
	    return uriPerson;
    }

    // -----------------------------
	public int getContactID() {
		return contactID;
	}
	public void setContactID(int contactID) {
		this.contactID = contactID;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getActionURI() {
		return actionURI;
	}
	public void setActionURI(String actionURI) {
		this.actionURI = actionURI;
	}
    public Bitmap getContactIconBitmap() {
		return contactIconBitmap;
	}
	public void setContactIconBitmap(Bitmap contactIconBitmap) {
		this.contactIconBitmap = contactIconBitmap;
	}
	public int getID() {
		return ID;
	}
	public void setID(int _ID) {
		ID = _ID;
	}
	public Date getDateStop() {
		return dateStop;
	}
	public void setDateStop(Date dateStop) {
		this.dateStop = dateStop;
	}
}
