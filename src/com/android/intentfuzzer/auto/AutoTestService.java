package com.android.intentfuzzer.auto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.android.intentfuzzer.componentquery.ComponentQuery;
import com.android.intentfuzzer.componentquery.DullComponentQuery;
import com.android.intentfuzzer.util.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AutoTestService extends Service {
	
	private static final String TAG = AutoTestService.class.getSimpleName();
	
	public static AtomicBoolean sAutoTestStarted = new AtomicBoolean(false);
	
	private ComponentQuery mComponentQuery;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mComponentQuery = new DullComponentQuery(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (sAutoTestStarted.get()) {
			Toast.makeText(this, "AutoTest have been launched..", Toast.LENGTH_SHORT).show();
			return START_NOT_STICKY;
		}
		
		sAutoTestStarted.set(true);
		Toast.makeText(this, "AutoTest launched..", Toast.LENGTH_SHORT).show();
		Utils.d(AutoTestService.class, "AutoTest launched..");
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<Integer, List> map = mComponentQuery.query(ComponentQuery.TYPE_ALL);
				AutoTestManager.getInstance().batchSend(map);
				sAutoTestStarted.set(false);
			}
		}).start();
		
		return START_NOT_STICKY;
	}

}
