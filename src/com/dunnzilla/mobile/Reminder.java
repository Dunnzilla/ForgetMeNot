package com.dunnzilla.mobile;

import java.util.Date;

import android.graphics.Bitmap;

public class Reminder {
	// --- Database fields: ---
	private int		contactID;
	private String	displayName;
    private String	actionURI;
	private String	note;
    private Date	dateStart;
    private int		period;
	// --- App fields ---
    private Bitmap contactIconBitmap;

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
}
