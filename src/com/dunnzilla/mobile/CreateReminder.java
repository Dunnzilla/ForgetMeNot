package com.dunnzilla.mobile;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateReminder extends Activity {
	static final int 			PICK_CONTACT = 1001;
    private static final String TAG = "CreateReminder";
    
    private DBReminder		db;
    private String			errorMessage;
    Reminder				reminder;

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
    	
    	db = new DBReminder(this);
    	db.open();
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
		return errorMessage;
	}
	private void setErrMessage(String e) {
		errorMessage = e;
	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if ( resultCode == RESULT_OK ) {
	    	switch(requestCode) {
	    	case PICK_CONTACT:
	    		AndroidReminderUtils.getContactInfo(this, reminder, intent);
	    		updateLayout(intent);
	       		break;
	    	}
		}
    	super.onActivityResult(requestCode, resultCode, intent);
    }

    public void saveReminder() {
    	DatePicker dp = (DatePicker)findViewById(R.id.cr_datepicker_start);
    	Date dateStart = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
    	DatePicker dpStop = (DatePicker)findViewById(R.id.cr_datepicker_stop);
    	Date dateStop = new Date(dpStop.getYear() - 1900, dpStop.getMonth(), dpStop.getDayOfMonth());
    	
    	TextView tvPeriod = (TextView) findViewById(R.id.cr_period);
    	TextView tvNote = (TextView) findViewById(R.id.cr_note);
    	
    	String sPeriod = tvPeriod.getText().toString().trim();
    	int i = Integer.parseInt(sPeriod);
    	CreateReminder.this.reminder.setPeriod(i);

    	CreateReminder.this.reminder.setNote(tvNote.getText().toString().trim());

    	CreateReminder.this.reminder.setDateStart(dateStart);
    	CreateReminder.this.reminder.setDateNext(dateStart);
    	CreateReminder.this.reminder.setDateStop(dateStop);
    	
    	Log.v(TAG, "New reminder for " + CreateReminder.this.reminder.getDisplayName() + ". Note: " + tvNote.getText().toString());
    	
    	Intent intent = this.getIntent();
    	CreateReminder.this.db.insert(reminder);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }        
        finish();
    }
    protected void updateLayout(Intent _intent) {
    	if( ! reminder.valid() ) {
    		// TODO Do something to visually indicate the contact chosen is invalid, or is pending selection
    		return;
    	}
		ImageView ivContactIcon = (ImageView) findViewById(R.id.cr_contact_icon);
		if( reminder.getContactIconBitmap() != null) {
			ivContactIcon.setImageBitmap(reminder.getContactIconBitmap());
    	}

		String displayName = reminder.getDisplayName();
		TextView tvName = (TextView) findViewById(R.id.cr_text_who);
    	if( displayName != null && displayName.length() > 0 ) {
    		tvName.setText(displayName);
    		tvName.setTextColor(0xFFFFFFFF);
    	} else {
    		tvName.setText(R.string.vr_default_name);
    		tvName.setTextColor(0xFFFFFFFF);    		
    	}
    }
}
