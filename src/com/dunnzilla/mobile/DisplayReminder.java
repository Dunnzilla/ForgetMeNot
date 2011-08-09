package com.dunnzilla.mobile;

import java.io.InputStream;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayReminder extends Activity {
    private static final String TAG = "DisplayReminder";
	public static final String INTENT_EXTRAS_KEY_REMINDER_ID = "REMINDER_ID";
    
    private DBReminder	db;
    Reminder			reminder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_reminder);
        
    	db = new DBReminder(this);
    	db.open();
        
        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
        	long idReminder = extras.getLong(INTENT_EXTRAS_KEY_REMINDER_ID);
        	Log.v(TAG, "Loading ID " + idReminder);
            loadReminderFromID(idReminder);
        }
        getApplicationContext();

        
    	// TODO handle no reminder ID passed in, or an invalid reminder ID
        Button bCreate = (Button) findViewById(R.id.disprem_btn_done);
        bCreate.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		try {
        			String strSummary = reminder.onEventComplete(db);
        			Toast.makeText(DisplayReminder.this, strSummary, Toast.LENGTH_SHORT).show();
        		}
        		catch(Exception e) {
        			Toast.makeText(DisplayReminder.this, e.getMessage(), Toast.LENGTH_SHORT).show();        			
        		}
      			DisplayReminder.this.finish();
        	}
        });

        Button bSnooze = (Button) findViewById(R.id.disprem_btn_snooze);
        bSnooze.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		try {
        			String strSummary = reminder.onEventSnooze(db);
        			Toast.makeText(DisplayReminder.this, strSummary, Toast.LENGTH_SHORT).show();
        		}
        		catch(Exception e) {
        			Toast.makeText(DisplayReminder.this, e.getMessage(), Toast.LENGTH_SHORT).show();        			
        		}
      			DisplayReminder.this.finish();
        	}
        });

        Button bDelete = (Button) findViewById(R.id.disprem_btn_delete);
        bDelete.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		try {
        			String strSummary = reminder.onEventDelete(db);
        			Toast.makeText(DisplayReminder.this, strSummary, Toast.LENGTH_SHORT).show();
        		}
        		catch(Exception e) {
        			Toast.makeText(DisplayReminder.this, e.getMessage(), Toast.LENGTH_SHORT).show();        			
        		}
      			DisplayReminder.this.finish();
        	}
        });

        
        Intent i = new Intent();
        updateLayout(i);
	}
    @Override
    public void onStop() {    	
    	db.close();
    	super.onStop();
    }

    protected void loadReminderFromID(long id) {
		Cursor cu = db.selectID(id);
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
		cu.close();
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

    	if( reminder.getNote() != null && reminder.getNote().length() > 0 ) {
    		TextView tv = (TextView) findViewById(R.id.vr_text_note);
    		tv.setText(reminder.getNote());
    		tv.setTextColor(0xFFFFFFFF);
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
