package com.android.intentfuzzer.auto;

import java.util.Arrays;
import java.util.List;

import com.android.intentfuzzer.util.Utils;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class AutoDismissService extends BaseAccessibilityService {
	
	private List<String> cancelPackageList = Arrays.asList("android", "com.google.android.youtube");

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (event == null) {
			return;
		}
		
		dismissAppErrorDialogIfExists(event);
		
//		Utils.d(AutoDismissService.class, event.toString());
	}

	@Override
	public void onInterrupt() {
		
	}
	
	// 当测试 APP 中如果应用发生了异常 Crash，那么系统将会弹出一个 AppErrorDialog，这时需要自动点击 OK 键取消
	private void dismissAppErrorDialogIfExists(AccessibilityEvent event) {
		if ((event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
				event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
				&& cancelPackageList.contains(event.getPackageName())) {
			AccessibilityNodeInfo nodeInfo = findViewByText("OK", true);
            if (nodeInfo != null) {
            	Utils.d(AutoDismissService.class, "dismiss AppErrorDialog");
                performViewClick(nodeInfo);
            }
            
            nodeInfo = findViewByText("确定", true);
            if (nodeInfo != null) {
            	Utils.d(AutoDismissService.class, "dismiss AppErrorDialog");
                performViewClick(nodeInfo);
            }
            
            nodeInfo = findViewByText("取消", true);
            if (nodeInfo != null) {
            	Utils.d(AutoDismissService.class, "dismiss AppErrorDialog");
                performViewClick(nodeInfo);
            }
		}
	}

}
