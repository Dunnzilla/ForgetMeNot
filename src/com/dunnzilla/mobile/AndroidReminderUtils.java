package com.dunnzilla.mobile;

import java.io.InputStream;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class AndroidReminderUtils {

    public static Reminder loadReminderFromID(Activity a, DBReminder db, long id) {
		Cursor cu = db.selectID(id);
		Reminder newReminder = null;
		if(cu.moveToFirst()) {
			newReminder = new Reminder(cu);
	    	Uri uriPerson = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, newReminder.getContactID());
	    	// Then query for this specific record:
	    	Cursor cursorPerson = a.managedQuery(uriPerson, null, null, null, null);

	        if( cursorPerson.moveToFirst()) {
		        do {
		     	   // TODO SPA-12 try/catch
		        	newReminder.setContactID(cursorPerson.getInt(cursorPerson.getColumnIndex(ContactsContract.Contacts._ID)));
		        	newReminder.setDisplayName(cursorPerson.getString(cursorPerson.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
		     	   
		           InputStream streamPhoto = ContactsContract.Contacts.openContactPhotoInputStream(a.getContentResolver(), uriPerson);
		           if (streamPhoto != null) {
		        	   newReminder.setContactIconBitmap(BitmapFactory.decodeStream(streamPhoto));
		           }
		       }  while(cursorPerson.moveToNext());
	        }
	        cursorPerson.close();
		}
		cu.close();
		return newReminder;
    }
    public static void getContactInfo(Activity a, Reminder r, Intent _intent)
    {
    	// TODO managedQuery() is deprecated in API 11, replaced by CursorLoader
    	Uri u = _intent.getData(); 
    	Cursor cursor = a.managedQuery(u, null, null, null, null);
       if( ! cursor.moveToFirst()) {
    	   cursor.close();
    	   return;
       }
       // TODO why am I potentially overwriting the reminder contact ID and other values multiple times? 
       do {
    	   // TODO SPA-12 try/catch
    	   // TODO don't mix up getColumnIndex with getColumnIndexOrThrow
    	   r.setContactID(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
    	   r.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
           Bitmap b = loadContactPhoto(a, r.getContactID());
           if( b != null ) {
        	   r.setContactIconBitmap(b);
           }
      }  while(cursor.moveToNext());
       cursor.close();
    }
 
    public static Bitmap loadContactPhoto(Context c, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(c.getContentResolver(), uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }    

}
