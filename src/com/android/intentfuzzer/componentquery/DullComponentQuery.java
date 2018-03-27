package com.android.intentfuzzer.componentquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.android.intentfuzzer.util.AppInfo;
import com.android.intentfuzzer.util.Utils;

public class DullComponentQuery implements ComponentQuery {
	
	private Context mContext;
	
	public DullComponentQuery(Context context) {
		this.mContext = context;
	}

	@Override
	public List query(int type) {
		List<AppInfo> appInfos = Utils.getPackageInfo(mContext, Utils.ALL_APPS);
		List resultList = new ArrayList();
		
		for (AppInfo appInfo : appInfos) {
			PackageInfo packageInfo = appInfo.getPackageInfo();
			switch (type) {
			case TYPE_ALL:
				// TODO
				resultList.addAll(Arrays.asList(packageInfo.activities));
				resultList.addAll(Arrays.asList(packageInfo.services));
				resultList.addAll(Arrays.asList(packageInfo.providers));
				break;
				
			case TYPE_ACTIVITY:
				if (packageInfo.activities != null) {
					resultList.addAll(Arrays.asList(packageInfo.activities));
				}
				break;
				
			case TYPE_SERVICE:
				resultList.addAll(Arrays.asList(packageInfo.services));
				break;
				
			case TYPE_PROVIDER:
				resultList.addAll(Arrays.asList(packageInfo.providers));
				break;

			default:
				break;
			}
			
		}
		return resultList;
	}
	
}
