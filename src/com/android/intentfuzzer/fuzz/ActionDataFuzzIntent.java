package com.android.intentfuzzer.fuzz;

public class ActionDataFuzzIntent extends BasicFuzzIntent {

	@Override
	protected void genereateFuzz() {
		this.setAction(SAMPLE_ACTION);
		this.setDataAndType(SAMPLE_URI, SAMPLE_MIME_TYPE);
		this.addCategory(SAMPLE_CATEGORY);
	}

}
