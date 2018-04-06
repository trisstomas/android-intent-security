package com.android.intentfuzzer.fuzz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.android.intentfuzzer.util.ParcelableTest;
import com.android.intentfuzzer.util.SerializableTest;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;

public abstract class BasicFuzzIntent extends Intent {
	
	// https://developer.android.com/guide/components/intents-filters.html
	
	protected static final String SAMPLE_ACTION = "android.intent.action.SEND";
	
	protected static final Uri SAMPLE_URI = Uri.parse("http://www.sample.com/sample.jpg");
	
	protected static final String SAMPLE_MIME_TYPE = "text/plain";
	
	protected static final String SAMPLE_CATEGORY = "android.intent.category.BROWSABLE";
	
	protected static final List<Uri> SAMPLE_URIS = new ArrayList<Uri>();
	
	protected static final List<String> SAMPLE_ACTIONS = new ArrayList<String>();
	
	protected static final List SAMPLE_EXTRAS = new ArrayList();
	
	private static final String EXTRA_NAME = "extra";
	
	static {
		SAMPLE_URIS.add(Uri.parse("geo:38.899533,-77.036476"));
		SAMPLE_URIS.add(Uri.parse("http://www.google.com"));
		SAMPLE_URIS.add(Uri.parse("http://maps.google.com/maps?f=dsaddr=startLat%20startLng&daddr=endLat%20endLng&hl=en"));
		SAMPLE_URIS.add(Uri.parse("tel:13151617189"));
		SAMPLE_URIS.add(Uri.parse("smsto:0800000123"));
		SAMPLE_URIS.add(Uri.parse("content://media/external/images/media/23"));
		SAMPLE_URIS.add(Uri.parse("mailto:sample@gmail.com"));
		SAMPLE_URIS.add(Uri.parse("file:///sdcard/song.mp3"));
		SAMPLE_URIS.add(Uri.parse("market://details?id=123"));
		SAMPLE_URIS.add(Uri.parse("market://search?q=pname:pkg_name"));
		
		SAMPLE_ACTIONS.add("android.intent.action.ANSWER");
		SAMPLE_ACTIONS.add("android.intent.action.DIAL");
		SAMPLE_ACTIONS.add("android.intent.action.INSERT_OR_EDIT");
		SAMPLE_ACTIONS.add("android.intent.action.SET_WALLPAPER");
		SAMPLE_ACTIONS.add("android.intent.action.VOICE_COMMAND");
		SAMPLE_ACTIONS.add("android.settings.INTERNAL_STORAGE_SETTINGS");
		SAMPLE_ACTIONS.add("android.settings.NETWORK_OPERATOR_SETTINGS");
		SAMPLE_ACTIONS.add("android.settings.USER_DICTIONARY_SETTINGS");
		
		SAMPLE_EXTRAS.add(new SerializableTest());
		SAMPLE_EXTRAS.add("com.android.intentfuzzer.SAMPLE_EXTRA");
		SAMPLE_EXTRAS.add(new ParcelableTest());
		
	}
	
	public BasicFuzzIntent() {
		setExtraRandomly();
		genereateFuzz();
	}

	protected abstract void genereateFuzz();
	
	protected void setDataAndTypeRandomly() {
		Random generator = new Random();
		Uri uri = SAMPLE_URIS.get(generator.nextInt(SAMPLE_URIS.size()));
		this.setData(uri);
	}
	
	protected void setActionRandomly() {
		Random generator = new Random();
		String action = SAMPLE_ACTIONS.get(generator.nextInt(SAMPLE_ACTIONS.size()));
		this.setAction(action);
	}
	
	protected void setExtraRandomly() {
		Random generator = new Random();
		Object extra = SAMPLE_EXTRAS.get(generator.nextInt(SAMPLE_EXTRAS.size()));
		
		if (extra instanceof Serializable) {
			this.putExtra(EXTRA_NAME, (Serializable) extra);
		} else if (extra instanceof String) {
			this.putExtra(EXTRA_NAME, (String) extra);
		} else if (extra instanceof Parcelable) {
			this.putExtra(EXTRA_NAME, (Parcelable) extra);
		}
		
	}
	
}
