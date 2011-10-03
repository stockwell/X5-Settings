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

public class Misc extends Activity{
	private Button applyButton;
	private CheckBox geminiBox;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misc);
        
        applyButton = (Button) findViewById(R.id.button1);
        geminiBox = (CheckBox) findViewById(R.id.checkBox1);
        
        applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setMisc();
          	 }
		});   
        
        getMiscSettings();
        
	}
	
	public void setMisc(){
		boolean WRITE = false;
		String gemini = "chmod ";
		gemini += geminiBox.isChecked() ? "0666" : "0000";
		gemini += " /dev/gemini0";
		
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		
		if(WRITE) {
			Rootcommands.runRootCommand(gemini);
			Rootcommands.runRootCommand("echo "+					
					gemini +
					" > /etc/init.d/16misc");
			Rootcommands.runRootCommand("chmod +x /system/etc/init.d/16misc");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete, 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
	
	public void getMiscSettings() {
		String settings = null;
		FileReader input;
		BufferedReader reader;
		
		try {
			input = new FileReader("/etc/init.d/16misc");
	    	reader = new BufferedReader(input);
	    	
	    	settings = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
	    	if(settings.split(" ")[1].matches("0666")){
				geminiBox.setChecked(true);
			}

		}catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	
	}
}
