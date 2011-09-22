package com.ideos.x5settings;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.view.View;
import android.util.Log;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Gravity;

public class About extends Activity implements ViewFactory{
	private Handler mHandler = new Handler();
	private int index;
	private TextSwitcher mSwitcher;
	private String[] donators;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        donators = getResources().getStringArray(R.array.donators);
        index = 0;
        
        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher1);
        mSwitcher.setFactory(this);
        
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
				mSwitcher.setText(donators[index]);
				if (index == donators.length-1)
					index = 0;
				else
					index++;
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
	
	 public View makeView() {
	        TextView t = new TextView(this);
	        t.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
	        t.setTextSize(36);
	        return t;
	    }
}