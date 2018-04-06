package com.android.intentfuzzer.util;

import java.util.ArrayList;
import java.util.List;

import com.android.intentfuzzer.auto.AutoTestManager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

public class Utils {
	
	private static final String TAG = "IntentFuzzer";

	public static int ALL_APPS = 0;
	public static int SYSTEM_APPS = 1;
	public static int NONSYSTEM_APPS = 2;
	public static int ABOUNT = 3;

	public static final int MSG_PROCESSING = 0;
	public static final int MSG_DONE = 1;
	public static final int MSG_ERROR = 2;

	public static final int ACTIVITIES = 0;
	public static final int RECEIVERS = 1;
	public static final int SERVICES = 2;

	public static final String PKGINFO_KEY = "pkginfo";
	public static final String APPTYPE_KEY = "apptype";
	
	public static void d(Class clazz, String msg) {
		Log.d(TAG, clazz.getSimpleName() + ":" + msg);
	}
	
	public static int switchToNewType(int oldType) {
		switch (oldType) {
		case ACTIVITIES:
			return AutoTestManager.SEND_TYPE_ACTIVITY;
		case RECEIVERS:
			return AutoTestManager.SEND_TYPE_RECEIVER;	
		case SERVICES:
			return AutoTestManager.SEND_TYPE_SERVICE;
		default:
			return -1;
		}
	}

	public static List<AppInfo> getPackageInfo(Context context, int type) {
		List<AppInfo> pkgInfoList = new ArrayList<AppInfo>();

		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(
						PackageManager.GET_DISABLED_COMPONENTS
								| PackageManager.GET_ACTIVITIES
								| PackageManager.GET_RECEIVERS
								| PackageManager.GET_INSTRUMENTATION
								| PackageManager.GET_PROVIDERS
								| PackageManager.GET_SERVICES);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			
			// 如果使运行在 system_server 进程的组件 crash 会导致手机重启...
			// 所以自动化时，不对安卓系统的组件进行测试
			// 同时也不测试 intentfuzzer 应用本身
			// 因为使用的虚拟机进行测试，不支持 camera
			if ("android".equals(packageInfo.packageName) 
					|| "com.android.intentfuzzer".equals(packageInfo.packageName)
					|| "com.android.camera2".equals(packageInfo.packageName)) {
				continue;
			}
			
			if (type == SYSTEM_APPS) {
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
					pkgInfoList.add(fillAppInfo(packageInfo, context));
				}
			} else if (type == NONSYSTEM_APPS) {
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					pkgInfoList.add(fillAppInfo(packageInfo, context));
				}
			} else {
				pkgInfoList.add(fillAppInfo(packageInfo, context));
			}

		}

		return pkgInfoList;

	}

	private static AppInfo fillAppInfo(PackageInfo packageInfo, Context context) {
		AppInfo appInfo = new AppInfo();
		appInfo.setPackageInfo(packageInfo);
		appInfo.setAppName(packageInfo.applicationInfo.loadLabel(
				context.getPackageManager()).toString());
		appInfo.setPackageName(packageInfo.packageName);
		appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(context
				.getPackageManager()));

		return appInfo;

	}
}
