package com.dunnzilla.mobile;

import java.util.Timer;
import java.util.TimerTask;

//import android.app.NotificationManager;
//import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class ReminderService extends Service {
	
	private NotificationManager nm;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			notifyFromHandler(msg);
		}
	};
	private Timer timer;

	private TimerTask timerTask = new TimerTask() {
		public void run() {
			// TODO something useful, like lookup people who are due for a call
			sendNotification("Somebody!!", 1);
		}
	};

	private void notifyFromHandler(Message msg) {
		Bundle msgData = msg.getData();
		long	contactID = ((Long)msgData.get("ID_CONTACT")).longValue();
		String strContactID = Long.toString(contactID); 
		String strSomeMessage = (String)msgData.get("SOME_MESSAGE");
		Uri uri = Uri.parse("reminder://com.dunnzilla.mobile/view?id=" + strContactID);
		String notificationText = strSomeMessage + ", " + strContactID;
		
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		PendingIntent pi = PendingIntent.getActivity(this, Intent.FLAG_ACTIVITY_NEW_TASK, intent, PendingIntent.FLAG_ONE_SHOT);
		
		final Notification n = new Notification(R.drawable.notify_icon_logo_24, "Forget me not!", System.currentTimeMillis());

		Context context = getApplicationContext();
		CharSequence contentTitle = "Forget Me Not";
		n.setLatestEventInfo(context, contentTitle, notificationText, pi);
		nm.notify(1, n);	// TODO wtf
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
