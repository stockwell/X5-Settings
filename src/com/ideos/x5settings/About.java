package com.ideos.x5settings;
import java.io.FileReader;
import java.io.BufferedReader;

import android.app.Activity;
import android.widget.Button;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Gravity;

public class About extends Activity{
	private Handler mHandler = new Handler();
	private int index;
	private TextSwitcher mSwitcher;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        index = 0;
        
        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher1);

        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);
	}

private Runnable UpdateName = new Runnable() {
	   public void run() {
		   
		   try {
				index+=1;
				mSwitcher.setText("hello");  
			} catch (Exception e) {
				Log.d("X5 Settings", "Unexpected error: "+e.getMessage());
			}
		   
		   mHandler.postDelayed(UpdateName, 2000);
	   }
	};
	
	@Override
 protected void onDestroy() {
		mHandler.removeCallbacks(UpdateName);
		super.onDestroy();
	}
	
	@Override
	protected void onPause(){
		mHandler.removeCallbacks(UpdateName);
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		mHandler.post(UpdateName);
		super.onResume();
	}
}