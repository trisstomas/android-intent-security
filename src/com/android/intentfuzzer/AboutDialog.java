package com.android.intentfuzzer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutDialog extends Dialog {

	private Context context;

	public AboutDialog(Context context) {
		super(context);
		this.context = context;
	}

	public AboutDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);

	}

}
