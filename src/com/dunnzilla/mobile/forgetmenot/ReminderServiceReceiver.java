package com.dunnzilla.mobile.forgetmenot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent i) {
		if( i.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent rsIntent = new Intent(context, ReminderService.class);
			context.startService(rsIntent);
		}
	}
}
