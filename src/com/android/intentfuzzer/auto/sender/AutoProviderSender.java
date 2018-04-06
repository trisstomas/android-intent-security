package com.android.intentfuzzer.auto.sender;

import com.android.intentfuzzer.auto.Constants;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class AutoProviderSender implements AutoSender<Intent> {
	
	// ContentProvider 的测试比较特殊，包含了 CRUD 四个操作
	
	private Handler mMainHanlder;
	
	public AutoProviderSender(Handler mainHandler) {
		this.mMainHanlder = mainHandler;
	}

	@Override
	public void send(final Activity activity, Intent fuzzIntent) {
		mMainHanlder.post(new Runnable() {
			@Override
			public void run() {
				
			}
		});
		
		// 0.5s 后停止该组件
		mMainHanlder.postDelayed(new Runnable() {
			@Override
			public void run() {
				
			}
		}, 500);
	}

}
