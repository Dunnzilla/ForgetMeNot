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
//import android.widget.Button;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CreateReminder extends Activity {
	static final int 			PICK_CONTACT = 1001;
    private static final String TAG = "CreateReminder";
    
    private DB 	   m_db;
    private String m_errorMessage;
    Reminder		reminder;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_reminder);
        
        View.OnClickListener vocl_pickContact = new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		startActivityForResult(i, PICK_CONTACT);
        	}
    	}; 
    	
    	m_db = new DB(this);
    	m_db.open();
    	reminder = new Reminder();
        
        ImageButton ContactIcon  = (ImageButton) findViewById(R.id.cr_contact_icon);
        TextView tvContactName = (TextView) findViewById(R.id.cr_text_who);
        Button bSave = (Button) findViewById(R.id.cr_save);
        
        ContactIcon.setOnClickListener( vocl_pickContact );
        tvContactName.setOnClickListener( vocl_pickContact );
        bSave.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		if( CreateReminder.this.validateSettings() ) {
        			CreateReminder.this.saveReminder();  // TODO Possibly move into a smarter class when adding the Edit ability (v0.6?)
        			CreateReminder.this.finish();
        		} else {
        			Toast.makeText(CreateReminder.this, CreateReminder.this.getErrMessage(), Toast.LENGTH_SHORT).show();
        		}
        	}
        });        
    }
	
	public boolean validateSettings() {
		if( CreateReminder.this.reminder.getContactID() <= 0) {
			CreateReminder.this.setErrMessage("Choose a contact.");
			return false;
		}
		return true;
	}
	
	public String getErrMessage() {
		return m_errorMessage;
	}
	private void setErrMessage(String e) {
		m_errorMessage = e;
	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if ( resultCode == RESULT_OK ) {
	    	switch(requestCode) {
	    	case PICK_CONTACT:
	    		getContactInfo(intent);
	    		updateLayout(intent);
	       		break;
	    	}
		}
    	super.onActivityResult(requestCode, resultCode, intent);
    }

    public void saveReminder() {
    	DatePicker dp = (DatePicker)findViewById(R.id.cr_datepicker_start);
    	Date d = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
    	CreateReminder.this.reminder.setDateStart(d);
    	Log.v(TAG, "Saving " + CreateReminder.this.reminder.getDisplayName() + " starting date " + "");
    	CreateReminder.this.m_db.insert(reminder);
    }
    protected void updateLayout(Intent _intent) {
    	if( ! reminder.valid() ) {
    		/** @todo Do something to visually indicate the contact chosen is invalid, or is pending selection */
    		return;
    	}
		ImageView ivContactIcon = (ImageView) findViewById(R.id.cr_contact_icon);
		if( reminder.getContactIconBitmap() != null) {
			ivContactIcon.setImageBitmap(reminder.getContactIconBitmap());
    	}

    	if( reminder.getDisplayName().length() > 0 ) {
    		TextView tvName = (TextView) findViewById(R.id.cr_text_who);
    		tvName.setText(reminder.getDisplayName());
    		tvName.setTextColor(0xFFFFFFFF);
    	}
    }
    protected void getContactInfo(Intent _intent)
    {
    	/**
    	 * @todo managedQuery() is deprecated in API 11, replaced by CursorLoader
    	 */
    	Uri u = _intent.getData(); 
    	Cursor cursor = managedQuery(u, null, null, null, null);
       if( ! cursor.moveToFirst()) {
    	   cursor.close();
    	   return;
       }
       do {           
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
