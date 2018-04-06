package com.android.intentfuzzer.auto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.intentfuzzer.fuzz.BasicFuzzIntent;
import com.android.intentfuzzer.fuzz.FuzzIntentFatory;
import com.android.intentfuzzer.fuzz.NullFuzzIntent;
import com.android.intentfuzzer.util.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AutoTestManager {

	private final static String TAG = AutoTestManager.class.getSimpleName();

	public static final int SEND_TYPE_ACTIVITY = 1;
	public static final int SEND_TYPE_RECEIVER = 2;
	public static final int SEND_TYPE_SERVICE = 3;

	private Handler mMainHandler;

	private Handler mWorkHandler;

	private Context mContext;

	private Map<Integer, AutoSender<Intent>> mSenderMap;
	
	private static AutoTestManager sInstance;
	
	public static synchronized AutoTestManager getInstance() {
		if (sInstance == null) {
			sInstance = new AutoTestManager();
		}
		return sInstance;
	}
	
	public void init(Context context) {
		if (context instanceof Activity) {
			mContext = context;
		} else {
			throw new IllegalArgumentException("context should from activity.");
		}
	}

	private AutoTestManager() {
		HandlerThread workThread = new HandlerThread(TAG);
		workThread.start();

		mWorkHandler = new Handler(workThread.getLooper());
		mMainHandler = new Handler();

		mSenderMap = new HashMap<Integer, AutoSender<Intent>>();
	}
	
	// 在子线程中执行该方法
	public void batchSendWithComponentName(List<ComponentName> componentNames, int type) {
		Utils.d(AutoTestService.class, "batchSendWithComponentName start...");
		LogObserver.getInstance().start();
		for (ComponentName componentName : componentNames) {
			Intent intent = new NullFuzzIntent();
			intent.setComponent(componentName);
			Utils.d(AutoTestManager.class, "Send: " + componentName);
			send(type, intent);
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LogObserver.getInstance().stop();
	}
	
	// 在子线程中执行该方法
	public void batchSend(List list) {
		Utils.d(AutoTestService.class, "batchSend start...");
		LogObserver.getInstance().start();
		for (int i = 0; i < list.size(); i++) {
			Object component = list.get(i);
			
			ComponentName componentName = null;
			int type = 0;
			
			if (component instanceof ActivityInfo) {
				ActivityInfo activityInfo = (ActivityInfo) component;
				type = SEND_TYPE_ACTIVITY;
				componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
			} else if (component instanceof ServiceInfo) {
				// TODO
			}
			
			for (BasicFuzzIntent intent : FuzzIntentFatory.getInstance().getFuzzIntents(componentName)) {
				send(type, intent);
				
				Utils.d(AutoTestManager.class, "send intent:"  + intent.toString() + " to:" + componentName.toString());
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		LogObserver.getInstance().stop();
	}

	public void send(int type, Intent fuzzIntent) {
		AutoSender<Intent> autoSender = mSenderMap.get(type);
		if (autoSender == null) {
			autoSender = createNewSender(type);
			mSenderMap.put(type, autoSender);
		}

		if (autoSender == null) {
			Log.d(TAG, "get AutoSender failed:" + fuzzIntent + " with type:"
					+ type);
			return;
		}

		Log.d(TAG, "AutoManager send start");
		autoSender.send(fuzzIntent);
	}
	
	private AutoSender<Intent> createNewSender(int type) {
		AutoSender<Intent> autoSender = null;
		switch (type) {
		case SEND_TYPE_ACTIVITY:
			autoSender = new AutoActivitySender(mContext);
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
