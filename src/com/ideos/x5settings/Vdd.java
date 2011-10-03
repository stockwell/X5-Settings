package com.ideos.x5settings;
import java.io.FileReader;
import java.io.BufferedReader;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

public class Vdd extends Activity {
	
	private static final int freq_count = 14; 
	
	private Button applyButton;
	private Button testButton;
	private Spinner frequencySpinner;
	private EditText VDD;
	
	String[] frequencies = new String[freq_count];
	String[] vdds = new String[freq_count];
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.vdd);
	        
	        VDD = (EditText) findViewById(R.id.editText1);
	        frequencySpinner = (Spinner) findViewById(R.id.spinner1);
	        
	        getFreqs();
	        
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	            	android.R.layout.simple_spinner_item, frequencies);
	            	frequencySpinner.setAdapter(adapter);
	            	
            frequencySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					VDD.setText(vdds[arg2]);
					
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
            });
	}
	
	public void getFreqs() {
		String tmp = null;
		int i = 0;
		try {
			FileReader input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/vdd_levels");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	while((tmp=reader.readLine())!=null){
	    		frequencies[i] = tmp.split(": ")[0];
	    		frequencies[i] = frequencies[i].replaceAll(" ", "");
	    		vdds[i] = tmp.split(": ")[1].trim();
	    		i++;
	    	}
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	}
	
	public void setVdd(){
		boolean WRITE = false;
		int i=0;
		for (i=0;i<freq_count;i++){
			String command = "echo ";
			command += frequencies[i] +
			" " +
			vdds[i];
		}
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
		}
	}
}