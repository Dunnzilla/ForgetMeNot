package com.dunnzilla.mobile;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class EditReminder extends CreateReminder {
	static final int 			PICK_CONTACT = 1001;
    private static final String TAG = "EditReminder";
    
    @Override
	protected void mapDBFieldsToResourceIDs() {
        idMap = new HashMap<String,Integer>();    	
    	idMap.put(DBConst.f_DATETIME_START, R.id.cr_datepicker_start);
    	idMap.put(DBConst.f_DATETIME_STOP, R.id.cr_datepicker_stop);
    	idMap.put(DBConst.f_PERIOD, R.id.cr_period);
    	idMap.put(DBConst.f_NOTE, R.id.cr_note);
    	idMap.put("__contact_icon", R.id.cr_contact_icon);
    	idMap.put("__contact_name", R.id.cr_text_who);
    	idMap.put("__button_save_or_update", R.id.cr_save);
	}
    
    @Override
	public boolean validateSettings() {
		if( ! super.validateSettings()) {
			return false;
		}
		if( reminder.getID() <= 0 ) {
			setErrMessage("Invalid reminder ID.  Maybe you meant to create one?");
			return false;
		}

		return true;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reminderViewInit();

        Button bSave = (Button) findViewById( idMap.get("__button_save_or_update") );        
        bSave.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		if( EditReminder.this.validateSettings() ) {
        			EditReminder.this.saveReminder();
        			EditReminder.this.finish();
        		} else {
        			Toast.makeText(EditReminder.this, EditReminder.this.getErrMessage(), Toast.LENGTH_SHORT).show();
        		}
        	}
        });
        
        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
        	long idReminder = extras.getLong(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID);
        	Log.v(TAG, "Loading ID " + idReminder);
        	reminder = AndroidReminderUtils.loadReminderFromID(this, db, idReminder);
        }
		updateLayout();
	}
	
    public void saveReminder() {
    	db.open();
    	setReminderFromLayout();
    	
    	String dateRange = AndroidReminderUtils.formatDate(reminder.getDateStart()) + " - " + AndroidReminderUtils.formatDate(reminder.getDateStop());
    	Log.v(TAG, "Updating reminder #" + reminder.getID() + ", for " + reminder.getDisplayName() + ". Note: " + reminder.getNote() + ", period " + reminder.getPeriod() + ", daterange " + dateRange);
    	
    	Intent intent = getIntent();
    	db.update(reminder);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        db.close();
        finish();
    }
}
