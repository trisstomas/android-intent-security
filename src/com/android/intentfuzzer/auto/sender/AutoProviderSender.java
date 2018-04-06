package com.android.intentfuzzer.auto.sender;

import com.android.intentfuzzer.auto.Constants;
import com.android.intentfuzzer.util.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;

public class AutoProviderSender implements AutoSender<Intent> {
	
	// ContentProvider 的测试比较特殊，包含了 CRUD 四个操作
	
	private Handler mMainHanlder;
	
	public AutoProviderSender(Handler mainHandler) {
		this.mMainHanlder = mainHandler;
	}

	@Override
	public void send(final Activity activity, Intent fuzzIntent) {
		Parcelable parcelableExtra = fuzzIntent.getParcelableExtra("providerinfo");
		ProviderInfo providerInfo = null;
		if (parcelableExtra == null) {
			return;
		}
		
		providerInfo = (ProviderInfo) parcelableExtra;
		final ContentResolver contentResolver = activity.getContentResolver();
		// TODO
		final Uri uri = Uri.parse(providerInfo.authority);
		
		Utils.d(AutoProviderSender.class, "start do operation in uri:" + uri.toString());
		
		mMainHanlder.post(new Runnable() {
			@Override
			public void run() {
				try {
					ContentValues values = new ContentValues();
					contentResolver.insert(uri, values);
					
					contentResolver.query(uri, null, null, null, null);
					
					contentResolver.delete(uri, null, null);
					
					contentResolver.update(uri, values, null, null);
				} catch(Exception e) {
					Utils.d(AutoProviderSender.class, "exception in AutoProviderSender");
				}
			}
		});
	}

}
