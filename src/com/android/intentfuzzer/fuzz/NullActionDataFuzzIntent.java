package com.android.intentfuzzer.fuzz;

public class NullActionDataFuzzIntent extends BasicFuzzIntent {

	@Override
	protected void genereateFuzz() {
		this.setDataAndType(SAMPLE_URI, SAMPLE_MIME_TYPE);
		this.addCategory(SAMPLE_CATEGORY);
	}

}
