package com.android.intentfuzzer.auto.sender;

import android.app.Activity;

public interface AutoSender<T> {

	void send(Activity activity, T fuzzIntent);

}
