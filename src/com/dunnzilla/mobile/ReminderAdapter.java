package com.dunnzilla.mobile;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReminderAdapter extends BaseAdapter implements ListAdapter {
	public static final int RA_VIEWID_LAYOUT = 1;	
	public static final int RA_VIEWID_IMAGEBUTTON = 2;
	public static final int RA_VIEWID_NAME = 3;
	public static final int RA_VIEWID_NOTE = 4;
	public static final int RA_VIEWID_ACTION = 5;
	public static final int RA_VIEWID_SUMMARY = 6;
	public static final int RA_VIEWID_SUBLAYOUT = 7;	
	
	public static final int RA_CONTACTICON_MAXWIDTH = 72;
	
	
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
		LinearLayout llRightBar;
		TextView tvName, tvNote, tvSummary;
		ImageButton ib, ibDoIt; 
		Reminder r = reminders.get(arg0);
		if (oldView == null) {		 
			v = new RelativeLayout(context);
			llRightBar = new LinearLayout(context);

			v.setId( RA_VIEWID_LAYOUT );
			llRightBar.setId( RA_VIEWID_SUBLAYOUT );

			ib = new ImageButton(context);
			ib.setPadding(2, 2, 2, 2);
			ib.setAdjustViewBounds(true);
			ib.setScaleType(ImageView.ScaleType.FIT_XY);
			ib.setId( RA_VIEWID_IMAGEBUTTON );
			ib.setMaxHeight(RA_CONTACTICON_MAXWIDTH);
			ib.setMaxWidth(RA_CONTACTICON_MAXWIDTH);
			ib.setFocusable(false);

			tvName = new TextView(context);
			tvName.setId( RA_VIEWID_NAME );
			tvName.setFocusable(false);
			tvNote = new TextView(context);
			tvNote.setId( RA_VIEWID_NOTE );
			tvNote.setFocusable(false);
			
			ibDoIt = new ImageButton(context);
			ibDoIt.setId( RA_VIEWID_ACTION );
			ibDoIt.setAdjustViewBounds(true);
			ibDoIt.setMaxHeight(72);
			ibDoIt.setMaxWidth(72);
			ibDoIt.setScaleType(ImageView.ScaleType.FIT_XY);
			ibDoIt.setPadding(1, 1, 1, 1);
			ibDoIt.setFocusable(false);
			
			tvSummary = new TextView(context);
			tvSummary.setId( RA_VIEWID_SUMMARY );
			tvSummary.setFocusable(false);
			
			llRightBar.setBackgroundColor(0x00000000);

			RelativeLayout.LayoutParams lp_tvName = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_tvNote = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_ibContactIcon = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_ibDoIt = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_tvSummary = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			RelativeLayout.LayoutParams lp_llSidebar = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			// Set up the relative positions
			lp_ibContactIcon.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			
			lp_tvName.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp_tvName.addRule(RelativeLayout.ALIGN_LEFT);
			lp_tvName.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvName.setMargins(5, 0, 0, 0);

			lp_tvNote.addRule(RelativeLayout.BELOW, tvName.getId());
			lp_tvNote.addRule(RelativeLayout.ALIGN_LEFT);
			lp_tvNote.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvNote.setMargins(5, 0, 0, 0);
			
			lp_tvSummary.addRule(RelativeLayout.BELOW, tvNote.getId());
			lp_tvSummary.addRule(RelativeLayout.RIGHT_OF, ib.getId());
			lp_tvSummary.setMargins(5, 0, 0, 0);
			
			lp_ibDoIt.addRule(RelativeLayout.BELOW, tvName.getId());
			lp_ibDoIt.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp_ibDoIt.setMargins(5, 5, 5, 5);
			
			lp_llSidebar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp_llSidebar.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lp_llSidebar.setMargins(0, 0, 0, 0);
			
			// Let's add them to the view!
			v.addView(ib, lp_ibContactIcon);
			v.addView(tvName, lp_tvName);
			v.addView(tvNote, lp_tvNote);
			v.addView(tvSummary, lp_tvSummary);
			
			llRightBar.addView(ibDoIt, lp_ibDoIt);
			
			v.addView(llRightBar, lp_llSidebar);

		} else {
			v = (RelativeLayout) oldView;

			ib =  (ImageButton) v.findViewById(RA_VIEWID_IMAGEBUTTON);
			tvName = (TextView) v.findViewById(RA_VIEWID_NAME);
			tvNote = (TextView) v.findViewById(RA_VIEWID_NOTE);
			ibDoIt = (ImageButton) v.findViewById(RA_VIEWID_ACTION);
			tvSummary = (TextView) v.findViewById(RA_VIEWID_SUMMARY);
			
		}
		Bitmap b = AndroidReminderUtils.loadContactPhoto(context, r.getContactID());
		boolean contactIconIsSmall = false;
		if (b != null) {
			ib.setImageBitmap(b);
			if(b.getWidth() < RA_CONTACTICON_MAXWIDTH || b.getHeight() < RA_CONTACTICON_MAXWIDTH) {
				contactIconIsSmall = true;
			}
		} else {
			int drawable_resource_id = R.drawable.ic_contact_picture;
			ib.setImageResource(drawable_resource_id);
			
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), drawable_resource_id, o);
			if( bmp.getWidth() < RA_CONTACTICON_MAXWIDTH || bmp.getHeight() < RA_CONTACTICON_MAXWIDTH) {
				contactIconIsSmall = true;
			}
		}
		
		if( contactIconIsSmall ) {
			ib.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			ib.setMaxHeight(RA_CONTACTICON_MAXWIDTH);
			ib.setMaxWidth(RA_CONTACTICON_MAXWIDTH);
		} else {
			ib.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		
		ibDoIt.setImageResource(R.drawable.ic_launcher_voicedial);
		String displayName = r.getDisplayName();
		if(displayName == null || displayName.equals("")) {
			displayName = "Unknown Contact";
		}

		tvName.setText(displayName);
		tvName.setShadowLayer(4.0f, 4.0f, 4.0f, 0xFF000000);
		// TODO change color based on whatever silliness the user wants.
		tvName.setTextColor(0xFFFFFFFF);
		tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PT, 12);
		tvNote.setText(r.getNote());
		tvNote.setTextColor(0xFFCCCCFF);
		tvSummary.setText("Due " + r.getDescrDue());
		// Most of the items in this ListView will simply DisplayReminder
		OnClickListener ocDisplay = new View.OnClickListener() {
        	public void onClick(View view) {
        		// Early exit if caller was a goofball and forgot to set tags.
        		// This is a bit of overkill for a simple onClick but I was experimenting with Java error trapping
        		// and detection of stupid callers (or stupid devs :P )
        		for(int requiredTagID : new int[] { R.string.TAG_ID_ReminderAdapter_Reminder, R.string.TAG_ID_ReminderAdapter_Context}) {
            		if( null == view.getTag(requiredTagID) ) {
            			// If we were certain there was a good context passed in, we could use the context to get the resources to
            			// get the string for this id, but we're not, so we can't.  You'll just have to make do with the ID:
            			Log.w(TAG, "Required tag '" + requiredTagID + "' not set!");
            			return;
            		}        			
        		}

        		Reminder r = (Reminder) view.getTag(R.string.TAG_ID_ReminderAdapter_Reminder);
        		final Context contextParent = (Context) view.getTag(R.string.TAG_ID_ReminderAdapter_Context);
				Intent intentDisplay = new Intent(context, DisplayReminder.class);
				Bundle b = new Bundle();
				b.putLong(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID, r.getID());  
				intentDisplay.putExtras(b);					
				contextParent.startActivity(intentDisplay);
        	}
        };

		OnClickListener ocCall = AndroidReminderUtils.genOnClickDoVoiceDial();

        // TODO There has got to be a better way:
		ib.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
		ib.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
		tvName.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
		tvName.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
		tvNote.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
		tvNote.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
		tvSummary.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
		tvSummary.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
		v.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
		v.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);
		ibDoIt.setTag(R.string.TAG_ID_ReminderAdapter_Reminder, r);
		ibDoIt.setTag(R.string.TAG_ID_ReminderAdapter_Context, context);

		
		ib.setOnClickListener(ocDisplay);
		tvName.setOnClickListener(ocDisplay);
		tvNote.setOnClickListener(ocDisplay);
		tvSummary.setOnClickListener(ocDisplay);
		v.setOnClickListener(ocDisplay);			
		ibDoIt.setOnClickListener(ocCall);
		
		return v;
	}
}
