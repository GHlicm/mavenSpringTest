package com.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {
	private Map<String, Object> cache = new HashMap<String, Object>();
	private Map<String, Long> cacheConf = new HashMap<String, Long>();
	
	/*********************增删改查***************************/
	
	public boolean addToCache(String key, Object value){
		boolean flag = false;
		cache.put(key, value);
		cacheConf.put(key, new Date().getTime());
		flag = true;
		return flag;
	}
	
	public Object getValueFromCache(String key){
		Object object = cache.get(key);
		return object;
	}
	
	public boolean removeFromCache(String key){
		boolean flag = false;
		cache.remove(key);
		cacheConf.remove(key);
		flag = true;
		return flag;
	}
	
	/*********************缓存定时清理***************************/
	
}
