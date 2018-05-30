package com.android.intentfuzzer.componentquery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.intentfuzzer.auto.AutoTestManager;
import com.android.intentfuzzer.util.ReflectUtils;
import com.android.intentfuzzer.util.Utils;

public class ExtraComponentQuery implements ComponentQuery {

	@Override
	public Map<Integer, List> query(int type) {
		Map<Integer, List> map = new HashMap<Integer, List>();
		
		Object object = null;
		try {
			object = ReflectUtils.reflect("android.app.ActivityManager")
					.method("getDynamicReceiverNames").get();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if (object != null && object instanceof List) {
			List<String> list = (List<String>) object;
			map.put(AutoTestManager.SEND_TYPE_DYNAMIC_RECEIVER, list);
		} else {
			Utils.d(ExtraComponentQuery.class, "getDynamicReceiverNames failed!");
		}
		
		return map;
	}

}
