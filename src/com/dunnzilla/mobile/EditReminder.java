package com.dunnzilla.mobile;

import android.os.Bundle;

public class EditReminder extends DisplayReminder {
	static final int 			PICK_CONTACT = 1001;
    private static final String TAG = "EditReminder";
    
    private DBReminder		db;
    private String			errorMessage;
    Reminder				reminder;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_under_construction);
    	

        return;
        /*        
    	db = new DBReminder(this);
    	db.open();
    
        Bundle extras = this.getIntent().getExtras();
        if(extras != null) {
        	long idReminder = extras.getLong(DisplayReminder.INTENT_EXTRAS_KEY_REMINDER_ID);
        	Log.v(TAG, "Loading ID " + idReminder);
            loadReminderFromID(idReminder);
        }
        // TODO Probably need to shuffle pieces around before 
        
        View.OnClickListener vocl_pickContact = new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        		startActivityForResult(i, PICK_CONTACT);
        	}
    	}; 

        ImageButton ContactIcon  = (ImageButton) findViewById(R.id.cr_contact_icon);
        TextView tvContactName = (TextView) findViewById(R.id.cr_text_who);
        Button bSave = (Button) findViewById(R.id.cr_save);
        
        ContactIcon.setOnClickListener( vocl_pickContact );
        tvContactName.setOnClickListener( vocl_pickContact );
        bSave.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		if( EditReminder.this.validateSettings() ) {
        			EditReminder.this.saveReminder();  // TODO Possibly move into a smarter class when adding the Edit ability (v0.6?)
        			EditReminder.this.finish();
        		} else {
        			Toast.makeText(EditReminder.this, EditReminder.this.getErrMessage(), Toast.LENGTH_SHORT).show();
        		}
        	}
        });
        */
	}

}
