package com.android.intentfuzzer.auto;

import com.android.intentfuzzer.R;
import com.android.intentfuzzer.R.id;
import com.android.intentfuzzer.R.layout;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class IntermediateActivity extends Activity {
	
	private Handler mMainHandler;
	private Handler mWorkHandler;
	private ComponentName mComponentName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intermediate);
		
		 getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		mMainHandler = new Handler();
		final Intent fuzzIntent = getIntent().getParcelableExtra(AutoTestManager.KEY_INTENT);
		final int type = getIntent().getIntExtra(AutoTestManager.KEY_TYPE, 0);
		mComponentName = getIntent().getParcelableExtra(AutoTestManager.KEY_COMPONMENT_NAME);
		
		HandlerThread worker = new HandlerThread("worker");
		worker.start();
		mWorkHandler = new Handler(worker.getLooper());
		
		setStarting();
		
		// 0.3s 后启动该组件
		mWorkHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					setStarted();
					AutoTestManager.getInstance().send(IntermediateActivity.this, type, fuzzIntent);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}, 300);
		
		// 0.5s 后停止该组件
		mWorkHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					// 再等待 0.5s 后停止 IntermediaeActivity 界面
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
				TextView textView = (TextView) findViewById(R.id.tv_started);
				textView.setVisibility(View.VISIBLE);
				
				if (mComponentName != null) {
					textView.setText("start: " + mComponentName.getClassName());
				}
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
