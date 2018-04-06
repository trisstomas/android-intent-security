package com.android.intentfuzzer.auto.sender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.intentfuzzer.auto.Constants;
import com.android.intentfuzzer.fuzz.BasicFuzzIntent;
import com.android.intentfuzzer.util.Utils;

public class AutoActivitySender implements AutoSender<Intent> {
	
	private Handler mMainHanlder;
	
	public AutoActivitySender(Handler mainHandler) {
		this.mMainHanlder = mainHandler;
	}

	@Override
	public void send(final Activity activity, final Intent fuzzIntent) {
		mMainHanlder.post(new Runnable() {
			@Override
			public void run() {
				try {
					Utils.d(AutoServiceSender.class, "startActivity:" + fuzzIntent.toString());
					activity.startActivityForResult(fuzzIntent, Constants.REQUEST_CODE_ACTIIVTY, null);
				} catch(Exception e) {
					Utils.d(AutoActivitySender.class, "start activity failed:" + e.toString());
				}
			}
		});
		
		// 0.5s 后停止该组件
		mMainHanlder.postDelayed(new Runnable() {
			@Override
			public void run() { 
				activity.finishActivity(Constants.REQUEST_CODE_ACTIIVTY);
			}
		}, 500);
	}

}
