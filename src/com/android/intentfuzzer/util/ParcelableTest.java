package com.android.intentfuzzer.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTest implements Parcelable {
	
	int intValue;
	String stringValue;
	
	public ParcelableTest() {
		
	}
	
	public ParcelableTest(Parcel in) {
		this.intValue = in.readInt();
		this.stringValue = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(intValue);
		dest.writeString(stringValue);
	}
	
	public static final Parcelable.Creator<ParcelableTest> CREATOR = new Parcelable.Creator<ParcelableTest>() {

		@Override
		public ParcelableTest createFromParcel(Parcel source) {
			return new ParcelableTest(source);
		}

		@Override
		public ParcelableTest[] newArray(int size) {
			return null;
		}
	};

}
