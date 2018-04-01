package com.android.intentfuzzer;

import com.android.intentfuzzer.auto.AutoActivitySender;
import com.android.intentfuzzer.auto.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.TextView;

public class IntermediateActivity extends Activity {
	
	private Handler mMainHandler;
	private Handler mWorkHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intermediate);
		
		mMainHandler = new Handler();
		final Intent fuzzIntent = getIntent().getParcelableExtra(AutoActivitySender.KEY_INTENT);
		
		HandlerThread worker = new HandlerThread("worker");
		worker.start();
		mWorkHandler = new Handler(worker.getLooper());
		
		setStarting();
		
		mWorkHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					setStarted();
					IntermediateActivity.this.startActivityForResult(fuzzIntent, Constants.REQUEST_CODE_ACTIIVTY, null);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}, 300);
		
		mWorkHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					IntermediateActivity.this.finishActivity(Constants.REQUEST_CODE_ACTIIVTY);
					Thread.sleep(500);
					setResult("finish success");
				} catch(Exception e) {
					e.printStackTrace();
					setResult("exception catched");
				}
			}
		}, 500);
	}
	
	private void setStarting() {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.tv_starting).setVisibility(View.VISIBLE);				
			}
		});
	}
	
	private void setStarted() {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.tv_started).setVisibility(View.VISIBLE);
			}
		});
	}
	
	private void setResult(final String result) {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				TextView view = (TextView) findViewById(R.id.tv_result);
				view.setVisibility(View.VISIBLE);
				view.setText(result);
				
				finish();
			}
		});
	}

}
