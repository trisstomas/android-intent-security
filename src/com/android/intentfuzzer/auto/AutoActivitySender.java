package com.android.intentfuzzer.auto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.intentfuzzer.fuzz.BasicFuzzIntent;

public class AutoActivitySender implements AutoSender<Intent> {
	
	private Activity mActivityContext;
	
	private Handler mMainHandler;
	
	private Handler mWorkHandler;
	
	private static final int DELAY_ACTIIVTY_FINISH = 1000;
	
	public AutoActivitySender(Context context, Handler mainHandler, Handler workHandler) {
		this.mActivityContext = (Activity) context;
		this.mMainHandler = mainHandler;
		this.mWorkHandler = workHandler;
	}
	

	@Override
	public void send(final Intent fuzzIntent) {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mActivityContext.startActivityForResult(fuzzIntent, Constants.REQUEST_CODE_ACTIIVTY, null);
			}
		});
		
		mWorkHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mMainHandler.post(new Runnable() {
					@Override
					public void run() {
						mActivityContext.finishActivity(Constants.REQUEST_CODE_ACTIIVTY);
					}
				});
			}
		}, DELAY_ACTIIVTY_FINISH);
		
	}

}
