IntentFuzzer
====================

IntentFuzzer is inspired by the tool(https://www.isecpartners.com/tools/mobile-security/intent-fuzzer.aspx)
developed by iSECpartners(www.isecpartners.com).
You can specify an application,then either fuzz a single component or all components!
For a single component, just click an item listed. While click the "Null Fuzz All" button for all components!


扩展功能
===================

测试环境：Genymotion Custom Phone - 5.1.0 API 22 758x1280

要想让批量自动测试正常工作、并生成异常捕捉信息，需要做以下操作：

1. 如果是首次安装 APP，或者没有打开无障碍服务，需要在首页点击 "About" 进入设置界面给权限
2. 监听 EventLog 来获取异常信息需要应用为系统 APP, 依次输入命令
	* 卸载手机里已安装的 IntentFuzzer 应用
	* adb root (确保手机能够 root)
	* adb remount
	* adb shell (进入手机 shell)
	* cd /system/priv-app/
	* mkdir IntentFuzzer
	* exit
	* adb push <local apk file> /system/priv-app/IntentFuzzer/ (寻找到本地编译好的 apk 文件，一般能在项目目录中的bin 目录下找到)
	* adb reboot (重新启动手机，如果命令不生效，也可以手动重启)
	* 这个操作只做一次就行，之后在 eclipse 可以一键编译、安装 (AS 应该也行)

获取 DB 中的测试结果数据:

* PC 上下载一个 SQLite 客户端，我用的是：[http://sqlitebrowser.org/](http://sqlitebrowser.org/)
* adb pull /data/data/com.android.intentfuzzer/databases/result.db ./ (将测试结果 db 文件从手机拉到 PC)
* 使用 PC 上的客户端进行查看

## 1. 组件信息收集

## 2. 模糊 Intent 生成

- [x] 完成共 7 种模糊 Intent 测试用例

## 3. 测试自动化

- [x] 批量测试组件
- [x] 自动开启、关闭 Activity，避免内存占用
- [x] 增加广播、服务组件的测试

## 4. 异常信息捕捉

- [x] 通过监听 EventLog 的方式，实现异常捕捉
- [x] 批量测试日志持久化到数据库中

## 5. 测试结果统计、可视化

## 实现原理

### Accessbility: 自动取消错误弹窗

涉及文件：

* /IntentFuzzer/src/com/android/intentfuzzer/auto/BaseAccessibilityService.java
* /IntentFuzzer/src/com/android/intentfuzzer/auto/AutoDismissService.java

核心代码:

```java
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
```

参考资料：

* [深入了解AccessibilityService](https://blog.csdn.net/dd864140130/article/details/51794318)
* [AccessibilityService分析与防御](https://lizhaoxuan.github.io/2017/11/29/AccessibilityService%E5%88%86%E6%9E%90%E4%B8%8E%E9%98%B2%E5%BE%A1/)

### EventLog: 自动捕捉错误日志 (需要手机root, 将应用提升为系统应用)

涉及文件：

* /IntentFuzzer/src/com/android/intentfuzzer/auto/LogObserver.java

核心代码：

```java
while (sStarted) {
	try {
		while ((line = dis.readLine()) != null) {
			ExceptionInfo exceptionInfo = parseAndCreateExceptionInfo(line);
			// IntentFuzzer 本身的异常不算入结果中
			if (exceptionInfo != null && !TextUtils.equals("com.android.intentfuzzer", 
					exceptionInfo.getmCrashPackageName())) {
				mDBHelper.insertExceptionInfo(exceptionInfo);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		Utils.d(LogFetcher.class, "exception in reading log");
	}
}
```

参考资料:

* [Android event日志打印原理](https://blog.csdn.net/yaowei514473839/article/details/53513435)
* [Android-EventLog-Tags 说明](https://www.robotshell.com/2018/01/08/Android-EventLog-Tags-%E8%AF%B4%E6%98%8E/)

我们只使用到了 am_crash 的event log，用来获取应用崩溃信息, 示例：

am_crash(  519): [10992,0,com.svox.pico,8961605, java.lang.NullPointerException,Attempt to invoke virtual  method 'java.lang.String android.os.Bundle.getString(java.lang.String)' on a null object reference, GetSampleText.java,40]

### 模糊测试：

涉及文档都在该目录下：

* /IntentFuzzer/src/com/android/intentfuzzer/fuzz/BasicFuzzIntent.java
* /IntentFuzzer/src/com/android/intentfuzzer/fuzz/FuzzIntentFatory.java

核心代码：

```java
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
```

每次测试单个组件，都会进行以上intent的发送

License
===================
MIT License
