package com.labizy.services.content.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");

	private Map<String, String> defaultMap;
	public void setDefaultMap(Map<String, String> defaultMap) {
		this.defaultMap = defaultMap;
	}

	public static String DefaultValue;
	private static synchronized void setDefaultValue(String value){
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "Constants.setDefault()");
		}
		
		DefaultValue = value;
	}
	
	public static final String OFFSET_QUERY_PARAM = "offset";
	public static final String LIMIT_QUERY_PARAM = "limit";
	public static final String SORT_BY_QUERY_PARAM = "sortBy";
	
	public static final String LAB_TESTS_CACHE_KEY_TYPE = "LAB_TESTS_CACHE";
	public static final String LAB_TEST_CACHE_KEY_TYPE = "LAB_TEST_CACHE";
	
	public static final String LABS_CACHE_KEY_TYPE = "LABS_CACHE";
	public static final String LAB_CACHE_KEY_TYPE = "LAB_CACHE";
}
