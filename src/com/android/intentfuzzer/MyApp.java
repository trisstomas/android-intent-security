package com.android.intentfuzzer;

import com.android.intentfuzzer.auto.BaseAccessibilityService;

import android.app.Application;
import android.content.pm.PackageInfo;


public class MyApp extends Application {

	PackageInfo packageInfo;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		BaseAccessibilityService.getInstance().init(this);
	}
	
}
