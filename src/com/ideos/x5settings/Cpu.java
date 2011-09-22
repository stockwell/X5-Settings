package com.ideos.x5settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

public class Cpu extends Activity{
	private static String[] availableGovernors;
	private static String[] currentFreq;
	private static String[] freqArray;
	private static String currentGovernor;
	private Handler mHandler = new Handler();
	
	private Button applyButton;
	private TextView minFreqValue;
	private TextView maxFreqValue;
	private TextView currentFreqValue;
	private SeekBar minSeekBar;
	private SeekBar maxSeekBar;
	private Spinner govs;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpu);
    	
        applyButton = (Button) findViewById(R.id.button1);
    	minFreqValue = (TextView)findViewById(R.id.textView4);
    	maxFreqValue = (TextView)findViewById(R.id.textView5);
    	currentFreqValue = (TextView) findViewById(R.id.textView6);
    	minSeekBar = (SeekBar) findViewById(R.id.seekBar1);
    	maxSeekBar = (SeekBar) findViewById(R.id.seekBar2);
    	govs = (Spinner) findViewById(R.id.spinner1);
    	
    	currentFreq = getMinMax();
    	currentGovernor = getCurrentGovernor();
    	freqArray = getFrequencies();
    	availableGovernors = getGovernors();
        
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	android.R.layout.simple_spinner_item, availableGovernors);
    	govs.setAdapter(adapter);
    	
    	applyButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			setCPU();
          	 }
		});   
    	
		minSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				if(progress>maxSeekBar.getProgress()){ 
					progress = maxSeekBar.getProgress();
					minSeekBar.setProgress(progress);
				}
				minFreqValue.setText(freqArray[progress]);
			}           
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
    	});
		
		maxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				if(progress<minSeekBar.getProgress()){
					progress = minSeekBar.getProgress();
					maxSeekBar.setProgress(progress);
				}
				maxFreqValue.setText(freqArray[progress]);
			}           
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
    	});
		maxSeekBar.setMax(freqArray.length-1);
		minSeekBar.setMax(freqArray.length-1);
		minFreqValue.setText(freqArray[0]);
		
		govs.setSelection(Arrays.asList(availableGovernors).indexOf(currentGovernor));
		maxSeekBar.setProgress(Arrays.asList(freqArray).indexOf(currentFreq[1]));
		minSeekBar.setProgress(Arrays.asList(freqArray).indexOf(currentFreq[0]));
	}
	
	public void setCPU(){
		boolean WRITE = false;
		
		String min = "echo ";
		min += minFreqValue.getText();
		min += " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
		
		String max = "echo ";
		max += maxFreqValue.getText();
		max += " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
		
		String governor = "echo ";
		governor += govs.getSelectedItem().toString();
		governor += " > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
		
		WRITE = Rootcommands.runRootCommand("mount -o rw,remount -t ext4 /dev/block/mmcblk0p12 /system");
		if(WRITE) {
			Rootcommands.runRootCommand(min);
			Rootcommands.runRootCommand(max);
			Rootcommands.runRootCommand(governor);
			Rootcommands.runRootCommand("echo -e '" +
					min+"\n"+
					max+"\n"+
					governor+
					"' > /etc/init.d/13cpu");
			Rootcommands.runRootCommand("chmod +x /system/etc/init.d/13cpu");
			Rootcommands.runRootCommand("mount -o ro,remount -t ext4 /dev/block/mmcblk0p12 /system");
			WRITE = false;
			Toast complete = Toast.makeText(this, R.string.complete, 2000);
			complete.show();
		}
		else {
        	Log.d("X5 Settings", "Error writing file");
		}
	}
	
	public static String[] getGovernors() {
		String[] governors = null;
		try {
			FileReader input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	governors = reader.readLine().toString().split(" ");
	    	
	    	reader.close();
	    	input.close();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	    return governors;
	}
	
	public static String[] getFrequencies() {
		String[] frequencies = null;
		try {
			FileReader input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	frequencies = reader.readLine().toString().split(" ");
	    	
	    	reader.close();
	    	input.close();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	    return frequencies;
	}
	
	public static String[] getMinMax() {
		String current_frequencies[] = {null,null};
		
		try {
			FileReader input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
	    	BufferedReader reader = new BufferedReader(input);
	    	
	    	current_frequencies[0] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
	    	input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
			reader = new BufferedReader(input);
	    	
	    	current_frequencies[1] = reader.readLine();
	    	
	    	reader.close();
	    	input.close();
	    	
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
	    return current_frequencies;
	}
	
	public static String getCurrentGovernor() {
		String currentGovernor = null;
		
		try {
			FileReader input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
			BufferedReader reader = new BufferedReader(input);
			
			currentGovernor = reader.readLine();
			
			reader.close();
			input.close();
		} catch (Exception e) {
			Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
		}
		
		return currentGovernor;
	}

private Runnable UpdateCpuFreq = new Runnable() {
	   public void run() {
		   String currentFreq = null;
		   try {
				FileReader input = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
				BufferedReader reader = new BufferedReader(input);
				
				currentFreq = reader.readLine();
				
				reader.close();
				input.close();
			} catch (Exception e) {
				Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
			}
		   currentFreqValue.setText(currentFreq);  
		   mHandler.postDelayed(UpdateCpuFreq, 800);
	   }
	};
	
	@Override
    protected void onDestroy() {
		mHandler.removeCallbacks(UpdateCpuFreq);
		super.onDestroy();
	}
	
	@Override
	protected void onPause(){
		mHandler.removeCallbacks(UpdateCpuFreq);
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		mHandler.post(UpdateCpuFreq);
		super.onResume();
	}
}
