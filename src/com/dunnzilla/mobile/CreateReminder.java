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
//import android.widget.Button;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CreateReminder extends Activity {
	static final int PICK_CONTACT = 1001;
	private int    m_contactID;
	private String m_displayName;
    private String m_type;
    private Bitmap m_ContactIconBitmap;
    private DB 	   m_db;
    private String m_errorMessage;
    private static final String TAG = "CreateReminder";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_reminder);
        
        m_ContactIconBitmap = null;
        
        View.OnClickListener vocl_pickContact = new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		startActivityForResult(i, PICK_CONTACT);
        	}
    	}; 
    	
    	m_db = new DB(this);
    	m_db.open();
        
        ImageButton ContactIcon  = (ImageButton) findViewById(R.id.cr_contact_icon);
        TextView tvContactName = (TextView) findViewById(R.id.cr_text_who);
        Button bSave = (Button) findViewById(R.id.cr_save);
        
        ContactIcon.setOnClickListener( vocl_pickContact );
        tvContactName.setOnClickListener( vocl_pickContact );
        bSave.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		if( validateSettings() ) {
        			saveReminder(); /** @todo Move into a smarter class when adding the Edit ability (v0.6?) */
        			finish();
        		} else {
        			Toast.makeText(CreateReminder.this, CreateReminder.this.getErrMessage(), Toast.LENGTH_SHORT).show();
        		}
        	}
        });        
    }
	
	public boolean validateSettings() {
		if( m_contactID <= 0) {
			CreateReminder.this.setErrMessage("Choose a contact.");
			return false;
		}

		if( CreateReminder.this.m_displayName.length() <= 0 || CreateReminder.this.m_displayName == "null" ) {
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
    	Log.v(TAG, "Saving " + CreateReminder.this.m_displayName);
    }
    protected void updateLayout(Intent _intent) {
    	if( m_contactID <= 0 ) {
    		return;
    	}
		ImageView ivContactIcon = (ImageView) findViewById(R.id.cr_contact_icon);
		if(m_ContactIconBitmap != null) {
			ivContactIcon.setImageBitmap(m_ContactIconBitmap);
    	}

    	if( m_displayName.length() > 0 ) {
    		TextView tvName = (TextView) findViewById(R.id.cr_text_who);
    		tvName.setText(m_displayName);
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
    	   m_contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
           m_displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
           Bitmap b = loadContactPhoto(m_contactID);
           if( b != null ) {
        	   m_ContactIconBitmap = b;
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
