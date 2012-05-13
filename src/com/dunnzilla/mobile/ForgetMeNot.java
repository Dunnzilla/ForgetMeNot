package com.dunnzilla.mobile;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;


public class ForgetMeNot extends ListActivity {
	private static final int MENU_ITEM_SETTINGS = 1, MENU_ITEM_ABOUT = 2, MENU_ITEM_MESSAGE = 3;
	private static final int MENU_GROUP_DEFAULT = 1;
	private static final int CREATE_REMINDER = 1001;
	private static final int PREFS_UPDATED = 1002;
	//private static final String TAG = "ForgetMeNot";

	private ArrayList<Reminder> reminders;
	private DBReminder			db;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        repopulate();


        boolean startReminderService = true;
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo s : manager.getRunningServices(Integer.MAX_VALUE)) {
	    	// TODO SPA-10 Determine service name from manifest or reflection. Don't hardcode.
	        if ( "com.dunnzilla.mobile.ReminderService".equals(s.service.getClassName())) {
	        	startReminderService = false;
	        }
	    }

	    if( startReminderService ) {
			Intent rsIntent = new Intent(getApplicationContext(), ReminderService.class);
			getApplicationContext().startService(rsIntent);        	
        }

        Button bCreate = (Button) findViewById(R.id.btn_create_fmn);
        bCreate.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		start_CreateReminder();
        	}
        });
    }

	@Override
    public void onResume() {
        super.onResume();
        repopulate();
	}
	private void repopulate() {
		db = new DBReminder(ForgetMeNot.this);
        
        if(ForgetMeNot.this.reminders == null) {
        	ForgetMeNot.this.reminders = new ArrayList<Reminder>();
        } else {
        	ForgetMeNot.this.reminders.clear();
        }
        
		Cursor cu = db.selectAllActiveByDue();
		startManagingCursor(cu);
		if(cu.moveToFirst()) {
			do {
				Reminder r = new Reminder(cu);
				
				// We don't store contact name, number or picture, as those could change.
				// Instead, we store the ID and update the contact info at runtime based
				// on whatever is in the address book.
				// (There may be flaws in this method, but I'm new to Android so this
				// seems like a decent way to decouple the Android data from the Reminder data)
				
				r.updateFromContactsContract(this);
				reminders.add(r);
			} while(cu.moveToNext());
		}
		cu.close();
    	getListView().setCacheColorHint(0);  // This prevents fancy gradient listviews from going black when scrolling
    	
        ListAdapter adapter;
    	adapter = new ReminderAdapter(this, reminders);
        setListAdapter(adapter);
        registerForContextMenu( getListView() );
        db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_GROUP_DEFAULT, MENU_ITEM_SETTINGS, 0, "Settings");
		menu.add(MENU_GROUP_DEFAULT, MENU_ITEM_ABOUT, 0, "About");
		//menu.add(MENU_GROUP_DEFAULT, MENU_ITEM_MESSAGE, 0, "Random");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Do stuff");
	    String[] menuItems = getResources().getStringArray(R.array.contextmenu_reminder_options);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	    long id = getListAdapter().getItemId(info.position);
	    // TODO whatver w/ the id
	}


	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	switch(requestCode) {
	    	case CREATE_REMINDER:
	    		if ( resultCode == RESULT_OK ) {
	    			ForgetMeNot.this.repopulate();
	    		}
	       		break;
	    	case PREFS_UPDATED:
				Intent rsIntent = new Intent(getApplicationContext(), ReminderService.class);
				getApplicationContext().stopService(rsIntent);
				getApplicationContext().startService(rsIntent);
	    		break;
		}
    	super.onActivityResult(requestCode, resultCode, intent);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case MENU_ITEM_SETTINGS:
			start_Prefs();
	        return true;
		case MENU_ITEM_ABOUT:
			start_About();
			return true;
		case MENU_ITEM_MESSAGE:
			start_DevMessage();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// onAttachedToWindow() code courtesy of a June 4th, 2010 article by Eric Burke
	// http://stuffthathappens.com/blog/2010/06/04/android-color-banding/ 
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		// Eliminates color banding
		window.setFormat(PixelFormat.RGBA_8888);
	}
	  
	private void start_DevMessage() {
		Intent i = new Intent(this, DevMessage.class);
		startActivity(i);
	}
    private void start_CreateReminder() {
    	Intent i = new Intent(this, CreateReminder.class);
        startActivityForResult(i, CREATE_REMINDER);
    }
    private void start_Prefs() {
    	Intent i = new Intent(this, Prefs.class);
    	startActivityForResult(i, PREFS_UPDATED);
    }
    private void start_About() {
    	Intent i = new Intent(this, About.class);
        startActivity(i);
    }
}
