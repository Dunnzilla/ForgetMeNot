package com.dunnzilla.mobile;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.util.Log;

public class ReminderService extends Service {
	private static final String TAG = "Service";
	public static final String INTENT_EXTRAS_KEY_REMINDER_ID = "REMINDER_ID";
	
	private NotificationManager nm;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			notifyFromHandler(msg);
		}
	};
	private Timer 				timer;
	private ArrayList<Reminder> reminders;
	private DBReminder			db;

	private TimerTask timerTask = new TimerTask() {
		public void run() {
			repopulate();

			Log.v(TAG, "Checking for reminders . . . ");
			if( ! reminders.isEmpty() ) {
				Log.v(TAG, "Found " + reminders.size() + " reminders ready to create notifications.");
				for(Reminder r : reminders) {
					sendNotification(r);					
				}
			}
			else {
				Log.v(TAG, "No notifications necessary.");
			}
		}
	};

	private void repopulate() {
        if(reminders == null) {
        	reminders = new ArrayList<Reminder>();
        } else {
        	reminders.clear();
        }
		Cursor cu = db.selectDue();
		if (cu.moveToFirst()) {
			do {
				reminders.add( new Reminder(cu) );
			} while (cu.moveToNext());
		}
		cu.close();
	}
	private void notifyFromHandler(Message msg) {
		Bundle	msgData = msg.getData();		
		long	reminderID = ((Long)msgData.get(INTENT_EXTRAS_KEY_REMINDER_ID)).longValue();
		Reminder r = new Reminder(db, reminderID); 
		//String strReminderID = Long.toString(reminderID); 
		Uri uri = Uri.parse("reminder://com.dunnzilla.mobile/view?id=" + r.getID());
		
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		Bundle b = new Bundle();
		b.putLong(INTENT_EXTRAS_KEY_REMINDER_ID, r.getID());  
		intent.putExtras(b);		
		PendingIntent pendintent = PendingIntent.getActivity(this, Intent.FLAG_ACTIVITY_NEW_TASK, intent, PendingIntent.FLAG_ONE_SHOT);

		long id = r.getContactID();
		// Form an array specifying which columns to return. 
		String[] projection = new String[] {
									Data._ID,
		                             ContactsContract.Contacts.DISPLAY_NAME
		                          };
		Cursor cu = getContentResolver().query(Data.CONTENT_URI,
		          projection,
		          Data.CONTACT_ID + "=?",
		          new String[] {String.valueOf(id)}, null);

		String displayName = "";
	    if( cu.moveToFirst()) {
	        do {
				// TODO Make more betters	        	
	        	displayName = ", " + cu.getString(cu.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
	       }  while(cu.moveToNext());
	    }
	    cu.close();
		
		String notificationText = "Remember " + displayName;
		String note = r.getNote();
		// TODO write or import some decent string utils
	    if( note != null && note.length() > 0) {
	    	notificationText = notificationText.concat(" - " + note);
	    }
		
		// Why 'final' here, in the middle of a function?
		final Notification n = new Notification(R.drawable.notify_icon_logo_24, notificationText, System.currentTimeMillis());

		// TODO get the contentTitle from strings
		CharSequence contentTitle = "Forget Me Not";
		n.setLatestEventInfo(getApplicationContext(), contentTitle, notificationText, pendintent);
		nm.notify(r.getID(), n);
	}
	
	/**
	 *  TODO this confuses me.  The book, Android in Action, has us call:
	 *  sendNotification() -> handler.sendMessage() -> handler.handleMessage()
	 *  -> notifyFromHandler(), which finally just creates a notification.  :|
	 * 
	 */
	private void sendNotification(Reminder r) {
		long reminderID = r.getID();
		Message m = Message.obtain();
		Bundle b = new Bundle();
		b.putLong(INTENT_EXTRAS_KEY_REMINDER_ID, reminderID);
		Log.v(TAG, "sendNotification(" + reminderID + ")");
		m.setData(b);
		handler.sendMessage(m);
	}

	@Override
	public void onCreate() {
		super.onCreate();
        db = new DBReminder(this);
 
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		timer = new Timer();
		// TODO Check application preferences for setting the timer delay
		Log.v(TAG, "Starting service");
		timer.schedule(timerTask, 10000, 10000);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
