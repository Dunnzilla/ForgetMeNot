package com.dunnzilla.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent i) {
		if( i.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.v("ReminderServiceReceiver", "Boot completed.  Starting ReminderService.");
			Intent rsIntent = new Intent(context, ReminderService.class);
			context.startService(rsIntent);
		}
	}
}
