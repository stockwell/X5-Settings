package com.ideos.x5settings;

import java.io.BufferedReader;
import java.io.FileReader;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import android.widget.Spinner;

public class Led extends Activity{
	private static String ledConfig;
	
	private Button applyButton;
	private Spinner spinner;
	private CheckBox keypad_led_bl;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led);
        
        applyButton = (Button) findViewById(R.id.button1);
    	spinner = (Spinner) findViewById(R.id.spinner1);
        keypad_led_bl = (CheckBox) findViewById(R.id.checkBox1);
        
        String[] ledItems = getResources().getStringArray(R.array.sel_led);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	android.R.layout.simple_spinner_item, ledItems);
    	
    	spinner.setAdapter(adapter);
    	
    	applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setLED(spinner.getSelectedItemPosition());
          	 }
		});   
    	
    	keypad_led_bl.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Rootcommands.runRootCommand("echo "+ (keypad_led_bl.isChecked() ? 1 : 0) +" > /sys/module/leds_msm_pmic/parameters/disable_keypad_leds");
				if (keypad_led_bl.isChecked()){
					Rootcommands.runRootCommand("echo 0 > /sys/devices/platform/pmic-leds/leds/button-backlight/brightness");
					Toast temp = Toast.makeText(v.getContext(), R.string.temp, 2000);
					temp.show();
				}
				
			}
		});
    	
    	ledConfig = ledStatus();
    	
    	int index = 0;
		if (ledConfig.matches("sleep_enabled")) index = 1;
		else if (ledConfig.matches("bln")) index = 2;
		
    	spinner.setSelection(index);
	}
	
	public void setLED(int Index){
		boolean WRITE = false;
		String command1 = null;
		String command2 = null;
		
	    switch(Index)
	    {
	    case 0:
	    	command1 = "echo 1 > /sys/module/RGB_led/parameters/off_when_suspended\n" +
	    			   "echo 0 > /sys/module/RGB_led/parameters/backlight_notification";
	    	command2 = "echo 1 > /sys/module/RGB_led/parameters/off_when_suspended"; 
	    	break;
	    case 1:
	    	command1 = "echo 0 > /sys/module/RGB_led/parameters/off_when_suspended\n" +
	    			   "echo 0 > /sys/module/RGB_led/parameters/backlight_notification";
	    	command2 = "echo 0 > /sys/module/RGB_led/parameters/off_when_suspended";
	    	break;
	    case 2:
	    	command1 = "echo 1 > /sys/module/RGB_led/parameters/backlight_notification";
	    	command2 = command1;
	    	break;
	    
	    }
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
			Rootcommands.runRootCommand("echo -e '"+command1+"' > /etc/init.d/12leds");
			Rootcommands.runRootCommand(command2);
			if (Index<2)
				Rootcommands.runRootCommand("echo 0 > /sys/module/RGB_led/parameters/backlight_notification");
			Rootcommands.runRootCommand("chmod +x /etc/init.d/12leds");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete, 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
	
	public static String ledStatus() {
		String bln = null;
	    String sleep = null;
		try {
			FileReader input = new FileReader("/sys/module/RGB_led/parameters/backlight_notification");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	bln = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
	    	
	    	if(bln.equals("0")){
	    		input = new FileReader("/sys/module/RGB_led/parameters/off_when_suspended");
		    	reader = new BufferedReader(input);
		    	sleep = reader.readLine();
		    	
		    	reader.close();
		    	input.close();
	    	}
	    	
		}
		 catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
			return "error";
		}
	    if(bln.equals("1")) return "bln";
	    else if(sleep.equals("1")) return "sleep_disabled";
	    else return "sleep_enabled";
	}
}