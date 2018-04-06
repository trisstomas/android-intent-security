package com.android.intentfuzzer.auto;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;

import com.android.intentfuzzer.db.DBHelper;
import com.android.intentfuzzer.util.Utils;

public class LogObserver {
	
	private static LogObserver sInstance;
	
	private LogFetcher mLogFetcher;
	
	private static final String[] LOGCAT_COMMAND = {"sh", "-c", "logcat -b events"};
	
	// exmaple:
	// I/am_crash(  519): [10992,0,com.svox.pico,8961605,
	// java.lang.NullPointerException,Attempt to invoke virtual 
	// method 'java.lang.String android.os.Bundle.getString(java.lang.String)' on a null object reference,
	// GetSampleText.java,40]
	// 
	// group(0): processName
	// group(1): exceptionName
	// group(2): exceptionMessage
	private static final String AM_CRASH_REGEX_STRING = "am_crash.*,.*,(.*),.*,(.*),(.*),.*,.*";
	
	private static Pattern sPattern = Pattern.compile(AM_CRASH_REGEX_STRING);
	
	private static boolean sStarted;
	
	private DBHelper mDBHelper;
	
	public static synchronized LogObserver getInstance() {
		if (sInstance == null) {
			sInstance = new LogObserver();
		}
		return sInstance;
	}
	
	public void init(Context context) {
		mDBHelper = new DBHelper(context);
	}
	
	private LogObserver() {
		mLogFetcher = new LogFetcher();
	}
	
	public void start() {
		if (sStarted) {
			return ;
		}
		sStarted = true;
		mLogFetcher.start();
	}
	
	public void stop() {
		sStarted = false;
		mLogFetcher.interrupt();
	}
	
	private class LogFetcher extends Thread {
		
		private Process mProcess;
		
		
		@Override
		public void run() {
			try {
				Runtime.getRuntime().exec("logcat -c").waitFor();
				mProcess = Runtime.getRuntime().exec(LOGCAT_COMMAND);
				Utils.d(LogObserver.class, "init LogFetcher success");
			} catch (InterruptedException e) {
				e.printStackTrace();
				Utils.d(LogFetcher.class, e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Utils.d(LogFetcher.class, e.toString());
			}
			
			DataInputStream dis = null;
			String line = null;
			try {
				dis = new DataInputStream(mProcess.getInputStream());
			} catch(Exception e){
				Utils.d(LogFetcher.class, "exception in opening input stream");
			}
			
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
			
		}
	}
	
	private ExceptionInfo parseAndCreateExceptionInfo(String line) {
		Matcher matcher = sPattern.matcher(line);
		ExceptionInfo exceptionInfo = null;
		if (matcher.find()) {
			Utils.d(LogObserver.class, "match:" + line);
			String processName = matcher.group(1);
			String exceptionName = matcher.group(2);
			String exceptionMessage = matcher.group(3);
			exceptionInfo = new ExceptionInfo(processName, exceptionName, exceptionMessage, System.currentTimeMillis());
		} else {
//			Utils.d(LogObserver.class, "not match:" + line);
		}
		return exceptionInfo;
	}

}
