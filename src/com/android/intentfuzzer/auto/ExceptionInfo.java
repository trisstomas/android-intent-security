package com.android.intentfuzzer.auto;

import android.content.Context;

public class ExceptionInfo {
	
	private String mCrashPackageName;
	
	private String mExceptionName;
	
	private String mExceptionMessage;
	
	private long mCrashTime;

	public ExceptionInfo(String mCrashPackageName, String mExceptionName,
			String mExceptionMessage, long mCrashTime) {
		super();
		this.mCrashPackageName = mCrashPackageName;
		this.mExceptionName = mExceptionName;
		this.mExceptionMessage = mExceptionMessage;
		this.mCrashTime = mCrashTime;
	}
	
	public String getmCrashPackageName() {
		return mCrashPackageName;
	}

	public void setmCrashPackageName(String mCrashPackageName) {
		this.mCrashPackageName = mCrashPackageName;
	}

	public String getmExceptionName() {
		return mExceptionName;
	}

	public void setmExceptionName(String mExceptionName) {
		this.mExceptionName = mExceptionName;
	}

	public String getmExceptionMessage() {
		return mExceptionMessage;
	}

	public void setmExceptionMessage(String mExceptionMessage) {
		this.mExceptionMessage = mExceptionMessage;
	}

	public long getmCrashTime() {
		return mCrashTime;
	}

	public void setmCrashTime(long mCrashTime) {
		this.mCrashTime = mCrashTime;
	}

	@Override
	public String toString() {
		return "ExceptionInfo [mCrashPackageName=" + mCrashPackageName
				+ ", mExceptionName=" + mExceptionName + ", mExceptionMessage="
				+ mExceptionMessage + ", mCrashTime=" + mCrashTime + "]";
	}

}
