package com.dunnzilla.mobile;

import java.io.InputStream;
import java.util.ArrayList;

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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class ReminderAdapter extends BaseAdapter implements ListAdapter {

	private static final String TAG = "ReminderAdapter";
	private ArrayList<Reminder> reminders;
	private Context context;
	TextView tvName;
	TextView tvNote;

	public ReminderAdapter(Context _context, ArrayList<Reminder> _reminders) {
		reminders = _reminders;
		context = _context;
	}

	@Override
	public int getCount() {
		if (reminders == null)
			return 0;

		return reminders.size();
	}

	@Override
	public boolean isEmpty() {
		if (reminders == null)
			return true;

		if (reminders.size() <= 0)
			return true;

		return false;
	}

	@Override
	public Object getItem(int arg0) {
		return reminders.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return reminders.get(arg0).getID();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int arg0, View oldView, ViewGroup vgParent) {
		RelativeLayout v;
		TextView tvName, tvNote;
		if (oldView == null) {		 
			Reminder r = reminders.get(arg0);
			v = new RelativeLayout(context);

			v.setId(1);
			ImageButton ib = new ImageButton(context);
			ib.setId(2);
			tvName = new TextView(context);
			tvName.setId(3);
			tvNote = new TextView(context);
			tvNote.setId(4);

			RelativeLayout.LayoutParams lp_tvName = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_tvNote = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_ibContactIcon = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			Bitmap b = loadContactPhoto(r.getContactID());
			if (b != null) {
				ib.setImageBitmap(b);
			} else {
				ib.setImageResource(R.drawable.ic_contact_picture);
			}
	           
			tvName.setText(r.getDisplayName());
			tvName.setShadowLayer(4.0f, 4.0f, 4.0f, 0xFF000000);
			// TODO change color based on whatever silliness the user wants.
			tvName.setTextColor(0xFFFFFFFF);
			tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PT, 8);
			tvNote.setText(r.getNote());

			// Set up the relative positions
			lp_ibContactIcon.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			
			lp_tvName.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp_tvName.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvName.setMargins(5, 0, 0, 0);

			lp_tvNote.addRule(RelativeLayout.BELOW, tvName.getId());
			lp_tvNote.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvNote.setMargins(5, 0, 0, 0);
			
			// Setup the OnClickListener which will be used for all the views in this listview entry
			OnClickListener oc = new View.OnClickListener() {
	        	public void onClick(View view) {
	        		ArrayList<String> arPhones = new ArrayList<String>();
	        		Reminder r = (Reminder) view.getTag(R.string.TAG_ID_ReminderAdapter_Reminder);
	        		final Context contextParent = (Context) view.getTag(R.string.TAG_ID_ReminderAdapter_Context);
	        		
					Cursor phones = contextParent.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + r.getContactID(), null, null);
					while (phones.moveToNext()) {
						String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Log.v(TAG, "Phone # is " + phoneNumber);
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
										//editText.setText(selectedEmail);
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
					contextParent.startActivity(callIntent);
	        	}
	        };

	        // TODO There has got to be a better way:
			ib.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
			ib.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
			tvName.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
			tvName.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
			tvNote.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
			tvNote.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
			v.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
			v.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);

			
			ib.setOnClickListener(oc);
			tvName.setOnClickListener(oc);
			tvNote.setOnClickListener(oc);
			v.setOnClickListener(oc);

			// Let's add them to the view!
			v.addView(ib, lp_ibContactIcon);
			v.addView(tvName, lp_tvName);
			v.addView(tvNote, lp_tvNote);
		} else {
			v = (RelativeLayout) oldView;
		}
		return v;
	}
    public Bitmap loadContactPhoto(long id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }
}
