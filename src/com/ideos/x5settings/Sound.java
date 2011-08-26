package com.ideos.x5settings;

import java.io.BufferedReader;
import java.io.FileReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Sound extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound);
        
        final Button applyButton = (Button) findViewById(R.id.button1);
        final CheckBox forceHeadset = (CheckBox) findViewById(R.id.checkBox1);
        
        if (getSoundSettings()[0]=="1") forceHeadset.setChecked(true);
        
        applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setSound();
          	 }
		});   
	}
	
	public void setSound(){
		boolean WRITE = false;
		
		String headsetSwitch = "echo ";
		headsetSwitch += (((CheckBox) findViewById(R.id.checkBox1)).isChecked()) ? 1 : 0;
		headsetSwitch += " > /sys/module/snd_soc_msm7kv2/parameters/headset_mic_switch";
		
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
			Rootcommands.runRootCommand(headsetSwitch);
			Rootcommands.runRootCommand("echo '" +
					headsetSwitch+""+
					"' > /etc/init.d/15sound");
			Rootcommands.runRootCommand("chmod +x /system/etc/init.d/15sound");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, "Complete", 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
	
	public static String[] getSoundSettings() {
		String settings[] = {null};
		try {
			FileReader input = new FileReader("/sys/module/snd_soc_msm7kv2/parameters/headset_mic_switch");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	settings[0] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	    return settings;
	}
}
