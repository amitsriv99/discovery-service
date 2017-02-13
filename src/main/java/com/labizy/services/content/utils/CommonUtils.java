package com.labizy.services.content.utils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.labizy.services.content.beans.PropertiesBean;
import com.labizy.services.content.beans.SupportedEnvironsBean;
import com.labizy.services.content.builder.PropertiesBuilder;
import com.labizy.services.content.exceptions.EnvironNotDefPropertiesBuilderException;

public class CommonUtils {
	private static Logger logger = LoggerFactory.getLogger("com.labizy.services.content.AppLogger");
	
	private static Date infinityDate = null;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");

	static{
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
        	infinityDate = formatter.parse("31-Dec-2049");
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Unable to initialize Infinity.. {}", e.toString());
        }
	}
	
	private PropertiesBean commonProperties;
	private List<Integer> listOfNumbers = null;
	private long seed;

	public void setCommonProperties(PropertiesBean commonProperties) {
		this.commonProperties = commonProperties;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public final long getInfinityTimestamp(){
		if(logger.isInfoEnabled()){
			logger.info("Infinity is set to " + infinityDate.toString());
		}
        
		return infinityDate.getTime();
	}
	
	public final String getCurrentTimestampAsString(){
		return simpleDateTimeFormat.format(new java.util.Date(System.currentTimeMillis()));
	}
	
	public final String getTimestampAsDateString(java.sql.Timestamp timestamp, boolean onlyDatePart){
		if(timestamp == null){
			return null;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		
		return ((onlyDatePart) ? simpleDateFormat.format(calendar.getTime()) : simpleDateTimeFormat.format(calendar.getTime()));
	}
	
	public final java.sql.Timestamp getCurrentDateTimeAsSqlTimestamp(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		return new java.sql.Timestamp(calendar.getTimeInMillis());
	}

	public final String getUniqueGeneratedId(String prefix, String suffix){
		if(logger.isDebugEnabled()){
			logger.debug("Inside UniqueIdGenerator.getUniqueGeneratedId() method..");
		}
		
		if(listOfNumbers == null){
			listOfNumbers = new ArrayList<Integer>();  
			
	        for (int i = 1000; i <= 9999; i++) {
	        	listOfNumbers.add(new Integer(i));
	        }
		}
		
        Collections.shuffle(listOfNumbers);
        
        int lowerIndex = 0;
        int upperIndex = (listOfNumbers.size() - 1);
        int randomIndex = ThreadLocalRandom.current().nextInt(lowerIndex, upperIndex);
        
        int randomNumber = -1;
        try{
        	randomNumber = listOfNumbers.get(randomIndex);
        }catch(RuntimeException e){
        	//Do nothing..
        } 
        
        StringBuffer buffer = new StringBuffer();
        
        if(! StringUtils.isEmpty(prefix)){
        	buffer.append(prefix).append("-");
        }
        
       	buffer.append(System.currentTimeMillis()).append("-").append(randomIndex).append("-").append(randomNumber);

       	if(! StringUtils.isEmpty(suffix)){
        	buffer.append("-").append(suffix);
        }
        
        return buffer.toString();
	}

	public final String getMessageFromTemplate(String template, String[] placeHolderValues){
		MessageFormat messageFormat = new MessageFormat(template);
		String message = messageFormat.format(placeHolderValues);
		
		return message;
	} 
	
	public final String getEnviron() throws EnvironNotDefPropertiesBuilderException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside {} method", "CommonUtils.getEnviron()");
		}
		
		String environ = System.getProperty(commonProperties.getEnvironSystemPropertyName());
		if(logger.isInfoEnabled()){
			logger.info("***** Environment : {} *****", environ);
		}
		
		if((environ == null) || ("".equals(environ.trim())) || (!SupportedEnvironsBean.isEnvironSupported(environ))){
			throw new EnvironNotDefPropertiesBuilderException("System variable '" + commonProperties.getEnvironSystemPropertyName() + "' is not set to point to one of " + SupportedEnvironsBean.getSupportedEnvirons() + ". Please set -D" + commonProperties.getEnvironSystemPropertyName() + "=local|ppe|test|lnp|prod");
		}
		
		return environ;
	} 
	
	public static void main (String[] args){
	    System.setProperty("environ", "local");

		PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
		
		PropertiesBean commonProperties = new PropertiesBean();
		Set<String> supportedEnvirons = new HashSet<String>();
		supportedEnvirons.add("local");
		supportedEnvirons.add("prod");
		supportedEnvirons.add("ppe");
		commonProperties.setSupportedEnvirons(supportedEnvirons);
		commonProperties.setEnvironSystemPropertyName("environ");
		propertiesBuilder.setCommonProperties(commonProperties);
		
		CommonUtils commonUtils = new CommonUtils();
		propertiesBuilder.setCommonUtils(commonUtils);
		commonUtils.setCommonProperties(commonProperties);

		commonUtils.setSeed(1L);
		
		String uniqueId = commonUtils.getUniqueGeneratedId("LAB", "");
		System.out.println("Unique Id : " + uniqueId);
	}	
}