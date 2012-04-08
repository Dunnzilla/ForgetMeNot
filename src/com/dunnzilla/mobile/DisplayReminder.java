package com.dunnzilla.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayReminder extends Activity {
    private static final String TAG = "DisplayReminder";
    
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
        	long idReminder = extras.getLong(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID);
        	Log.v(TAG, "Loading ID " + idReminder);
        	reminder = AndroidReminderUtils.loadReminderFromID(this, db, idReminder);
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
        
        Button bEdit = (Button) findViewById(R.id.disprem_btn_edit);
        bEdit.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
				Bundle b = new Bundle();
				b.putLong(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID, reminder.getID());

				Intent intentEdit = new Intent(DisplayReminder.this, EditReminder.class);
				intentEdit.putExtras(b);					
				startActivity(intentEdit);
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
    
    @Override
    public void onResume()
    {
        super.onResume();
        updateLayout(this.getIntent());
    }

    protected void updateLayout(Intent _intent) {
    	if( reminder == null) {
    		return;
    	}
    	if( ! reminder.valid() ) {
    		// TODO Do something to visually indicate the contact chosen is invalid, or is pending selection
    		return;
    	}

    	reminder = AndroidReminderUtils.loadReminderFromID(this, db, reminder.getID());

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
    	
    	TextView tvPeriod = (TextView) findViewById(R.id.vr_text_who_summary);
    	tvPeriod.setText("Contact " + reminder.getDescrPeriod());
    	
    	TextView tvDateRange = (TextView) findViewById(R.id.vr_date_range);
    	Log.v(TAG, "Using Android utils to format dates from " + reminder.getDateStart() + " to " + reminder.getDateStop());
    	String dateRange = AndroidReminderUtils.formatDate(reminder.getDateStart()) + " - " + AndroidReminderUtils.formatDate(reminder.getDateStop());
    	tvDateRange.setText(dateRange);
    	
    }
}
