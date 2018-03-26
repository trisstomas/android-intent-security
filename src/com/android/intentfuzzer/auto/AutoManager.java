package com.android.intentfuzzer.auto;

import java.util.HashMap;
import java.util.Map;

import com.android.intentfuzzer.fuzz.BasicFuzzIntent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AutoManager {
	
	private final static String TAG = AutoManager.class.getSimpleName();
	
	public static final int SEND_TYPE_ACTIVITY = 1;
	public static final int SEND_TYPE_RECEIVER = 2;
	public static final int SEND_TYPE_SERVICE = 3;
	
	private Handler mMainHandler;
	
	private Handler mWorkHandler;
	
	private Context mContext;
	
	private Map<Integer, AutoSender<Intent>> mSenderMap;
	
	public AutoManager(Context context) {
		HandlerThread workThread = new HandlerThread(TAG);
		workThread.start();
		
		mWorkHandler = new Handler(workThread.getLooper());
		mMainHandler = new Handler();
		
		if (context instanceof Activity) {
			mContext = context;
		} else {
			throw new IllegalArgumentException("context should from activity.");
		}
		
		
		mSenderMap = new HashMap<Integer, AutoSender<Intent>>();
	}
	
	public void send(int type, Intent fuzzIntent) {
		AutoSender<Intent> autoSender = mSenderMap.get(type);
		if (autoSender == null) {
			autoSender = createNewSender(type);
			mSenderMap.put(type, autoSender);
		}
		
		if (autoSender == null) {
			Log.d(TAG, "get AutoSender failed:" + fuzzIntent + " with type:" + type);
			return ;
		}
		
		Log.d(TAG, "AutoManager send start");
		autoSender.send(fuzzIntent);
	}
	
	private AutoSender<Intent> createNewSender(int type) {
		AutoSender<Intent> autoSender = null;
		switch (type) {
		case SEND_TYPE_ACTIVITY:
			autoSender = new AutoActivitySender(mContext, mMainHandler,mWorkHandler);
			break;
			
		case SEND_TYPE_RECEIVER:
			
			break;
			
		case SEND_TYPE_SERVICE:
			
			break;

		default:
			break;
		}
		return autoSender;
	}

}
