/**
 * 
 */
package com.dunnzilla.mobile;


import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Jason Dunn <attnjd@gmail.com>
 *
 */
public class FMNPreferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
