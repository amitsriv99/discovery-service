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
	
	public static final String IS_LENIENT_PARAM = "isLenient";
	public static final String INCLUDE_PRODUCTS_PARAM = "incldPrds";
	public static final String INCLUDE_LABS_PARAM = "incldLabs";
	
	public static final String PRODUCT_ID_PARAM = "prdId";
	public static final String PRODUCT_NAME_PARAM = "prdName";
	public static final String PRODUCT_TYPE_PARAM = "prdType";
	public static final String PRODUCT_SUB_TYPE_PARAM = "prdSubType";
	public static final String PRODUCT_SEARCH_TAGS_PARAM = "prdSearchTags";
	
	public static final String LAB_ID_PARAM = "labId";
	public static final String LAB_NAME_PARAM = "labName";
	public static final String LAB_GROUP_NAME_PARAM = "labGrpName";
	public static final String LAB_LOCALITY_NAME_PARAM = "locality";
	public static final String LAB_CITY_TOWN_VILLAGE_PARAM = "city";
	public static final String LAB_STATE_PARAM = "state";
	public static final String LAB_COUNTRY_PARAM = "cntry";
	
	public static final String LATITUDE_PARAM = "lat";
	public static final String LONGITUDE_PARAM = "lng";
	public static final String RADIUS_PARAM = "rad";
	public static final String RADIUS_UOM_PARAM = "radUom";
	
	public static final String LAB_TESTS_CACHE_KEY_TYPE = "LAB_TESTS_CACHE";
	public static final String LAB_TEST_CACHE_KEY_TYPE = "LAB_TEST_CACHE";
	
	public static final String LABS_CACHE_KEY_TYPE = "LABS_CACHE";
	public static final String LAB_CACHE_KEY_TYPE = "LAB_CACHE";
	public static final String PRICE_PROMO_CACHE_KEY_TYPE = "PRICE_PROMO_CACHE";
}
