package com.dunnzilla.mobile;


import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;

public class ForgetMeNot extends Activity {
	private static final int MENU_ITEM_SETTINGS = 1, MENU_ITEM_ABOUT = 2;
	private static final int MENU_GROUP_DEFAULT = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


//		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);//
//		startActivityForResult(i, PICK_CONTACT;

        Button bCreate = (Button) findViewById(R.id.btn_create_fmn);
        bCreate.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View view) {
        		start_CreateReminder();
        	}
        });
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_GROUP_DEFAULT, MENU_ITEM_SETTINGS, 0, "Settings");
		menu.add(MENU_GROUP_DEFAULT, MENU_ITEM_ABOUT, 0, "About");
		return super.onCreateOptionsMenu(menu);
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
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
    private void start_CreateReminder() {
    	Intent i = new Intent(this, CreateReminder.class);
        startActivity(i);
    }
    private void start_Prefs() {
    	Intent i = new Intent(this, Prefs.class);
        startActivity(i);
    }
    private void start_About() {
    	Intent i = new Intent(this, About.class);
        startActivity(i);
    }
}