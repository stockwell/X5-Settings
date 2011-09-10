package com.ideos.x5settings;

import java.io.BufferedReader;
import java.io.FileReader;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import android.widget.Spinner;

public class Vold extends Activity{
	private static String voldPrimary = voldStatus();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vold);
        
        String[] voldItems = getResources().getStringArray(R.array.sel_storage);
     	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	android.R.layout.simple_spinner_item, voldItems);
     	
        Spinner s = (Spinner) findViewById(R.id.spinner1);
    	s.setAdapter(adapter);
    	
    	final Button applyButton = (Button) findViewById(R.id.button1);
    	final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
    	
    	applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setVold(spinner.getSelectedItemPosition());
          	 }
		});
    	
    	int index = voldPrimary.matches("sdcard") ? 1 : 0;
    	spinner.setSelection(index);
	}
	
	public boolean setVold(int Index){
		boolean WRITE = false;
		String command = null;
		if (Index == 0)
			command = "echo -e 'dev_mount emmc /mnt/sdcard 14 /devices/platform/msm_sdcc.2/mmc_host/mmc0\n" +
					  "dev_mount sdcard /mnt/emmc auto /devices/platform/msm_sdcc.4/mmc_host/mmc2";
		else
			command = "echo -e 'dev_mount emmc /mnt/emmc 14 /devices/platform/msm_sdcc.2/mmc_host/mmc0\n" +
					  "dev_mount sdcard /mnt/sdcard auto /devices/platform/msm_sdcc.4/mmc_host/mmc2";
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
			Rootcommands.runRootCommand(command + "'> /etc/vold.fstab");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete_reboot, 2000);
			complete.show();
		}
		return true;
	}
	
	public static String voldStatus() {
		String fstab = null;
		String emmcLocation = null;
		try {
			FileReader input = new FileReader("/etc/vold.fstab");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	while((fstab = reader.readLine()) != null)
	    		if (fstab.matches(".*mmc0.*")) emmcLocation = fstab.split(" ")[2];
	    	reader.close();
	    	input.close();
		}
		 catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
			return "error";
		}
	    if(emmcLocation.matches("/mnt/sdcard")) return "emmc";
	    else return "sdcard";
	}
}