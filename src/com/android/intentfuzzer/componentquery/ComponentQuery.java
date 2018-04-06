package com.android.intentfuzzer.componentquery;

import java.util.List;
import java.util.Map;

public interface ComponentQuery {

	int TYPE_ALL = 1;
	int TYPE_ACTIVITY = 2;
	int TYPE_SERVICE = 3;
	int TYPE_BROADCAST = 4;
	int TYPE_PROVIDER = 5;
	
	Map<Integer, List> query(int type); 
	
}
