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
        final CheckBox speakerphoneEcho = (CheckBox) findViewById(R.id.checkBox2);
        
        String currentSettings[] = getSoundSettings();
        
        if (Integer.valueOf(currentSettings[0])==1) forceHeadset.setChecked(true);
        if (Integer.valueOf(currentSettings[1])==1) speakerphoneEcho.setChecked(true);
        
        applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setSound();
          	 }
		});   
	}
	
	public void setSound(){
		boolean WRITE = false;
		
		String headsetSwitch = "echo ";
		String echoFix = "echo ";
		headsetSwitch += (((CheckBox) findViewById(R.id.checkBox1)).isChecked()) ? 1 : 0;
		headsetSwitch += " > /sys/module/snd_soc_msm7kv2/parameters/headset_mic_switch";
		echoFix += (((CheckBox) findViewById(R.id.checkBox2)).isChecked()) ? 1 : 0;
		echoFix += " > /sys/module/snd_soc_msm7kv2/parameters/speakerphone_echo_fix";
		
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		
		if(WRITE) {
			Rootcommands.runRootCommand(headsetSwitch);
			Rootcommands.runRootCommand(echoFix);
			Rootcommands.runRootCommand("echo -e '"+
					headsetSwitch+"\n"+
					echoFix +
					"' > /etc/init.d/15sound");
			Rootcommands.runRootCommand("chmod +x /system/etc/init.d/15sound");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete, 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
	
	public static String[] getSoundSettings() {
		String settings[] = {null, null};
		try {
			FileReader input = new FileReader("/sys/module/snd_soc_msm7kv2/parameters/headset_mic_switch");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	settings[0] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();

	    	input = new FileReader("/sys/module/snd_soc_msm7kv2/parameters/speakerphone_echo_fix");
	    	reader = new BufferedReader(input);
	    	
	    	settings[1] = reader.readLine();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	    return settings;
	}
}
