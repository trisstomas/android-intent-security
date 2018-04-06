package com.android.intentfuzzer.fuzz;

public class ActionRandomDataFuzzIntent extends BasicFuzzIntent {

	@Override
	protected void genereateFuzz() {
		this.setAction(SAMPLE_ACTION);
		setDataAndTypeRandomly();
	}

}
