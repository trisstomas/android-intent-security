package com.android.intentfuzzer.auto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.android.intentfuzzer.IntermediateActivity;
import com.android.intentfuzzer.fuzz.BasicFuzzIntent;

public class AutoActivitySender implements AutoSender<Intent> {
	
	private Activity mActivityContext;
	public static final String KEY_INTENT = "fuzzIntent";
	
	public AutoActivitySender(Context context) {
		this.mActivityContext = (Activity) context;
	}

	@Override
	public void send(final Intent fuzzIntent) {
		Intent intent = new Intent(mActivityContext, IntermediateActivity.class);
		intent.putExtra(KEY_INTENT, fuzzIntent);
		mActivityContext.startActivity(intent);
	}

}
