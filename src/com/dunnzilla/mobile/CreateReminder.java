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
import android.view.View;
//import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

public class CreateReminder extends Activity {
	static final int PICK_CONTACT = 1001;
	private int    m_contactID;
	private String m_displayName;
    private String m_type;
    private String m_emailAddress;
    private String m_phoneNumber;
    private Bitmap m_ContactIconBitmap;

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

        DB db = new DB(this);
        db.open();
        
        ImageButton ContactIcon  = (ImageButton) findViewById(R.id.cr_contact_icon);
        TableRow tContactRow = (TableRow) findViewById(R.id.cr_row01);
        
        ContactIcon.setOnClickListener( vocl_pickContact );
        tContactRow.setOnClickListener( vocl_pickContact );
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
           
           String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
           if ( hasPhone.equalsIgnoreCase("1"))
               hasPhone = "true";
           else
               hasPhone = "false" ;

           if (Boolean.parseBoolean(hasPhone)) 
           {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ m_contactID,null, null);
            while (phones.moveToNext()) 
            {
              m_phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phones.close();
           }

           Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + m_contactID,null, null);
           while (emails.moveToNext()) 
           {
            m_emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
           }
           emails.close();

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
