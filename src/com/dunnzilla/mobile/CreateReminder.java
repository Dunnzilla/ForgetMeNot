package com.dunnzilla.mobile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    
    public Reminder						reminder;
    protected DBReminder				db;
    protected String					errorMessage;
	protected Map<String,Integer>		idMap;

	protected void mapDBFieldsToResourceIDs() {
        idMap = new HashMap<String,Integer>(20);    	
    	idMap.put(DBConst.f_DATETIME_START, R.id.cr_datepicker_start);
    	idMap.put(DBConst.f_DATETIME_STOP, R.id.cr_datepicker_stop);
    	idMap.put(DBConst.f_PERIOD, R.id.cr_period);
    	idMap.put(DBConst.f_NOTE, R.id.cr_note);
    	idMap.put("__contact_icon", R.id.cr_contact_icon);
    	idMap.put("__contact_name", R.id.cr_text_who);
    	idMap.put("__button_save_or_update", R.id.cr_save);
    	idMap.put("__layout", R.layout.create_reminder);
	}

	protected void reminderViewInit() {		 
        mapDBFieldsToResourceIDs();
        Integer i = idMap.get("__layout");
        int lid;
        // Default to the create_reminder layout if no __layout key is specified in the ID map.
        // This way you can use a different layout if you wish.
        if(i == null) {
        	lid = R.layout.create_reminder;
        } else {
        	lid = i.intValue();
        }
        setContentView( lid );
        
    	db = new DBReminder(this);
    	reminder = new Reminder();

        View.OnClickListener vocl_pickContact = new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		startActivityForResult(i, PICK_CONTACT);
        	}
    	};    	

        ImageButton ContactIcon  = (ImageButton) findViewById( idMap.get("__contact_icon") );
        TextView tvContactName = (TextView) findViewById( idMap.get("__contact_name") );
        
        ContactIcon.setOnClickListener( vocl_pickContact );
        tvContactName.setOnClickListener( vocl_pickContact );
        db.close();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        reminderViewInit();

        Button bSave = (Button) findViewById( idMap.get("__button_save_or_update") );
        bSave.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		if( CreateReminder.this.validateSettings() ) {
        			CreateReminder.this.saveReminder();
        			CreateReminder.this.finish();
        		} else {
        			Toast.makeText(CreateReminder.this, CreateReminder.this.getErrMessage(), Toast.LENGTH_SHORT).show();
        		}
        	}
        });
	}
	
	public boolean validateSettings() {
		if( reminder.getContactID() <= 0) {
			setErrMessage("Choose a contact.");
			return false;
		}
		return true;
	}
	
	public String getErrMessage() {
		return errorMessage;
	}
	protected void setErrMessage(String e) {
		errorMessage = e;
	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if ( resultCode == RESULT_OK ) {
	    	switch(requestCode) {
	    	case PICK_CONTACT:
	    		AndroidReminderUtils.getContactInfo(this, reminder, intent);
	    		updateLayout();
	       		break;
	    	}
		}
    	super.onActivityResult(requestCode, resultCode, intent);
    }

    public void setReminderFromLayout()
    {
    	// TODO also set the contact?
    	DatePicker dp = (DatePicker)findViewById( idMap.get(DBConst.f_DATETIME_START) );
    	Date dateStart = new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth());
    	DatePicker dpStop = (DatePicker)findViewById( idMap.get(DBConst.f_DATETIME_STOP) );
    	Date dateStop = new Date(dpStop.getYear() - 1900, dpStop.getMonth(), dpStop.getDayOfMonth());
    	  	
    	TextView tvPeriod = (TextView) findViewById( idMap.get(DBConst.f_PERIOD) );
    	TextView tvNote = (TextView) findViewById( idMap.get(DBConst.f_NOTE) );
    	
    	String sPeriod = tvPeriod.getText().toString().trim();
    	int i = Integer.parseInt(sPeriod);
  
    	reminder.setPeriod(i);
    	reminder.setNote(tvNote.getText().toString().trim());
    	reminder.setDateStart(dateStart);
    	reminder.setDateNext(dateStart);//TODO get the actual next date
    	reminder.setDateStop(dateStop);
    }

    public void saveReminder() {
    	setReminderFromLayout();
    	Log.v(TAG, "New reminder for " + reminder.getDisplayName() + ". Note: " + reminder.getNote());
    	
    	Intent intent = getIntent();
    	db.insert(reminder);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }        
        finish();
    }

    protected void updateLayout() {
    	if( ! reminder.valid() ) {
    		// TODO Do something to visually indicate the contact chosen is invalid, or is pending selection
    		return;
    	}
		ImageView ivContactIcon = (ImageView) findViewById( idMap.get("__contact_icon") );
		if( reminder.getContactIconBitmap() != null) {
			ivContactIcon.setImageBitmap(reminder.getContactIconBitmap());
    	}

		String displayName = reminder.getDisplayName();
		TextView tvName = (TextView) findViewById( idMap.get("__contact_name") );
    	if( displayName != null && displayName.length() > 0 ) {
    		tvName.setText(displayName);
    		tvName.setTextColor(0xFFFFFFFF);
    	} else {
    		tvName.setText(R.string.vr_default_name);
    		tvName.setTextColor(0xFFFFFFFF);    		
    	}

    	String note = reminder.getNote();
    	if(note != null && note.length() > 0) {
    		TextView tvNote = (TextView) findViewById( idMap.get(DBConst.f_NOTE) );
    		tvNote.setText(note);
    	}
    	
    	DatePicker dpStart = (DatePicker) findViewById( idMap.get(DBConst.f_DATETIME_START) );
    	Date d = reminder.getDateStart();
    	dpStart.updateDate(1900 + d.getYear(), d.getMonth(), d.getDate());
    	
    	DatePicker dpStop = (DatePicker) findViewById( idMap.get(DBConst.f_DATETIME_STOP) );
    	d = reminder.getDateStop();
    	dpStop.updateDate(1900 + d.getYear(), d.getMonth(), d.getDate());
    	
    	TextView tvPeriod = (TextView) findViewById( idMap.get(DBConst.f_PERIOD) );
    	tvPeriod.setText(Integer.toString(reminder.getPeriod()));

    }
}
