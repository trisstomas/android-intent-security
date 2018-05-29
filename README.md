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

- [x] PMS.getInstalledPackages
- [] 支持动态Receiver获取

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

## 应用preload功能

使用monkey进行实现

测试动态Receiver需要应用都被加载起来，以确保动态广播被注册到系统，这样才能获取到动态广播的信息

## 支持动态Receiver测试

因为动态Receiver是在各个应用运行状态下，可选择性的向AMS系统服务动态的注册与反注册，以实现监听与取消监听整个系统中的对应广播。与其它在Manifest.xml文件中注册的组件不同，动态注册的广播信息是不能通过PackageManager.getInstalledPackages进行获取的。基于此，很多测试工具并不支持动态广播的测试。

也有些研究结果虽然可以进行分析，但是却需要通过大量反编译第三方应用代码以获取动态广播类名信息，这种方法可以对技术水平不高的小众应用生效，然而这对于主流TOP应用基本是不可行的，因为主流APP多多少少会对应用做加固、混淆等保护措施，以避免因应用安全导致的损失。

因为AOSP其开源性，我们可以轻松获取到安卓系统的源码并进行修改与编译，对于模糊测试，我们可以从系统的角度下手，对测试APP暴露定制的数据接口，使得APP可以轻松、高效的获取到运行在系统中的动态Receiver信息

### 实现(1)

修改IntentReceiver aidl文件，这里需要Binder的一些基础介绍

### 实现(2)

在System端暴露数据接口

### 实现(3)

通过反射调用接口并获取动态广播信息


## 数据来源

### 使用scrapy爬取、下载、安装TOP应用

APP来源: 小米商店 http://app.mi.com/

使用步骤：

1. brew install python3 // 安装python3
2. pip3 install Scrapy // 安装Scrapy
3. 下载项目
4. cd <项目目录>
5. scrapy crawl xiaomi // 启动爬虫并自动下载、安装应用

### 被测试应用信息

|序号|类型|个数|代表应用|
|---|---|---|---|
|1|聊天社交|17|QQ|
|2|影音视听|16|优酷|
|3|实用工具|13|拉勾|
|4|效率办公|12|有道云笔记|
|5|金融理财|9|支付宝|
|6|时尚购物|9|淘宝|
|7|医疗健康|8|美柚|
|8|新闻资讯|7|今日头条|
|9|体育运动|5|悦跑圈|
|10|学习教育|3|百词斩|

共100款应用

## 测试结果

### 以异常类型为维度

|序号|异常类型|出现异常次数|
|---|---|---|
|1|||
|2|||
|3|||
|4|||
|5|||
|6|||
|7|||
|8|||
|9|||
|10|||


### 以应用为维度 (TOP10)

|序号|应用包名|出现异常次数|
|---|---|---|
|1|||
|2|||
|3|||
|4|||
|5|||
|6|||
|7|||
|8|||
|9|||
|10|||

### 以组件异常为维度

|序号|组件类型|出现异常次数|
|---|---|---|
|1|Activity||
|2|Service|
|3|静态Receiver||
|4|动态Receiver||
|5|Provider||

### 对比其它测试应用 (功能维度)

|应用|Activity|Service|静态Receiver|动态Receiver|Provider|批量测试|
|---|---|----|----|---|---|---|---|
|IntentFuzzer||||||
|IntentFuzzer加强|||||





License
===================
MIT License
