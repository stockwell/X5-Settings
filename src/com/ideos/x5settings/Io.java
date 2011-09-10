package com.ideos.x5settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Spinner;

public class Io extends Activity{
	private static String[] availableSchedulers;
	private static String currentScheduler;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.io);
        
        final Button applyButton = (Button) findViewById(R.id.button1);
        Spinner scheds = (Spinner) findViewById(R.id.spinner1);
        
    	availableSchedulers = getSchedulers();
    	int i = 0;
    	for (i=0;i<availableSchedulers.length;i++){
    		if (availableSchedulers[i].contains("[")){
    			String tmp = availableSchedulers[i].substring(1, (availableSchedulers[i].length()-1));
    			currentScheduler = tmp;
    			availableSchedulers[i] = tmp;
    		}
    	}
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	android.R.layout.simple_spinner_item, availableSchedulers);
    	scheds.setAdapter(adapter);
    	
    	applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setScheduler();
    			currentScheduler=((Spinner) findViewById(R.id.spinner1)).getSelectedItem().toString();
          	 }
		});   
		
		scheds.setSelection(Arrays.asList(availableSchedulers).indexOf(currentScheduler));
	}
	
	public void setScheduler(){
		boolean WRITE = false;
		
		String scheduler = "echo ";
		scheduler += ((Spinner) findViewById(R.id.spinner1)).getSelectedItem().toString();
		scheduler += " > /sys/devices/platform/msm_sdcc.2/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler";
		
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
			Rootcommands.runRootCommand(scheduler);
			Rootcommands.runRootCommand("echo '" +
					scheduler+"\n"+
					"' > /etc/init.d/14iosched");
			Rootcommands.runRootCommand("chmod +x /system/etc/init.d/14iosched");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete, 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
	
	public static String[] getSchedulers() {
		String[] schedulers = null;
		try {
			FileReader input = new FileReader("/sys/devices/platform/msm_sdcc.2/mmc_host/mmc0/mmc0:0001/block/mmcblk0/queue/scheduler");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	schedulers = reader.readLine().toString().split(" ");
	    	
	    	reader.close();
	    	input.close();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	    return schedulers;
	}

}