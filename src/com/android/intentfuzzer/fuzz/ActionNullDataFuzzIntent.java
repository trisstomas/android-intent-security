package com.android.intentfuzzer.fuzz;

public class ActionNullDataFuzzIntent extends BasicFuzzIntent {

	@Override
	protected void genereateFuzz() {
		this.setAction(SAMPLE_ACTION);
	}

}
