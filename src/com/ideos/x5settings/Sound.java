package com.ideos.x5settings;

import java.io.BufferedReader;
import java.io.FileReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Sound extends Activity{
	private Button applyButton;
	private CheckBox forceHeadset;
	private CheckBox speakerphoneEcho;
	private SeekBar seekBar;
	private TextView seekBarValue;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound);
        
        applyButton = (Button) findViewById(R.id.button1);
        forceHeadset = (CheckBox) findViewById(R.id.checkBox1);
        speakerphoneEcho = (CheckBox) findViewById(R.id.checkBox2);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        seekBarValue = (TextView) findViewById(R.id.textView2);
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
      	  public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      	    seekBarValue.setText(String.valueOf(progress));
      	   }
      	   
      	   public void onStartTrackingTouch(SeekBar seekBar) {
      	   }

      	   public void onStopTrackingTouch(SeekBar seekBar) {
      	   }
       });
	
        
        applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setSound();
          	 }
		});   
        
        getSoundSettings();
	}
	
	public void setSound(){
		boolean WRITE = false;
		
		String headsetSwitch = "echo ";
		String echoFix = "echo ";
		headsetSwitch += forceHeadset.isChecked() ? 1 : 0;
		headsetSwitch += " > /sys/module/snd_soc_msm7kv2/parameters/headset_mic_switch";
		echoFix += speakerphoneEcho.isChecked() ? 1 : 0;
		echoFix += " > /sys/module/snd_soc_msm7kv2/parameters/speakerphone_echo_fix";
		
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		
		if(WRITE) {
			Rootcommands.runRootCommand(headsetSwitch);
			Rootcommands.runRootCommand(echoFix);
			Rootcommands.runRootCommand("echo -e '"+
					headsetSwitch+"\n"+
					echoFix +
					"' > /etc/init.d/15sound");
			Rootcommands.runRootCommand("echo " + seekBar.getProgress() + " > /system/etc/volumefactor.txt");
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
	
	public void getSoundSettings() {
		String settings[] = {null, null, null};
		try {
			FileReader input = new FileReader("/sys/module/snd_soc_msm7kv2/parameters/headset_mic_switch");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	settings[0] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();

	    	input = new FileReader("/sys/module/snd_soc_msm7kv2/parameters/speakerphone_echo_fix");
	    	reader = new BufferedReader(input);
	    	
	    	settings[1] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
	    	input = new FileReader("/etc/volumefactor.txt");
	    	reader = new BufferedReader(input);
	    	
	    	settings[2] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
	    	if (Integer.valueOf(settings[0])==1) forceHeadset.setChecked(true);
	        if (Integer.valueOf(settings[1])==1) speakerphoneEcho.setChecked(true);
	        seekBar.setProgress(Integer.valueOf(settings[2]));
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	
	}
}
