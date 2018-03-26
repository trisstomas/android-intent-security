package com.android.intentfuzzer.fuzz;

import android.content.Intent;

public abstract class BasicFuzzIntent extends Intent {
	
	public BasicFuzzIntent() {
		genereateFuzz();
	}

	protected abstract void genereateFuzz();
	
}
