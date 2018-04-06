package com.android.intentfuzzer.fuzz;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;

public class FuzzIntentFatory {
	
	private static FuzzIntentFatory sFuzzIntentFatory;
	
	private FuzzIntentFatory() {
		
	}
	
	public static synchronized FuzzIntentFatory getInstance() {
		if (sFuzzIntentFatory == null) {
			sFuzzIntentFatory = new FuzzIntentFatory();
		}
		return sFuzzIntentFatory;
	}
	
	public List<BasicFuzzIntent> getFuzzIntents(ComponentName componentName) {
		List<BasicFuzzIntent> list = new ArrayList<BasicFuzzIntent>();
		
		// 内容为空的 Intent
		list.add(new NullFuzzIntent());
		
		// Action 与 Data 交叉，都不为空
		list.add(new ActionDataFuzzIntent());
		
		// Action 不为空, Data 为空
		list.add(new ActionNullDataFuzzIntent());
		// Action 不为空，Data 随机
		list.add(new ActionRandomDataFuzzIntent());
		
		// Action 为空，Data 不为空
		list.add(new NullActionDataFuzzIntent());
		// Action 随机，Data 不为空
		list.add(new RandomActionDataFuzzIntent());
		
		// 设置目标组件
		for (Intent intent : list) {
			intent.setComponent(componentName);
		}
		
		return list;
	}

}
