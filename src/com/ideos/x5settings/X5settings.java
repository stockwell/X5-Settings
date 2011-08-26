package com.ideos.x5settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ListView;
import android.app.ListActivity;
import android.content.Intent;

public class X5settings extends ListActivity {
    /** Called when the activity is first created. */
	Rootcommands method = new Rootcommands();
	boolean ROOT = Rootcommands.runRootCommand("echo root");
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
    	super.onCreateOptionsMenu(menu);
    	MenuItem item = menu.add("Exit");
    	item.setIcon(R.drawable.ic_menu_quit);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
	String[] settingsItems = {
			"Softkey Haptic Feedback",
			"Touchscreen Filter",
			"Internal/External Storage Selection",
			"Notification LED",
			"CPU Frequency and Governor",
			"I/O Scheduler",
			"Audio Settings",
			};  
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setListAdapter(new ArrayAdapter<String>(this,
   			 android.R.layout.simple_list_item_1, settingsItems));
        if (!ROOT){
    		Toast noroot = Toast.makeText(this, "Cannot obtain root access, is su installed?", 2000);
    		noroot.show();
    	}
    }

	protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Object o = this.getListAdapter().getItem(position);
    	String selection = o.toString();
    	Intent myIntent;
    	if (selection == "Softkey Haptic Feedback")
    		myIntent = new Intent(v.getContext(), Haptic.class);
    	else if (selection == "Touchscreen Filter") 	
    		myIntent = new Intent(v.getContext(), Touchscreen.class);
    	else if (selection ==  "Internal/External Storage Selection")
			myIntent = new Intent(v.getContext(), Vold.class);
    	else if (selection ==  "Notification LED")
			myIntent = new Intent(v.getContext(), Led.class);
    	else if (selection ==  "CPU Frequency and Governor")
			myIntent = new Intent(v.getContext(), Cpu.class);
    	else if (selection ==  "I/O Scheduler")
			myIntent = new Intent(v.getContext(), Io.class);
    	else if (selection ==  "Audio Settings")
			myIntent = new Intent(v.getContext(), Sound.class);
    	else myIntent = null;
    	if(ROOT)startActivity(myIntent); 
    }        
}
	