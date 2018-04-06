package com.android.intentfuzzer.componentquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import com.android.intentfuzzer.auto.AutoTestManager;
import com.android.intentfuzzer.util.AppInfo;
import com.android.intentfuzzer.util.Utils;

public class DullComponentQuery implements ComponentQuery {
	
	private Context mContext;
	
	public DullComponentQuery(Context context) {
		this.mContext = context;
	}

	@Override
	public Map<Integer, List> query(int type) {
		List<AppInfo> appInfos = Utils.getPackageInfo(mContext, Utils.ALL_APPS);
		Map<Integer, List> map = new HashMap<Integer, List>();
		map.put(AutoTestManager.SEND_TYPE_ACTIVITY, new ArrayList<ActivityInfo>());
		map.put(AutoTestManager.SEND_TYPE_RECEIVER, new ArrayList<ActivityInfo>());
		map.put(AutoTestManager.SEND_TYPE_PROVIDER, new ArrayList<ProviderInfo>());
		map.put(AutoTestManager.SEND_TYPE_SERVICE, new ArrayList<ServiceInfo>());
		
		for (AppInfo appInfo : appInfos) {
			PackageInfo packageInfo = appInfo.getPackageInfo();
			switch (type) {
			case TYPE_ALL:
				if (packageInfo.activities != null) {
					map.get(AutoTestManager.SEND_TYPE_ACTIVITY).addAll(Arrays.asList(packageInfo.activities));
				}
				
				if (packageInfo.services != null) {
					map.get(AutoTestManager.SEND_TYPE_SERVICE).addAll(Arrays.asList(packageInfo.services));
				}
				
				if (packageInfo.providers != null) {
					map.get(AutoTestManager.SEND_TYPE_PROVIDER).addAll(Arrays.asList(packageInfo.providers));
				}
				
				if (packageInfo.receivers != null) {
					map.get(AutoTestManager.SEND_TYPE_RECEIVER).addAll(Arrays.asList(packageInfo.receivers));
				}
				break;
				
			case TYPE_ACTIVITY:
				if (packageInfo.activities != null) {
					map.get(AutoTestManager.SEND_TYPE_ACTIVITY).addAll(Arrays.asList(packageInfo.activities));
				}
				break;
				
			case TYPE_SERVICE:
				if (packageInfo.services != null) {
					map.get(AutoTestManager.SEND_TYPE_SERVICE).addAll(Arrays.asList(packageInfo.services));
				}
				break;
				
			case TYPE_PROVIDER:
				if (packageInfo.providers != null) {
					map.get(AutoTestManager.SEND_TYPE_PROVIDER).addAll(Arrays.asList(packageInfo.providers));
				}
				break;
				
			case TYPE_BROADCAST:
				if (packageInfo.receivers != null) {
					map.get(AutoTestManager.SEND_TYPE_RECEIVER).addAll(Arrays.asList(packageInfo.receivers));
				}
				break;

			default:
				break;
			}
			
		}
		return map;
	}
	
}
