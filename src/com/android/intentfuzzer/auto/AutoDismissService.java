package com.android.intentfuzzer.auto;

import com.android.intentfuzzer.util.Utils;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class AutoDismissService extends BaseAccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (event == null) {
			return;
		}
		
		// 当测试 APP 中如果应用发生了异常 Crash，那么系统将会弹出一个 AppErrorDialog，这时需要自动点击 OK 键取消
		dismissAppErrorDialogIfExists(event);
		
//		Utils.d(AutoDismissService.class, event.toString());
	}

	@Override
	public void onInterrupt() {
		
	}
	
	private void dismissAppErrorDialogIfExists(AccessibilityEvent event) {
		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
				&& event.getPackageName().equals("android")) {
			AccessibilityNodeInfo nodeInfo = findViewByText("OK", true);
            if (nodeInfo != null) {
            	Utils.d(AutoDismissService.class, "dismiss AppErrorDialog");
                performViewClick(nodeInfo);
            }
		}
	}

}
