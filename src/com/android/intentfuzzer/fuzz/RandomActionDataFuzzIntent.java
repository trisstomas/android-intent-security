package com.android.intentfuzzer.fuzz;

public class RandomActionDataFuzzIntent extends BasicFuzzIntent {

	@Override
	protected void genereateFuzz() {
		setActionRandomly();
		this.setDataAndType(SAMPLE_URI, SAMPLE_MIME_TYPE);
		this.addCategory(SAMPLE_CATEGORY);
	}

}
