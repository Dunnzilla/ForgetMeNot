package com.dunnzilla.mobile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

public class ReminderService extends Service {
	private static final String TAG = "Service";
	
	private NotificationManager nm;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			notifyFromHandler(msg);
		}
	};
	private Timer timer;
	private ArrayList<Reminder> reminders;
	private DB					db;

	private TimerTask timerTask = new TimerTask() {
		public void run() {
			repopulate();
			if( ! reminders.isEmpty() ) {
				for(Reminder r : reminders) {
					sendNotification(r.getDisplayName(), r.getID());					
				}
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
				Reminder r = new Reminder(cu);
		    	Uri uriPerson = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, r.getContactID());
				r.setContactID(cu.getInt(cu.getColumnIndex(ContactsContract.Contacts._ID)));
				try {
					r.setDisplayName(cu.getString(cu.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
				}
				catch(Exception e) {
					Log.v(TAG, "Goodness");
				}

				InputStream streamPhoto = ContactsContract.Contacts
						.openContactPhotoInputStream(getContentResolver(),
								uriPerson);
				if (streamPhoto != null) {
					r.setContactIconBitmap(BitmapFactory
							.decodeStream(streamPhoto));
				}
				reminders.add(r);
			} while (cu.moveToNext());
		}
		cu.close();
	}
	private void notifyFromHandler(Message msg) {
		Bundle msgData = msg.getData();
		long	contactID = ((Long)msgData.get("ID_CONTACT")).longValue();
		String strContactID = Long.toString(contactID); 
		String strSomeMessage = (String)msgData.get("SOME_MESSAGE");
		Uri uri = Uri.parse("reminder://com.dunnzilla.mobile/view?id=" + strContactID);
		String notificationText = strSomeMessage + ", " + strContactID;
		
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		PendingIntent pi = PendingIntent.getActivity(this, Intent.FLAG_ACTIVITY_NEW_TASK, intent, PendingIntent.FLAG_ONE_SHOT);
		
		final Notification n = new Notification(R.drawable.notify_icon_logo_24, strSomeMessage, System.currentTimeMillis());

		Context context = getApplicationContext();
		CharSequence contentTitle = "Forget Me Not";
		n.setLatestEventInfo(context, contentTitle, notificationText, pi);
		nm.notify(Integer.parseInt(strContactID), n);
	}
	
	/**
	 *  TODO this confuses me.  The book, Android in Action, has us call:
	 *  sendNotification() -> handler.sendMessage() -> handler.handleMessage()
	 *  -> notifyFromHandler(), which finally just creates a notification.  :|
	 * 
	 */
	private void sendNotification(String notificationMessage, long contactID) {
		Message m = Message.obtain();
		Bundle b = new Bundle();
		b.putString("SOME_MESSAGE", notificationMessage);
		b.putLong("ID_CONTACT", contactID);
		m.setData(b);
		handler.sendMessage(m);
	}
	@Override
	public void onCreate() {
		super.onCreate();
        db = new DB(this);
 
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		timer = new Timer();
		// TODO Check application preferences for setting the timer delay
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
