package com.dunnzilla.mobile.forgetmenot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;

public class ReminderService extends Service {
	private static final String TAG = "Service";
	
	private NotificationManager nm;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			notifyFromHandler(msg);
		}
	};
	private Timer 				timer;
	private ArrayList<Reminder> reminders;
	private DBReminder			db;
	private long 				timerDelay_ms;

	private TimerTask timerTask = new TimerTask() {
		public void run() {
			repopulate();

			if( reminders != null && ! reminders.isEmpty() ) {
				// Only show 1 notification.  It will either be "Contact Dave!" or "Multiple reminders."
				if( reminders.size() > 1 ) {
					nm.cancelAll();
					sendNotification(reminders.size());
				} else {
					for(Reminder r : reminders) {
						sendNotification(r);					
					}
				}
			}
		}
	};

	private void repopulate() {
		try {
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
		} catch(Exception e) {
			//Log.e(TAG, e.getMessage());
		}
	}

	private void notifyFromHandler(Message msg) {
		Bundle	msgData = msg.getData();
		// If this was a "Multiple people to remember!" notification, just go straight to the main activity
		int reminderType = AndroidReminderUtils.REMINDER_NOTIFICATION_TYPE_SINGLE;
		if(msgData.containsKey(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_TYPE)) {
			reminderType = msgData.getInt(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_TYPE);
		}

		Bundle b = new Bundle();
		Intent intent;

		String notificationText;
		int notificationID = 0;
		int notificationImageResource = R.drawable.notify_icon_logo_24;

		switch(reminderType) {
		case AndroidReminderUtils.REMINDER_NOTIFICATION_TYPE_MULTIPLE:
			// TODO use different image for multiple reminders
			notificationText = "Multiple Reminders"; 
		    intent = new Intent("android.intent.action.MAIN");
		    intent.setComponent(ComponentName.unflattenFromString("com.dunnzilla.mobile.forgetmenot/com.dunnzilla.mobile.forgetmenot.ForgetMeNot"));
		    intent.addCategory("android.intent.category.LAUNCHER");
			break;
		case AndroidReminderUtils.REMINDER_NOTIFICATION_TYPE_SINGLE:
		default:
			long reminderID = ((Long)msgData.get(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID)).longValue();
			Reminder r = new Reminder(db, reminderID); 
			// String strReminderID = Long.toString(reminderID); 
			Uri uri = Uri.parse("reminder://com.dunnzilla.mobile.forgetmenot/view?id=" + r.getID());
			b.putLong(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID, r.getID());  
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

			String displayName = null;
		    if( cu.moveToFirst()) {
		        displayName = cu.getString(cu.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
		    }
		    cu.close();
			
		    if(displayName == null) {
		    	return;
		    }
			notificationText = "Remember " + displayName;
			String note = r.getNote();
		    if( note != null && note.length() > 0) {
		    	notificationText = notificationText.concat(" - " + note);
		    }
		    notificationID = r.getID();
		    intent = new Intent(Intent.ACTION_VIEW, uri);
			break;
		}
		
		intent.putExtras(b);		
		PendingIntent pendintent = PendingIntent.getActivity(this, Intent.FLAG_ACTIVITY_NEW_TASK, intent, PendingIntent.FLAG_ONE_SHOT);
		
		// Why 'final' here, in the middle of a function?
		final Notification n = new Notification(notificationImageResource, notificationText, System.currentTimeMillis());

		n.flags |= Notification.FLAG_AUTO_CANCEL;
		CharSequence contentTitle = getResources().getString(R.string.app_name);
		n.setLatestEventInfo(getApplicationContext(), contentTitle, notificationText, pendintent);
		nm.notify(notificationID, n);
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
		b.putLong(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_ID, reminderID);
		b.putInt(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_TYPE, AndroidReminderUtils.REMINDER_NOTIFICATION_TYPE_SINGLE);
		m.setData(b);
		handler.sendMessage(m);	
	}
	// Calling with 
	private void sendNotification(int countOfRemindersWithNotifications) {
		Message m = Message.obtain();
		Bundle b = new Bundle();
		b.putInt(AndroidReminderUtils.INTENT_EXTRAS_KEY_REMINDER_TYPE, AndroidReminderUtils.REMINDER_NOTIFICATION_TYPE_MULTIPLE);
		m.setData(b);
		handler.sendMessage(m);		
	}

	private void setDefaults() {
		timerDelay_ms = 3600000;
	}
	@Override
	public void onCreate() {
		super.onCreate();		
        db = new DBReminder(this);
        
        setDefaults();
        checkPrefs();
 
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		timer = new Timer();
		timer.schedule(timerTask, 2000, timerDelay_ms);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

    private void checkPrefs() {
        SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
        String timerDelay = prefs.getString("pref_notify_delay", "3600");
        Long L = new Long(timerDelay);
        timerDelay_ms = L.longValue() * 1000;
    }
}