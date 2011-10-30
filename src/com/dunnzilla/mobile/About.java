package com.dunnzilla.mobile;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
		
        if(Build.VERSION.SDK_INT < 11) {
    		setContentView(R.layout.about);            
        } else {
    		setContentView(R.layout.tablet_about);        	
        }
	
		try
		{
			androidManifest_versionName = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionName;
		}
		catch (NameNotFoundException e)
		{
		    Log.v(TAG, e.getMessage());    
		}
		TextView tvVersion = (TextView) findViewById(R.id.about_versionName);
		tvVersion.setText("Version " + androidManifest_versionName);
		
		//
		// LnH blog posts URL:
		// http://www.leapsnhounds.com/blog/?json=1
		// http://www.leapsnhounds.com/?json=get_recent_posts&count=3
		//
		// LnH Picasa Web Albums pics URL (JSON):
		// https://picasaweb.google.com/data/feed/base/user/thefirehydrant/albumid/5235966506291221969?alt=json&kind=photo&hl=en_US
		
		//RestClient.connect("https://picasaweb.google.com/data/feed/base/user/thefirehydrant/albumid/5235966506291221969?alt=json&kind=photo&hl=en_US");
		
		/*
		 * FMN logo based on WC image
		 * By Rude (Own work) [CC-BY-SA-3.0 (www.creativecommons.org/licenses/by-sa/3.0) or GFDL (www.gnu.org/copyleft/fdl.html)], via Wikimedia Commons
		 */
	}
}
