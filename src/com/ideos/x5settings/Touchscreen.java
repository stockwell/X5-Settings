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

public class Touchscreen extends Activity{
	private static int filterThreshold;
    
	private Button applyButton;
	private CheckBox filter;
	private SeekBar seekBar;
	private TextView seekBarValue;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchscreen);
        
    	applyButton = (Button) findViewById(R.id.button1);
        filter = (CheckBox) findViewById(R.id.checkBox1);
        seekBar = (SeekBar)findViewById(R.id.seekBar1);
        seekBarValue = (TextView)findViewById(R.id.textView2);
        
        filterStatus();
        
        filter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (filter.isChecked()){
					seekBar.setProgress(5);
				}
				else {
					seekBar.setProgress(0);					
				}
			}
		});
        
        applyButton.setOnClickListener(new View.OnClickListener() {
        	 public void onClick(View view) {
        		 setFilter(filter.isChecked());
        	 }
        });
        
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
        	  public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
        		filterThreshold = progress;
        	    seekBarValue.setText(String.valueOf(progress));
        	   }
        	   
        	   public void onStartTrackingTouch(SeekBar seekBar) {
        	   }

        	   public void onStopTrackingTouch(SeekBar seekBar) {
        	   }
	       });
	   }
	
   
	public void setFilter(Boolean enabled){
		boolean WRITE = false;		   
	    int newFilterThreshold = 0;
	    if(enabled) newFilterThreshold = filterThreshold;
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
			Rootcommands.runRootCommand("echo 'echo "+newFilterThreshold+" > /sys/module/synaptics_i2c_rmi_1564/parameters/dup_threshold' > /etc/init.d/11dupfilter");
			Rootcommands.runRootCommand("echo "+newFilterThreshold+" > /sys/module/synaptics_i2c_rmi_1564/parameters/dup_threshold");
			Rootcommands.runRootCommand("chmod +x /etc/init.d/11dupfilter");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete, 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
		
	public void filterStatus() {
		String filterThreshold;
		try {
	    	FileReader input = new FileReader("/sys/module/synaptics_i2c_rmi_1564/parameters/dup_threshold");
	    	BufferedReader reader = new BufferedReader(input);
	    	filterThreshold = reader.readLine();
	    	reader.close();
	    	input.close();
	    
	    	seekBar.setProgress(Integer.valueOf(filterThreshold));
	        seekBarValue.setText(filterThreshold);
	        if(Integer.valueOf(filterThreshold)>0) filter.setChecked(true);
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	}
}
