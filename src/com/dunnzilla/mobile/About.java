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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author Jason Dunn <attnjd@gmail.com>
 *
 */
public class About extends Activity {

	String androidManifest_versionName = "0.0.0.0";
	private static final String TAG = "About";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		try
		{
			androidManifest_versionName = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionName;
		}
		catch (NameNotFoundException e)
		{
		    Log.v(TAG, e.getMessage());    
		}
		TextView tvVersion = (TextView) findViewById(R.id.about_versionName);
//		ImageView ivLogo = (ImageView) findViewById(R.drawable.logo);
		tvVersion.setText("Version " + androidManifest_versionName);
		/*
		 * FMN logo based on WC image
		 * By Rude (Own work) [CC-BY-SA-3.0 (www.creativecommons.org/licenses/by-sa/3.0) or GFDL (www.gnu.org/copyleft/fdl.html)], via Wikimedia Commons
		 */
	}
}
