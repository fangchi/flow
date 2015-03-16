package com.whty.flow.persistence.storage;

import java.util.HashMap;
import java.util.Map;

public class RAMStorage {

	private static Map<String, String> storage = new HashMap<String, String>();
	
	public static void put(String key,String val){
		storage.put(key, val);
	}
	
	public static String get(String key){
		return storage.get(key);
	}
}
