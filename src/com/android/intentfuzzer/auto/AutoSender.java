package com.android.intentfuzzer.auto;

public interface AutoSender<T> {

	void send(T fuzzIntent);

}
