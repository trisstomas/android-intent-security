package com.android.intentfuzzer.auto.sender;

import com.android.intentfuzzer.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class AutoServiceSender implements AutoSender<Intent> {
	
	private Handler mMainHanlder;
	
	public AutoServiceSender(Handler mainHandler) {
		this.mMainHanlder = mainHandler;
	}

	@Override
	public void send(final Activity activity, final Intent fuzzIntent) {
		mMainHanlder.post(new Runnable() {
			@Override
			public void run() {
				try {
					Utils.d(AutoServiceSender.class, "startService:" + fuzzIntent.toString());
					activity.startService(fuzzIntent);
				} catch(Exception e) {
					Utils.d(AutoActivitySender.class, "start service failed:" + e.toString());
				}
			}
		});
		
		// 0.5s 后停止该组件
		mMainHanlder.postDelayed(new Runnable() {
			@Override
			public void run() {
				activity.stopService(fuzzIntent);
			}
		}, 500);
	}

}
