package com.android.intentfuzzer.auto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.android.intentfuzzer.auto.sender.AutoActivitySender;
import com.android.intentfuzzer.auto.sender.AutoBroadcastSender;
import com.android.intentfuzzer.auto.sender.AutoProviderSender;
import com.android.intentfuzzer.auto.sender.AutoSender;
import com.android.intentfuzzer.auto.sender.AutoServiceSender;
import com.android.intentfuzzer.fuzz.BasicFuzzIntent;
import com.android.intentfuzzer.fuzz.FuzzIntentFatory;
import com.android.intentfuzzer.fuzz.NullFuzzIntent;
import com.android.intentfuzzer.util.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class AutoTestManager {

	private final static String TAG = AutoTestManager.class.getSimpleName();
	
	public static final String KEY_INTENT = "fuzzIntent";
	public static final String KEY_TYPE = "type";
	public static final String KEY_COMPONMENT_NAME = "cn";

	public static final int SEND_TYPE_ACTIVITY = 1;
	public static final int SEND_TYPE_RECEIVER = 2;
	public static final int SEND_TYPE_SERVICE = 3;
	public static final int SEND_TYPE_PROVIDER = 4;

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
			for (BasicFuzzIntent intent : FuzzIntentFatory.getInstance().getFuzzIntents(componentName)) {
				Utils.d(AutoTestManager.class, "send intent:"  + intent.toString() + " to:" + componentName.toString());
				
				startIntermediateActivity(intent, type, componentName);
				
				sleepAWhile();
			}
		}
		LogObserver.getInstance().stop();
	}
	
	private boolean allowSend(ComponentInfo info) {
		return info.exported;
	}
	
	// 在子线程中执行该方法
	public void batchSend(Map<Integer, List> map) {
		Utils.d(AutoTestService.class, "batchSend start...");
		LogObserver.getInstance().start();
		
		for (Integer type : map.keySet()) {
			List list = map.get(type);
			for (int i = 0; i < list.size(); i++) {
				Object component = list.get(i);
				
				// 如果组件exported=false，意味着不对其它APP开放，是不能进行模糊测试的
				if (component instanceof ComponentInfo) {
					ComponentInfo info = (ComponentInfo) component;
					if (!allowSend(info)) {
						continue;
					}
				}
				
				ComponentName componentName = null;
				
				if (type == SEND_TYPE_ACTIVITY || type == SEND_TYPE_RECEIVER) {
					ActivityInfo activityInfo = (ActivityInfo) component;
					componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
				} else if (type == SEND_TYPE_SERVICE) {
					ServiceInfo serviceInfo = (ServiceInfo) component;
					componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
				} else if (type == SEND_TYPE_PROVIDER) {
					ProviderInfo providerInfo = (ProviderInfo) component;
					componentName = new ComponentName(providerInfo.packageName, providerInfo.name);
				} 
				
				for (BasicFuzzIntent intent : FuzzIntentFatory.getInstance().getFuzzIntents(componentName)) {
					if (type == SEND_TYPE_PROVIDER) {
						intent.putExtra("providerinfo", (ProviderInfo) component);
					}
					startIntermediateActivity(intent, type, componentName);
					
					Utils.d(AutoTestManager.class, "send type:" + type + " intent:"  + intent.toString() + " to:" + componentName.toString());
					
					sleepAWhile();
				}
			}
		}
		
		LogObserver.getInstance().stop();
		Utils.d(AutoTestManager.class, "batchSend stop");
	}
	
	// 组件的发送间隔为 2s 一个
	private void sleepAWhile() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void startIntermediateActivity(Intent fuzzIntent, int type, ComponentName componentName) {
		Intent intent = new Intent(mContext, IntermediateActivity.class);
		intent.putExtra(KEY_INTENT, fuzzIntent);
		intent.putExtra(KEY_TYPE, type);
		intent.putExtra(KEY_COMPONMENT_NAME, componentName);
		mContext.startActivity(intent);
	}

	public void send(Activity activityContext, int type, Intent fuzzIntent) {
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
		try {
			autoSender.send(activityContext, fuzzIntent);
		} catch(Exception e) {
			Utils.d(AutoTestManager.class, "exception happen during send:" + e.toString());
		}
	}
	
	private AutoSender<Intent> createNewSender(int type) {
		AutoSender<Intent> autoSender = null;
		switch (type) {
		case SEND_TYPE_ACTIVITY:
			autoSender = new AutoActivitySender(mMainHandler);
			break;

		case SEND_TYPE_RECEIVER:
			autoSender = new AutoBroadcastSender(mMainHandler);
			break;

		case SEND_TYPE_SERVICE:
			autoSender = new AutoServiceSender(mMainHandler);
			break;
			
		case SEND_TYPE_PROVIDER:
			autoSender = new AutoProviderSender(mMainHandler);
			break;

		default:
			break;
		}
		return autoSender;
	}

}
