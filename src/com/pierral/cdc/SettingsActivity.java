package com.pierral.cdc;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	@TargetApi(14)
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);
        

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        	ActionBar actionBar = getActionBar();
        	actionBar.setDisplayHomeAsUpEnabled(true);
        }
	}


   	public boolean onOptionsItemSelected(MenuItem item) {
   	    // Handle item selection
   	    switch (item.getItemId()) {
   	    case android.R.id.home:
   	    	onBackPressed();
   	    default:
   	        return super.onOptionsItemSelected(item);
   	    }
   	}
}
