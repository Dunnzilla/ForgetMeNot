package com.dunnzilla.mobile;

import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

public class CreateReminder extends Activity {
	static final int PICK_CONTACT = 1001;
	private String m_displayName;
    private String m_type;
    private String m_emailAddress;
    private String m_phoneNumber;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_reminder);

        Button bContactIcon = (Button) findViewById(R.id.cr_contact_icon);
        bContactIcon.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		startActivityForResult(i, PICK_CONTACT);
        	}
    	});
    	
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent _data) {
		if ( resultCode == RESULT_OK ) {
	    	switch(requestCode) {
	    	case PICK_CONTACT:
	    		getContactInfo(_data);
	       		break;
	    	}
		}
    	super.onActivityResult(requestCode, resultCode, _data);
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
           String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
           m_displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
           String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
           if ( hasPhone.equalsIgnoreCase("1"))
               hasPhone = "true";
           else
               hasPhone = "false" ;

           if (Boolean.parseBoolean(hasPhone)) 
           {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
            while (phones.moveToNext()) 
            {
              m_phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phones.close();
           }

           Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,null, null);
           while (emails.moveToNext()) 
           {
            m_emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
           }
           emails.close();

      }  while(cursor.moveToNext());        
       cursor.close();
    }//getContactInfo
    
    public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }

}
