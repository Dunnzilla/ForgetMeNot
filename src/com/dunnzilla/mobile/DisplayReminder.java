package com.dunnzilla.mobile;

import java.io.InputStream;
import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayReminder extends Activity {
    private static final String TAG = "DisplayReminder";
    
    private DB		m_db;
    Reminder		reminder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_reminder);
        
    	m_db = new DB(this);
    	m_db.open();

        // TODO how do I pass in the reminder # to view, and how to parse the Intent which describes it?
        loadReminderFromID(1);
        Intent i = new Intent();
        updateLayout(i);
	}

    protected void loadReminderFromID(long id) {
		Cursor cu = m_db.selectID(id);
		if(cu.moveToFirst()) {
			reminder = new Reminder(cu);
	    	Uri uriPerson = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, reminder.getContactID());
	    	// Then query for this specific record:
	    	Cursor cursorPerson = managedQuery(uriPerson, null, null, null, null);

	        if( cursorPerson.moveToFirst()) {
		        do {
		     	   // TODO try/catch
		     	   reminder.setContactID(cursorPerson.getInt(cursorPerson.getColumnIndex(ContactsContract.Contacts._ID)));
		     	   reminder.setDisplayName(cursorPerson.getString(cursorPerson.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
		     	   
		           InputStream streamPhoto = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uriPerson);
		           if (streamPhoto != null) {
		        	   reminder.setContactIconBitmap(BitmapFactory.decodeStream(streamPhoto));
		           }
		       }  while(cursorPerson.moveToNext());
	        }
	        cursorPerson.close();
		}
    }
    protected void updateLayout(Intent _intent) {
    	if( ! reminder.valid() ) {
    		// TODO Do something to visually indicate the contact chosen is invalid, or is pending selection
    		return;
    	}
		ImageView ivContactIcon = (ImageView) findViewById(R.id.vr_contact_icon);
		if( reminder.getContactIconBitmap() != null) {
			ivContactIcon.setImageBitmap(reminder.getContactIconBitmap());
    	}

    	if( reminder.getDisplayName().length() > 0 ) {
    		TextView tvName = (TextView) findViewById(R.id.vr_text_who);
    		tvName.setText(reminder.getDisplayName());
    		tvName.setTextColor(0xFFFFFFFF);
    	}
    }
    protected void getContactInfo(Intent _intent)
    {
    	// TODO managedQuery() is deprecated in API 11, replaced by CursorLoader
    	Uri u = _intent.getData(); 
    	Cursor cursor = managedQuery(u, null, null, null, null);
       if( ! cursor.moveToFirst()) {
    	   cursor.close();
    	   return;
       }
       // TODO why am I potentially overwriting the reminder contact ID and other values multiple times? 
       do {
    	   // TODO try/catch
    	   reminder.setContactID(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
    	   reminder.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
           Bitmap b = loadContactPhoto(reminder.getContactID());
           if( b != null ) {
        	   reminder.setContactIconBitmap(b);
           }
      }  while(cursor.moveToNext());
       cursor.close();
    }//getContactInfo
    
    public Bitmap loadContactPhoto(long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }
}
