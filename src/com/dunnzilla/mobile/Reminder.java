package com.dunnzilla.mobile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;


public class Reminder {
	// --- Database fields: ---
	private int		ID;
	private int		contactID;
	private String	displayName;
    private String	actionURI;
	private String	note;
    private Date	dateStart;
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
    public Reminder(Cursor cursor_reminder_db) {
    	setContactID(cursor_reminder_db.getInt(cursor_reminder_db.getColumnIndex(DBConst.f_CONTACT_ID)));
    	setNote(cursor_reminder_db.getString(cursor_reminder_db.getColumnIndex(DBConst.f_NOTE)));
    	setPeriod(cursor_reminder_db.getInt(cursor_reminder_db.getColumnIndex(DBConst.f_PERIOD)));
    	setActionURI(cursor_reminder_db.getString(cursor_reminder_db.getColumnIndex(DBConst.f_URI_ACTION)));
    	
    	String ds = cursor_reminder_db.getString(cursor_reminder_db.getColumnIndex(DBConst.f_DATETIME_START));
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		try {
			d = f.parse(ds);
	    	setDateStart(d);
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
}
