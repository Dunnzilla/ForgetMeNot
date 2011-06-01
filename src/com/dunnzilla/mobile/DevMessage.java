package com.dunnzilla.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class DevMessage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devmessage);
		
		Button b = (Button) this.findViewById(R.id.btn_devmessage_okay);
		b.setOnClickListener( new OnClickListener() {
				public void onClick(View v) {
					
				}
			}
		);
	}
}