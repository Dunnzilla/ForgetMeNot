package com.dunnzilla.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;

public class OutboundCallReceiver extends BroadcastReceiver {
	private static final String TAG = "OutboundCallReceiver";
    @Override 
    public void onReceive(Context context, Intent intent) {
            try {
				String strOutgoing = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        		try {
        			Log.v(TAG, "Trying to look up phone number '" + strOutgoing + "'");
        			DBReminder dbr = new DBReminder(context);
        			Cursor cContacts = dbr.selectAllContactsForPhoneNumber(context, strOutgoing);
        			if(cContacts != null && cContacts.moveToFirst()) {
        				do {
	    					String contactID = cContacts.getString(cContacts.getColumnIndex(PhoneLookup._ID));
	        					Log.v(TAG, "Flagging all reminders for contact #" + contactID + " complete");
	    					Cursor cursorRemindersForContact = dbr.getAllRemindersForContactID(Integer.parseInt(contactID));
	    					if(cursorRemindersForContact != null && cursorRemindersForContact.moveToFirst()) {
	    						do {
		    						Reminder r = new Reminder(cursorRemindersForContact);
		    						if( r != null ) {
		        						Log.v(TAG, "Call started.  Reminder #" + r.getID() + " complete!");
		        						String strSummary = r.onEventComplete(dbr);
		        						Toast.makeText(context, strSummary, Toast.LENGTH_SHORT).show();
		    						}
	    						} while(cursorRemindersForContact.moveToNext());
	    						cursorRemindersForContact.close();
	    					}
        				} while(cContacts.moveToNext());
            			cContacts.close();
        			}
//        			String strSummary = reminder.onEventComplete(db);
//        			Toast.makeText(DisplayReminder.this, strSummary, Toast.LENGTH_SHORT).show();
        		}
        		catch(Exception e) {
        			Log.e(TAG, e.getMessage());
//        			Toast.makeText(DisplayReminder.this, e.getMessage(), Toast.LENGTH_SHORT).show();        			
        		}
				
			} catch (Exception e) {
				// TODO something useful.  For now, monitoring outbound calls to set the contact as Complete is
				// a bonus activity.
				Log.e(TAG, e.getMessage());
			} 
    }
}
