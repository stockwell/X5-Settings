package com.ideos.x5settings;
import java.io.FileReader;
import java.io.BufferedReader;

import android.app.Activity;
import android.widget.Button;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import android.os.Vibrator;
public class Haptic extends Activity{
	private Vibrator hapticVib;
	private static int vibLength;
	
	
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.haptic);
        
        final Button applyButton = (Button) findViewById(R.id.button2);
        final CheckBox vibrate = (CheckBox) findViewById(R.id.checkBox1);
        final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar1);
        final TextView seekBarValue = (TextView)findViewById(R.id.textView2);
        
        vibLength = vibrateStatus();
        seekBar.setProgress(vibLength);
        seekBarValue.setText(Integer.toString(vibLength));
        
        if(vibLength>0) vibrate.setChecked(true);
        hapticVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        
        vibrate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (vibrate.isChecked()){
					hapticVib.vibrate(30);
					seekBar.setProgress(30);
				}
				else {
					seekBar.setProgress(0);					
				}
			}
		});
			
        
        applyButton.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {
        		 setVibrate(vibrate.isChecked());
        	 }
        });
        
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
        	  public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        		vibLength = progress;
        	    seekBarValue.setText(String.valueOf(progress));
        	   }
        	   
        	   public void onStartTrackingTouch(SeekBar seekBar) {
        	   }

        	   public void onStopTrackingTouch(SeekBar seekBar) {
        		   hapticVib.vibrate(vibLength);
        	   }
	       });
	   }
	
   
	public void setVibrate(Boolean vibrate){
		boolean WRITE = false;		   
		    int newVibeLength = 0;
		    if(vibrate) newVibeLength = vibLength;
			WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
			if(WRITE) {
				Rootcommands.runRootCommand("echo -e 'echo "+newVibeLength+" > /sys/module/synaptics_i2c_rmi_1564/parameters/vibrate\n" +
										    "echo "+newVibeLength+" > /sys/module/atmel_i2c_rmi_QT602240/parameters/vibrate' > /system/etc/init.d/10vibrate");
				Rootcommands.runRootCommand("echo "+newVibeLength+" > /sys/module/synaptics_i2c_rmi_1564/parameters/vibrate");
				Rootcommands.runRootCommand("echo "+newVibeLength+" > /sys/module/atmel_i2c_rmi_QT602240/parameters/vibrate");
				Rootcommands.runRootCommand("chmod +x /system/etc/init.d/10vibrate");
				Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
				WRITE = false;
				Toast vibe = Toast.makeText(this, R.string.complete, 2000);
				
				vibe.show();
			}
			else {
	        	Log.d("X5 Settings", "Error writing file");
			}
			
		}
		
	public int vibrateStatus() {
		String value;
		try {
	    	FileReader input = new FileReader("/sys/module/synaptics_i2c_rmi_1564/parameters/vibrate");
	    	BufferedReader reader = new BufferedReader(input);
	    	value = reader.readLine();
	    	reader.close();
	    	input.close();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
			return 0;
		}
	    if(value == null) return 0;
	    else return Integer.parseInt(value);
	
	}
}
