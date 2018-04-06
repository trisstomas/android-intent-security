package com.android.intentfuzzer.auto.sender;

import com.android.intentfuzzer.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class AutoBroadcastSender implements AutoSender<Intent> {
	
	private Handler mMainHanlder;
	
	public AutoBroadcastSender(Handler mainHandler) {
		this.mMainHanlder = mainHandler;
	}

	@Override
	public void send(final Activity activity, final Intent fuzzIntent) {
		mMainHanlder.post(new Runnable() {
			@Override
			public void run() {
				try {
					Utils.d(AutoServiceSender.class, "sendBroadcast:" + fuzzIntent.toString());
					activity.sendBroadcast(fuzzIntent);
				} catch(Exception e) {
					Utils.d(AutoActivitySender.class, "send broadcast failed:" + e.toString());
				}
			}
		});
		
		// 广播一旦发出，不能停止
	}

}
