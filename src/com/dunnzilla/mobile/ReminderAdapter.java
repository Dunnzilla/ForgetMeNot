package com.dunnzilla.mobile;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.widget.LinearLayout.LayoutParams;

public class ReminderAdapter extends BaseAdapter implements ListAdapter {

	private ArrayList<Reminder> reminders;
	private Context				context;
	TextView					tvName;
	TextView					tvNote;

	public ReminderAdapter(Context _context, ArrayList<Reminder> _reminders) {
		reminders = _reminders;
		context = _context;
	}

	@Override
	public int getCount() {
		if(reminders == null)
			return 0;

		return reminders.size();
	}

	@Override
	public boolean isEmpty() {
		if(reminders == null)
			return true;

		if(reminders.size() <= 0)
			return true;
		
		return false;
	}

	@Override
	public Object getItem(int arg0) {
		return reminders.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return reminders.get(arg0).getID();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int arg0, View oldView, ViewGroup vgParent) {
		LinearLayout v;
		TextView tvName, tvNote;
		if (oldView == null) {
			v = new LinearLayout(context);
			tvName = new TextView(context);
			tvNote = new TextView(context);
			v.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			v.addView(tvName,lp);
			v.addView(tvNote,lp);
			String rname = reminders.get(arg0).getDisplayName();
			String rnote = reminders.get(arg0).getNote();
			tvName.setText(rname);
			tvNote.setText(rnote);
		} else {
			v = (LinearLayout) oldView;

		}		
		return v;
	}
}
