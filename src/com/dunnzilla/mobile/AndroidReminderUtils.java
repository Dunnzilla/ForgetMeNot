package com.dunnzilla.mobile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

public class AndroidReminderUtils {
	public static final String INTENT_EXTRAS_KEY_REMINDER_ID = "REMINDER_ID";


	public static View.OnClickListener genOnClickDoVoiceDial() {
		return new View.OnClickListener() {
        	public void onClick(View view) {
        		Reminder r = (Reminder) view.getTag(R.string.TAG_ID_ReminderAdapter_Reminder);
        		final Context contextParent = (Context) view.getTag(R.string.TAG_ID_ReminderAdapter_Context);					
        		ArrayList<String> arPhones = new ArrayList<String>();

				Cursor phones = contextParent.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + r.getContactID(), null, null);
				while (phones.moveToNext()) {
					String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					arPhones.add(phoneNumber);
				}

				phones.close();
				Uri uri;
				if(arPhones.size() == 0) {
					Toast.makeText(contextParent, R.string.MSG_ERR_CONTACT_NO_PHONE, Toast.LENGTH_SHORT).show();
					return;
				} else if(arPhones.size() > 1) {
					
					final String[] phonesArr = new String[arPhones.size()];
					for (int i = 0; i < arPhones.size(); i++) {
						phonesArr[i] = arPhones.get(i);
					}

					AlertDialog.Builder dialog = new AlertDialog.Builder(contextParent);
					dialog.setTitle(R.string.TITLE_PICK_PHONE);
					((Builder) dialog).setItems(phonesArr,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String selectedPhone = phonesArr[which];
									Uri uri = Uri.parse("tel://" + selectedPhone);
									Intent callIntent = new Intent(Intent.ACTION_CALL, uri); 
									contextParent.startActivity(callIntent);
									return;
								}
							}).create();
					dialog.show();
                    return;             
				} else {
					uri = Uri.parse("tel://" + arPhones.get(0));
				}

				Intent callIntent = new Intent(Intent.ACTION_CALL, uri);
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				contextParent.startActivity(callIntent);
        	}
        };
	}
    public static Reminder loadReminderFromID(Activity a, DBReminder db, long id) {
    	db.open();
		Cursor cu = db.selectID(id);
		Reminder newReminder = null;
		try {
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
		} catch(Exception e) {
			// TODO
		}
		cu.close();
		db.close();
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
    
    public static String formatDate(Date d) {
    	// TODO let user pick date format preferences ( - or - consult system settings? )
    	String s = (1900 + d.getYear()) + "/" + (d.getMonth() + 1) + "/" + (d.getDate());
    	return s;
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
