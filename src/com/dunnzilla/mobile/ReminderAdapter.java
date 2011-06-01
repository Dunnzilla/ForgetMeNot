package com.dunnzilla.mobile;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.widget.LinearLayout.LayoutParams;

public class ReminderAdapter extends BaseAdapter implements ListAdapter {

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
			
			ImageButton ib = new ImageButton(context);
			ib.setId(1);
			tvName = new TextView(context);
			tvName.setId(2);
			tvNote = new TextView(context);
			tvNote.setId(3);
			//v.setOrientation(LinearLayout.VERTICAL);
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
	           
			tvName.setText(reminders.get(arg0).getDisplayName());
			tvName.setShadowLayer(4.0f, 4.0f, 4.0f, 0xFF000000);
			// TODO change color based on whatever silliness the user wants.
			tvName.setTextColor(0xFFFFFFFF);
			tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PT, 8);
			tvNote.setText(reminders.get(arg0).getNote());

			// Set up the relative positions
			lp_ibContactIcon.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			
			lp_tvName.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp_tvName.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvName.setMargins(5, 0, 0, 0);

			lp_tvNote.addRule(RelativeLayout.BELOW, tvName.getId());
			lp_tvNote.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvNote.setMargins(5, 0, 0, 0);

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
