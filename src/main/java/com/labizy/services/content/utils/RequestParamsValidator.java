package com.labizy.services.content.utils;

import org.springframework.util.StringUtils;

public class RequestParamsValidator {

	public boolean validateBoolean(String paramName, String paramValue){
		boolean validatedParamValue = false;
		
		if(Constants.IS_LENIENT_PARAM.equalsIgnoreCase(paramName)){
			try{
				validatedParamValue = Boolean.parseBoolean(paramValue);
			}catch(Exception e){
				validatedParamValue = true;
			}
		}
		
		return validatedParamValue;
	}
	
	public float validateGeoLocations(String paramName, String paramValue){
		float validatedParamValue = -1;
		
		if( (Constants.LATITUDE_PARAM.equalsIgnoreCase(paramName)) || (Constants.LONGITUDE_PARAM.equalsIgnoreCase(paramName)) || (Constants.RADIUS_PARAM.equalsIgnoreCase(paramName))){
			try{
				validatedParamValue = Float.parseFloat(paramValue);
			} catch(Exception e){
				validatedParamValue = -1;
			}
		}
		
		return validatedParamValue;
	}
	
	public String validate(String paramName, String paramValue){
		String validatedParamValue = null;
		
		if(Constants.LIMIT_QUERY_PARAM.equals(paramName)){
			int paramValueInt = 20;
			if(! StringUtils.isEmpty(paramValue)){
				try{
					paramValueInt = Integer.parseInt(paramName);
				}catch(Exception e){
					paramValueInt = 20;
				}
			}
			
			if(paramValueInt == 0){
				paramValueInt = 20;
			}else if(paramValueInt < 0){
				paramValueInt = -1;
			} else{
				//Do nothing..
			}
			
			validatedParamValue = Integer.toString(paramValueInt);
		}else if(Constants.OFFSET_QUERY_PARAM.equals(paramName)){
			int paramValueInt = 0;
			if(! StringUtils.isEmpty(paramValue)){
				try{
					paramValueInt = Integer.parseInt(paramName);
				}catch(Exception e){
					paramValueInt = 0;
				}
			}

			validatedParamValue = Integer.toString(Math.abs(paramValueInt));
		}else if(Constants.SORT_BY_QUERY_PARAM.equals(paramName)){
			if(! StringUtils.isEmpty(paramValue)){
				validatedParamValue = paramValue;
			}else{
				validatedParamValue = null;
			}
		}else{
			validatedParamValue = null;
		}
		
		return validatedParamValue;
	}
	
	public String[] validate(String paramName, String[] paramValues){
		String[] validatedParamValues = null;
		
		return validatedParamValues;
	}
}
