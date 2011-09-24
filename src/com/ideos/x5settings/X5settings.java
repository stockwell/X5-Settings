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
		MenuItem item = menu.add(R.string.exit);
		item.setIcon(R.drawable.ic_menu_quit);
		item = menu.add(R.string.app_name);
		item.setIcon(R.drawable.ic_menu_about);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == getText(R.string.app_name)) {
			startActivity(new Intent(this.getApplicationContext(), About.class));
		} else
			finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String[] settingsItems = getResources().getStringArray(
				R.array.menuItems_array);
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, settingsItems));

		if (!ROOT) {
			Toast noroot = Toast.makeText(this,
					"Cannot obtain root access, is su installed?", 2000);
			noroot.show();
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent myIntent;
		if (position == 0)
			myIntent = new Intent(v.getContext(), Haptic.class);
		else if (position == 1)
			myIntent = new Intent(v.getContext(), Touchscreen.class);
		else if (position == 2)
			myIntent = new Intent(v.getContext(), Vold.class);
		else if (position == 3)
			myIntent = new Intent(v.getContext(), Led.class);
		else if (position == 4)
			myIntent = new Intent(v.getContext(), Cpu.class);
		else if (position == 5)
			myIntent = new Intent(v.getContext(), Io.class);
		else if (position == 6)
			myIntent = new Intent(v.getContext(), Sound.class);
		else if (position == 7)
			myIntent = new Intent(v.getContext(), Vdd.class);
		else if (position == 8)
			myIntent = new Intent(v.getContext(), About.class);
		else
			myIntent = null;
		if (ROOT)
			startActivity(myIntent);
	}
}
